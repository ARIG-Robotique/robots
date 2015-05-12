package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
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
public class InitialCollectAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    private int step = 0;

    @Override
    public String name() {
        return "Collect initiale des objets";
    }

    @Override
    public int order() {
        return 1000;
    }

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public boolean isValid() {
        return validTime.isBefore(LocalDateTime.now());
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(200, 800);
            if (rs.getTeam() == Team.JAUNE) {
                if (step == 0) {
                    // Pied 1
                    mv.gotoPointMM(1355, 870);
                    step++;
                }
                if (step == 1) {
                    // Pied 2
                    mv.gotoPointMM(1400, 1300);
                    step++;
                }
                if (step == 2) {
                    // Gobelet 1 (commun)
                    try {
                        mv.gotoPointMM(1250, 1500);
                        mv.gotoOrientationDeg(0);
                        servosService.ouvrePriseGauche();
                        mv.gotoPointMM(1550, 1385);
                        servosService.priseProduitGauche();
                        rs.setGobeletCentraleRecupere(true);
                    } catch (ObstacleFoundException e) {
                        log.warn("Impossible de récupérer le gobelet commun.");
                        rs.setGobeletCentraleRecupere(false);
                    }
                    step++;
                }
                if (step == 3) {
                    // Pied 3
                    mv.pathTo(1500, 1100);
                    mv.alignFrontTo(1770, 1100);
                    mv.gotoPointMM(1650, 1100);
                    rs.setInitialCollectFinished(true);
                    completed = true;
                }
            } else {
                // TODO : Vert
            }
        } catch (AvoidingException | NoPathFoundException | ObstacleFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
        }
    }
}
