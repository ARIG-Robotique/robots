package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.*;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;

@Slf4j
public class GameMultiPathFinderImpl extends MultiPathFinderImpl {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Override
    public void setObstacles(final List<Shape> obstacles) {

        if (rs.getRemainingTime() > 10000) {
            // ajout des echantillons
            for (Echantillon echantillon : rs.echantillons()) {
                if (echantillon.isBlocking()) {
                    obstacles.add(tableUtils.createPolygonObstacle(echantillon, EurobotConfig.PATHFINDER_ECHANTILLON_SIZE));
                }
            }
        }

        // ajout campement
        if (rs.tailleCampementRougeVertNord() > 0) {
            obstacles.add(tableUtils.createPolygonObstacle(new Point(tableUtils.getX(rs.team() == Team.VIOLET, 133), 1380), EurobotConfig.PATHFINDER_ECHANTILLON_SIZE));
        }
        if (rs.tailleCampementRougeVertSud() > 0) {
            obstacles.add(tableUtils.createPolygonObstacle(new Point(tableUtils.getX(rs.team() == Team.VIOLET, 133), 1220), EurobotConfig.PATHFINDER_ECHANTILLON_SIZE));
        }

        // dead zone au distributeur
        if (EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE.equals(rs.currentAction())) {
            obstacles.add(new Rectangle(
                    rs.team() == Team.JAUNE ? 150 : 110, 160,
                    40, 40
            ));
        }

        super.setObstacles(obstacles);
    }
}
