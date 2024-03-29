package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Plante;
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

        if (rs.getRemainingTime() > 10000) {
            // ajout des plantes
            for (Plante plante : rs.stockPlantes()) {
                if (plante.isBlocking()) {
                    obstacles.add(tableUtils.createPolygonObstacle(plante, EurobotConfig.PATHFINDER_FLEUR_SIZE));
                }
            }
        }

        // ajout pots
        /*if (rs.tailleCampementRougeVertNord() > 0) {
            obstacles.add(tableUtils.createPolygonObstacle(new Point(tableUtils.getX(rs.team() == Team.VIOLET, 133), 1380), EurobotConfig.PATHFINDER_FLEUR_SIZE));
        }*/

        super.setObstacles(obstacles);
    }
}
