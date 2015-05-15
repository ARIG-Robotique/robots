package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.IOService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by gdepuille on 11/05/15.
 */
@Slf4j
@Component
public class PriseBalleDepart implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private IOService ioService;

    @Autowired
    private SD21Servos servos;

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
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1000, 500);
                rs.disableAvoidance();
                servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
                servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
                servosService.leveGobelets();
                mv.gotoOrientationDeg(-90);
                try {
                    mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
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
                servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
                servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
                servosService.leveGobelets();
                mv.gotoOrientationDeg(90);
                try {
                    mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                    rs.enableCalageBordure();
                    mv.gotoPointMM(1000, 3000 - 175);
                    mv.avanceMMSansAngle(30);
                } catch (ObstacleFoundException e) {
                    log.info("Caler sur bordure");
                } finally {
                    rs.disableCalageBordure();
                }
            }

            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_BALLE);
            try { Thread.currentThread().sleep(400); } catch (InterruptedException e) { }
            servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT_BALLE);
            try { Thread.currentThread().sleep(500); } catch (InterruptedException e) { }
            rs.setBalleDansAscenseur(true);
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            mv.reculeMM(70);

            boolean gbDroit = ioService.gobeletDroit();
            boolean gbGauche = ioService.gobeletGauche();
            if (gbDroit || gbGauche) {
                rs.getYZoneDeposePrincipale();
                if (ioService.gobeletDroit()) {
                    servosService.deposeGobeletDroit();
                } else if (ioService.gobeletGauche()) {
                    servosService.deposeGobeletGauche();
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
