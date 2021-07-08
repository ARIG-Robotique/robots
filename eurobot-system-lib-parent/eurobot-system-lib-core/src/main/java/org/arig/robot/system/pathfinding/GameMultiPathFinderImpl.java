package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.Point;
import java.awt.Polygon;
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
        // Ajout des obstacles en fonctions des bouées
        int[] boueesAvoided = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        for (int nb : boueesAvoided) {
            if (!rs.twoRobots() && (rs.team() == ETeam.BLEU ? nb == 9 : nb == 8)) {
                continue;
            }
            if (rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime && rs.boueePresente(nb)) {
                obstacles.add(tableUtils.createPolygonObstacle(rs.boueePt(nb),
                        nb <= 4 || nb >= 13 ? IEurobotConfig.pathFindingTailleBoueePort : IEurobotConfig.pathFindingTailleBouee));
            }
        }

        // ajoute les bouee bordure grand chenaux
        if (rs.team() == ETeam.BLEU && !rs.grandChenalVertBordureEmpty()) {
            obstacles.add(tableUtils.createPolygonObstacle(new org.arig.robot.model.Point(50, 1520), IEurobotConfig.pathFindingTailleBouee));
        }
        if (rs.team() == ETeam.BLEU && !rs.grandChenalRougeBordureEmpty()) {
            obstacles.add(tableUtils.createPolygonObstacle(new org.arig.robot.model.Point(50, 870), IEurobotConfig.pathFindingTailleBouee));
        }
        if (rs.team() == ETeam.JAUNE && !rs.grandChenalVertBordureEmpty()) {
            obstacles.add(tableUtils.createPolygonObstacle(new org.arig.robot.model.Point(3000 - 50, 1520), IEurobotConfig.pathFindingTailleBouee));
        }
        if (rs.team() == ETeam.JAUNE && !rs.grandChenalRougeBordureEmpty()) {
            obstacles.add(tableUtils.createPolygonObstacle(new org.arig.robot.model.Point(3000 - 50, 870), IEurobotConfig.pathFindingTailleBouee));
        }

        // ajoute les grand chenaux
        if (rs.team() == ETeam.BLEU && !rs.grandChenalVertEmpty()) {
            obstacles.add(buildChenal(new Point(330, 2000 - 515)));
        }
        if (rs.team() == ETeam.BLEU && !rs.grandChenalRougeEmpty()) {
            obstacles.add(buildChenal(new Point(330, 2000 - 1085)));
        }
        if (rs.team() == ETeam.JAUNE && !rs.grandChenalVertEmpty()) {
            obstacles.add(buildChenal(new Point(3000 - 330, 2000 - 1085)));
        }
        if (rs.team() == ETeam.JAUNE && !rs.grandChenalRougeEmpty()) {
            obstacles.add(buildChenal(new Point(3000 - 330, 2000 - 515)));
        }

        // ajoute le haut fond
        if (!rs.hautFondPris()) {
            obstacles.add(new Rectangle(150 - 1, 200 - 65, 2, 65));
        }

        super.setObstacles(obstacles);
    }

    private Polygon buildChenal(Point pt) {
        Polygon chenal = new Polygon();
        chenal.addPoint(12, 27);
        chenal.addPoint(33, 6);
        chenal.addPoint(33, -6);
        chenal.addPoint(12, -27);
        chenal.addPoint(-12, -27);
        chenal.addPoint(-33, -6);
        chenal.addPoint(-33, 6);
        chenal.addPoint(-12, 27);

        chenal.translate((int) pt.getX() / 10, (int) pt.getY() / 10);

        return chenal;
    }
}
