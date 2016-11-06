package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 06/05/15.
 */
@Slf4j
@Component
public class CollecteInitialeAction implements IAction, InitializingBean {

    @Autowired
    private Environment env;

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    /** The conv. */
    @Autowired
    private ConvertionRobotUnit conv;

    /** The position. */
    @Autowired
    @Qualifier("currentPosition")
    private Position position;

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
                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(1355, 870);
                rs.setPied1Recupere(true);

                // Pied 2
                mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(1400, 1300);
                rs.setPied2Recupere(true);

                // Gobelet 1 (commun)
                if (collectGobeletInitiale) {
                    log.info("Collecte du gobelet commun pendant la collecte initiale activé");
                    try {
                        mv.gotoPointMM(1250, 1500);
                        double r = Math.sqrt(Math.pow(1650 - 1250, 2));
                        double alpha = Math.asin(115 / r);

                        mv.alignFrontToAvecDecalage(1650, 1500, Math.toDegrees(-alpha));
                        servosService.ouvrePriseGauche();
                        mv.avanceMM(r * Math.cos(alpha) - 110);
                        servosService.priseProduitGauche();
                        servosService.priseProduitDroit();
                        rs.setGobeletCentraleRecupere(true);
                    } catch (ObstacleFoundException e) {
                        log.warn("Impossible de récupérer le gobelet commun.");
                        rs.setGobeletCentraleRecupere(false);
                        servosService.priseProduitGauche();
                    }
                } else {
                    log.info("Collecte du gobelet commun pendant la collecte initiale désactivé");
                }

                // Pied 3
                //mv.gotoPointMM(1490, 1100);
                mv.alignFrontTo(1770, 1100);
                Point ptFrom = new Point(conv.pulseToMm(position.getPt().getX()), conv.pulseToMm(position.getPt().getY()));
                Point ptTo = new Point(1770, 1100);
                double dist = Math.sqrt(Math.pow(ptTo.getX() - ptFrom.getX(), 2) + Math.pow(ptTo.getY() - ptFrom.getY(), 2)) - 115;
                mv.avanceMM(dist);
                rs.setPied3Recupere(true);
                rs.setInitialCollectFinished(true);

            } else {
                // Pied 1
                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(1355, 3000 - 870);
                rs.setPied1Recupere(true);

                // Pied 2
                mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(1400, 3000 - 1300);
                rs.setPied2Recupere(true);

                // Gobelet 1 (commun)
                if (collectGobeletInitiale) {
                    log.info("Collecte du gobelet commun pendant la collecte initiale activé");
                    try {
                        mv.gotoPointMM(1250, 1500);
                        double r = Math.sqrt(Math.pow(1650 - 1250, 2));
                        double alpha = Math.asin(115 / r);

                        mv.alignFrontToAvecDecalage(1650, 1500, Math.toDegrees(alpha));
                        servosService.ouvrePriseDroite();
                        mv.avanceMM(r * Math.cos(alpha) - 110);
                        servosService.priseProduitGauche();
                        servosService.priseProduitDroit();
                        rs.setGobeletCentraleRecupere(true);
                    } catch (ObstacleFoundException e) {
                        log.warn("Impossible de récupérer le gobelet commun.");
                        rs.setGobeletCentraleRecupere(false);
                        servosService.priseProduitDroit();
                        servosService.priseProduitGauche();
                    }
                } else {
                    log.info("Collecte du gobelet commun pendant la collecte initiale désactivé");
                }

                // Pied 3
                //mv.gotoPointMM(1490, 3000 - 1100);
                mv.alignFrontTo(1770, 3000 - 1100);
                Point ptFrom = new Point(conv.pulseToMm(position.getPt().getX()), conv.pulseToMm(position.getPt().getY()));
                Point ptTo = new Point(1770, 3000 - 1100);
                double dist = Math.sqrt(Math.pow(ptTo.getX() - ptFrom.getX(), 2) + Math.pow(ptTo.getY() - ptFrom.getY(), 2)) - 115;
                mv.avanceMM(dist);
                rs.setPied3Recupere(true);
                rs.setInitialCollectFinished(true);
            }
        } catch (ObstacleFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
