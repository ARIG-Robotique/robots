package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.IOService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by gdepuille on 06/05/15.
 */
@Slf4j
@Component
public class DeposeSpotSallePrincipaleAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IOService ioService;

    @Getter
    private boolean completed = false;

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Dépose spot dans la salle principale";
    }

    @Override
    public int order() {
        // Priorité de malade si la fin est proche
        if (rs.getElapsedTime() > 70000) {
            return 2000;
        }

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

        int nbPied = 4;
        if (rs.getElapsedTime() > 70000) {
            nbPied = 1;
        } else if (rs.getElapsedTime() > 60000) {
            nbPied = 2;
        } else if (rs.getElapsedTime() > 50000) {
            nbPied = 3;
        }

        return rs.getNbPied() >= nbPied;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1000, 600);
            } else {
                mv.pathTo(1000, 2400);
            }
            mv.alignFrontTo(1000, (rs.getTeam() == Team.JAUNE) ? 0 : 3000);

            boolean timeFinMatchProche = rs.getElapsedTime() > 70000;
            if (timeFinMatchProche) {
                // Fin de match proche on envoi la purée
                rs.disableAscenseur();
                servosService.deposeColonneAuSol();
                if (ioService.produitDroit()) {
                    servosService.deposeProduitDroitFinMatch();
                }
                if (ioService.produitGauche()) {
                    servosService.deposeProduitGaucheFinMatch();
                }
                mv.reculeMM(200);
                rs.resetNbPied();
                rs.setBalleDansAscenseur(false);
                mv.gotoOrientationDeg(rs.getTeam() == Team.JAUNE ? 90 : -90);

                completed = true;

                // <MODE_QUICK_AND_DIRTY>
                rs.setGobeletCentraleRecupere(true);
                rs.setGobeletClapJauneRecupere(true);
                rs.setGobeletClapVertRecupere(true);
                rs.setGobeletEscalierJauneRecupere(true);
                rs.setGobeletEscalierVertRecupere(true);

                rs.setPied1Recupere(true);
                rs.setPied2Recupere(true);
                rs.setPied3Recupere(true);
                rs.setPied4Recupere(true);
                rs.setPied5Recupere(true);
                rs.setPied6Recupere(true);
                rs.setPied7Recupere(true);
                rs.setPied8Recupere(true);
                // </MODE_QUICK_AND_DIRTY>

            } else {
                // Dépose normal
                rs.disableAvoidance();
                mv.gotoPointMM(1000, rs.getYZoneDeposePrincipale()); // Prend en compte la TEAM

                rs.disableAscenseur();
                if (rs.getNbPied() > 0) {
                    servosService.deposeColonneAuSol();
                    rs.resetNbPied();
                    rs.setBalleDansAscenseur(false);
                }
                if (ioService.produitDroit()) {
                    servosService.deposeProduitDroitFinMatch();
                }
                if (ioService.produitGauche()) {
                    servosService.deposeProduitGaucheFinMatch();
                }

                mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
                mv.reculeMM(200);
                mv.gotoOrientationDeg(rs.getTeam() == Team.JAUNE ? 90 : -90);
            }
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            rs.enableAscenseur();
            rs.enableAvoidance();
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
            servosService.fermeGuide();
        }
    }
}
