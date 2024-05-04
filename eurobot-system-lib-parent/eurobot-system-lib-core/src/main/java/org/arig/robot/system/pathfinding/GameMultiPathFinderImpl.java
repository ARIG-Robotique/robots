package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Plante;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Team;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

@Slf4j
public class GameMultiPathFinderImpl extends MultiPathFinderImpl {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Override
    public void setObstacles(final List<Shape> obstacles) {
        final int rayonRobotCm = 20;

        // zone des PAMI
        obstacles.add(new Rectangle(150 - 45 - rayonRobotCm, 200 - 15 - rayonRobotCm, 45 * 2 + rayonRobotCm * 2, 15 + rayonRobotCm));

        // zones adverses
        if (rs.team() == Team.BLEU) {
            // nord jaune
            obstacles.add(new Rectangle(255 - rayonRobotCm, 155 - rayonRobotCm, 45 + rayonRobotCm, 45 + rayonRobotCm));
            // milieu jaune
//            obstacles.add(tableUtils.createPolygonObstacle(new Point(225, 1000), 850));
        } else {
            // nord bleu
            obstacles.add(new Rectangle(0, 155 - rayonRobotCm, 45 + rayonRobotCm, 45 + rayonRobotCm));
            // milieu bleu
//            obstacles.add(tableUtils.createPolygonObstacle(new Point(2775, 1000), 850));
        }

        if (rs.getRemainingTime() > EurobotConfig.validRetourSiteDeChargeRemainingTimeNerell) {
            // ajout des plantes
            for (Plante plante : rs.plantes()) {
                if (plante.isBlocking()) {
                    obstacles.add(tableUtils.createPolygonObstacle(plante, EurobotConfig.PATHFINDER_FLEUR_SIZE));
                }
            }

            rs.plantes().stocksPresents().forEach(stock -> {
                obstacles.add(tableUtils.createPolygonObstacle(stock, EurobotConfig.PATHFINDER_STOCK_PLANTES_SIZE));
            });

            // ajout des stocks de pots
            for (StockPots stocksPot : rs.stocksPots()) {
                if (stocksPot.isPresent()) {
                    obstacles.add(tableUtils.createPolygonObstacle(stocksPot, EurobotConfig.PATHFINDER_STOCK_POTS_SZIE));
                }
            }
        }

        super.setObstacles(obstacles);
    }
}
