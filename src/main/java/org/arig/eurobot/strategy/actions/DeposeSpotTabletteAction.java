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
public class DeposeSpotTabletteAction implements IAction {

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
        return "Dépose spot sur la tablette";
    }

    @Override
    public int order() {
        // Il faut la poser dès que possible.
        return 1000;
    }

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }

        return rs.isInitialCollectFinished() && rs.getNbPied() == 3
                && !ioService.piedDroit() && !ioService.piedGauche();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1650, 1260);
                mv.gotoOrientationDeg(0);
                rs.disableAvoidance();
                servosService.leveGobelets();
                try {
                    mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                    rs.enableCalageBordure();
                    mv.gotoPointMM(1770, 1260);
                    mv.avanceMMSansAngle(30);
                } catch (ObstacleFoundException e) {
                    log.info("Caler sur bordure");
                } finally {
                    rs.disableCalageBordure();
                }
            } else {
                mv.pathTo(1650, 3000 - 1260);
                mv.gotoOrientationDeg(0);
                rs.disableAvoidance();
                servosService.leveGobelets();
                try {
                    mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                    rs.enableCalageBordure();
                    mv.gotoPointMM(1770, 3000 - 1260);
                    mv.avanceMMSansAngle(30);
                } catch (ObstacleFoundException e) {
                    log.info("Caler sur bordure");
                } finally {
                    rs.disableCalageBordure();
                }
            }

            rs.disableAscenseur();
            servosService.deposeColonneSurTablette();
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            mv.reculeMM(200);
            rs.resetNbPied();
            servosService.fermeGuide();
            rs.setBalleDansAscenseur(false);
            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            rs.enableAscenseur();
            rs.enableAvoidance();
        }
    }
}
