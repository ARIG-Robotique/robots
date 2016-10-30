package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 11/05/15.
 */
@Slf4j
//@Component
public class PriseBalleDepart implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Prise balle zone départ";
    }

    @Override
    public int order() {
        // Histoire que ce soit prioritaire quand même vis a vis des action de collecte
        return 500;
    }

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }

        return rs.getNbPied() == 0 && rs.getIndexZoneDeposeSallePrincipale() == 0
                && !ioService.piedDroit() && !ioService.piedGauche() && !rs.isBalleDansAscenseur();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1000, 500);
                rs.disableAvoidance();
                servosService.ouvrePince();
                servosService.leveGobelets();
                mv.gotoOrientationDeg(-90);
                try {
                    mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);
                    rs.enableCalageBordure();
                    mv.gotoPointMM(1000, 175);
                    mv.avanceMMSansAngle(30);
                } catch (ObstacleFoundException e) {
                    log.info("Caler sur bordure");
                } finally {
                    rs.disableCalageBordure();
                }
            } else {
                mv.pathTo(1000, 3000 - 500);
                rs.disableAvoidance();
                servosService.ouvrePince();
                servosService.leveGobelets();
                mv.gotoOrientationDeg(90);
                try {
                    mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);
                    rs.enableCalageBordure();
                    mv.gotoPointMM(1000, 3000 - 175);
                    mv.avanceMMSansAngle(30);
                } catch (ObstacleFoundException e) {
                    log.info("Caler sur bordure");
                } finally {
                    rs.disableCalageBordure();
                }
            }

            servosService.priseBalleDansAscenseur();
            rs.setBalleDansAscenseur(true);
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.reculeMM(70);

            boolean gbDroit = ioService.gobeletDroit();
            boolean gbGauche = ioService.gobeletGauche();
            if (gbDroit || gbGauche) {
                rs.getYZoneDeposePrincipale();
                if (ioService.gobeletDroit()) {
                    servosService.deposeProduitDroit();
                } else if (ioService.gobeletGauche()) {
                    servosService.deposeProduitGauche();
                }
                mv.reculeMM(200);
                servosService.priseProduitDroit();
                servosService.priseProduitGauche();
            }

            mv.tourneDeg(180);
            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
            rs.enableAvoidance();
        }
    }
}