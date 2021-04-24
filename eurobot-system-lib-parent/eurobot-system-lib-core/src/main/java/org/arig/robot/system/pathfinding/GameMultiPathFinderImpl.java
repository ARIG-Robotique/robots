package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
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
        // Ajout des obstacles en fonctions des bouées
        int[] boueesAvoided = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        for (int nb : boueesAvoided) {
            // ignore la bouée devant le petit port
            if ((rs.team() == ETeam.BLEU && nb == 9) || (rs.team() == ETeam.JAUNE && nb == 8)) {
                continue;
            }
            // ignore la bouée devant l'ecueil adverse
            if ((rs.team() == ETeam.BLEU && nb == 11) || (rs.team() == ETeam.JAUNE && nb == 6)) {
                continue;
            }

            Bouee bouee = rs.bouee(nb);
            if (rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime && bouee.presente()) {
                obstacles.add(tableUtils.createPolygonObstacle(bouee.pt(),
                        nb <= 4 || nb >= 13 ? IEurobotConfig.pathFindingTailleBoueePort : IEurobotConfig.pathFindingTailleBouee));
            }
        }

        // ajoute les grand chenaux
        if (rs.team() == ETeam.BLEU && !rs.grandChenaux().chenalVertEmpty()) {
            obstacles.add(buildChenal(new Point(330, 2000 - 515)));
        }
        if (rs.team() == ETeam.BLEU && !rs.grandChenaux().chenalRougeEmpty()) {
            obstacles.add(buildChenal(new Point(330, 2000 - 1085)));
        }
        if (rs.team() == ETeam.JAUNE && !rs.grandChenaux().chenalVertEmpty()) {
            obstacles.add(buildChenal(new Point(3000 - 330, 2000 - 1085)));
        }
        if (rs.team() == ETeam.JAUNE && !rs.grandChenaux().chenalRougeEmpty()) {
            obstacles.add(buildChenal(new Point(3000 - 330, 2000 - 515)));
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
