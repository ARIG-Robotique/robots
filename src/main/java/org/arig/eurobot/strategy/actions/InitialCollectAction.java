package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by gdepuille on 06/05/15.
 */
@Slf4j
@Component
public class InitialCollectAction implements IAction, InitializingBean {

    @Autowired
    private Environment env;

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = true; // Toujours terminé quoi qu'il se passe

    private boolean collectGobeletInitiale = false;

    @Override
    public void afterPropertiesSet() throws Exception {
        collectGobeletInitiale = env.getProperty("strategy.collect.initial.gobelet", Boolean.class, false);
    }

    @Override
    public String name() {
        return String.format("Collecte initiale des pieds (et gobelet commun : %s) devant la zone tablette", Boolean.toString(collectGobeletInitiale));
    }

    @Override
    public int order() {
        // Top priorité sur l'action de démarrage
        return 1000;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            if (rs.getTeam() == Team.JAUNE) {
                // Pied 1
                mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1355, 870);
                rs.setPied1Recupere(true);

                // Pied 2
                mv.setVitesse(IConstantesRobot.vitesseSuperLente, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1400, 1300);
                rs.setPied2Recupere(true);

                // Gobelet 1 (commun)
                if (collectGobeletInitiale) {
                    log.info("Collecte du gobelet commun pendant la collecte initiale activé");
                    try {
                        mv.setVitesse(IConstantesRobot.vitesseSuperLente, IConstantesRobot.vitesseOrientation);
                        mv.gotoPointMM(1250, 1500);
                        mv.gotoOrientationDeg(0);
                        servosService.ouvrePriseGauche();
                        mv.gotoPointMM(1550, 1385);
                        servosService.priseProduitGauche();
                        rs.setGobeletCentraleRecupere(true);
                    } catch (ObstacleFoundException e) {
                        log.warn("Impossible de récupérer le gobelet commun.");
                        rs.setGobeletCentraleRecupere(false);
                        servosService.fermeProduitGauche();
                    }
                } else {
                    log.info("Collecte du gobelet commun pendant la collecte initiale désactivé");
                }

                // Pied 3
                mv.setVitesse(IConstantesRobot.vitesseSuperLente, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1490, 1100);
                mv.alignFrontTo(1770, 1100);
                mv.gotoPointMM(1650, 1100);
                rs.setPied3Recupere(true);
                rs.setInitialCollectFinished(true);

            } else {
                // Pied 1
                mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1355, 3000 - 870);
                rs.setPied1Recupere(true);

                // Pied 2
                mv.setVitesse(IConstantesRobot.vitesseSuperLente, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1400, 3000 - 1300);
                rs.setPied2Recupere(true);

                // Gobelet 1 (commun)
                if (collectGobeletInitiale) {
                    log.info("Collecte du gobelet commun pendant la collecte initiale activé");
                    try {
                        mv.setVitesse(IConstantesRobot.vitesseSuperLente, IConstantesRobot.vitesseOrientation);
                        mv.gotoPointMM(1250, 1500);
                        mv.gotoOrientationDeg(0);
                        servosService.ouvrePriseDroite();
                        mv.gotoPointMM(1550, 3000 - 1385);
                        servosService.priseProduitDroit();
                        rs.setGobeletCentraleRecupere(true);
                    } catch (ObstacleFoundException e) {
                        log.warn("Impossible de récupérer le gobelet commun.");
                        rs.setGobeletCentraleRecupere(false);
                        servosService.fermeProduitDroit();
                    }
                } else {
                    log.info("Collecte du gobelet commun pendant la collecte initiale désactivé");
                }

                // Pied 3
                mv.setVitesse(IConstantesRobot.vitesseSuperLente, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1490, 3000 - 1100);
                mv.alignFrontTo(1770, 3000 - 1100);
                mv.gotoPointMM(1650, 3000 - 1100);
                rs.setPied3Recupere(true);
                rs.setInitialCollectFinished(true);
            }
        } catch (ObstacleFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
