package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public String name() {
        return "Dépose spot sur la tablette action";
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean isValid() {
        return rs.getNbPied() == 3 && rs.isInitialCollectFinished() && !ioService.piedDroit() && !ioService.piedGauche();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(400, 800);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1650, 1300);
                mv.gotoOrientationDeg(0);
                servosService.leveGobelets();
                mv.setVitesse(200, 800);
                mv.gotoPointMM(1780, 1300);
            } else {
                // TODO : Vert
            }

            rs.disableAscenseur();
            servosService.deposeColonneSurTablette();
            mv.reculeMM(200);
            rs.resetNbPied();
            rs.enableAscenseur();
            servosService.fermeGuide();
            rs.setBalleDansAscenseur(false);
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
        completed = true;
    }
}
