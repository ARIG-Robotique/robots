package org.arig.robot.system.pathfinding.impl;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

@Slf4j
public class GameMultiPathFinderImpl extends MultiPathFinderImpl {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Override
    public void setObstacles(final List<Shape> obstacles) {
        // Ajout des obstacles en fonctions des bouées
        int[] boueesAvoided = new int[]{5, 6, 7, 8, 9, 10, 11, 12};
        for (int nb : boueesAvoided) {
            // ignore la bouée devant le petit port
            if ((rs.getTeam() == ETeam.BLEU && nb == 9) || (rs.getTeam() == ETeam.JAUNE && nb == 8)) {
                continue;
            }
            // ignore la bouée devant l'ecueil adverse en aggressif
            if (rs.getStrategy() == EStrategy.AGGRESSIVE && ((rs.getTeam() == ETeam.BLEU && nb == 11) || (rs.getTeam() == ETeam.JAUNE && nb == 6))) {
                continue;
            }

            Bouee bouee = rs.bouee(nb);
            if (!bouee.prise()) {
                obstacles.add(tableUtils.createPolygonObstacle(bouee.pt(), IConstantesNerellConfig.pathFindingTailleBouee));
            }
        }
        super.setObstacles(obstacles);
    }
}
