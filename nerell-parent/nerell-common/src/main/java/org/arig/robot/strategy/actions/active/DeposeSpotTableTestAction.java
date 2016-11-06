package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 06/11/16.
 */
@Slf4j
@Component
public class DeposeSpotTableTestAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Getter
    private boolean completed = false;

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Dépose spot dans la salle principale";
    }

    @Override
    public int order() {
        // Proportionnel aux nombre de pieds dans le robot
        return (2 * rs.getNbPied()) + ((rs.isBalleDansAscenseur()) ? 3 * rs.getNbPied() : 0)
                + (ioService.gobeletDroit() || ioService.gobeletGauche() ? 4 : 0)
                + (ioService.piedDroit() ? 2 : 0) + (ioService.piedGauche() ? 2 : 0);
    }

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }

        return rs.getNbPied() >= 0;
    }

    @Override
    public void execute() {
        boolean droite = false;
        boolean gauche = false;
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(500, 800);
            mv.gotoOrientationDeg(90);

            // Dépose normal
            rs.disableAvoidance();
            mv.gotoPointMM(500, rs.getYZoneDeposePrincipale());

            rs.disableAscenseur();
            if (rs.getNbPied() > 0) {
                servosService.deposeColonneAuSol();
                rs.resetNbPied();
                rs.setBalleDansAscenseur(false);
            }
            if (ioService.produitDroit()) {
                servosService.deposeProduitDroitFinMatch();
                droite = true;
            }
            if (ioService.produitGauche()) {
                servosService.deposeProduitGaucheFinMatch();
                gauche = true;
            }

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.reculeMM(200);
            mv.gotoOrientationDeg(0);

        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            DeposeSpotTableTestAction.log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(IConstantesNerellConfig.invalidActionTimeSecond);
        } finally {
            rs.enableAscenseur();
            rs.enableAvoidance();
            if (droite) {
                servosService.priseProduitDroit();
            }
            if (gauche) {
                servosService.priseProduitGauche();
            }
            servosService.fermeGuide();
        }
    }
}
