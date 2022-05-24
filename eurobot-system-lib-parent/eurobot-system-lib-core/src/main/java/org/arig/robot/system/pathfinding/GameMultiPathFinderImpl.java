package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Echantillon;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

        // ajout des echantillons
        for (Echantillon echantillon : rs.echantillons()) {
            if (echantillon.isBlocking()) {
                obstacles.add(tableUtils.createPolygonObstacle(echantillon, EurobotConfig.PATHFINDER_ECHANTILLON_SIZE));
            }
        }

        // ajout des sites de fouille
        if (!StringUtils.startsWith(rs.currentAction(), EurobotConfig.ACTION_RETOUR_SITE_DE_FOUILLE_PREFIX) && rs.getRemainingTime() > 10000) {
            if (!rs.siteDeFouillePris() && rs.team() == Team.JAUNE || !rs.siteDeFouilleAdversePris() && rs.team() == Team.VIOLET) {
                obstacles.add(tableUtils.createPolygonObstacle(new Point(975, 625), EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE));
            }
            if (!rs.siteDeFouillePris() && rs.team() == Team.VIOLET || !rs.siteDeFouilleAdversePris() && rs.team() == Team.JAUNE) {
                obstacles.add(tableUtils.createPolygonObstacle(new Point(2025, 625), EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE));
            }
        }

        // ajout campement
        if (rs.tailleCampementRougeVertNord() > 0) {
            obstacles.add(tableUtils.createPolygonObstacle(new Point(tableUtils.getX(rs.team() == Team.VIOLET, 133), 1380), EurobotConfig.PATHFINDER_ECHANTILLON_SIZE));
        }
        if (rs.tailleCampementRougeVertSud() > 0) {
            obstacles.add(tableUtils.createPolygonObstacle(new Point(tableUtils.getX(rs.team() == Team.VIOLET, 133), 1220), EurobotConfig.PATHFINDER_ECHANTILLON_SIZE));
        }

        super.setObstacles(obstacles);
    }
}
