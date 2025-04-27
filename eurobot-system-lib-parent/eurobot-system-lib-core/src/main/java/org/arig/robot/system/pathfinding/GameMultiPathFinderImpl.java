package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Team;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

@Slf4j
public class GameMultiPathFinderImpl extends MultiPathFinderImpl {

    @Autowired
    private EurobotStatus rs;

    //@Autowired
    //private TableUtils tableUtils;

    @Override
    public void setObstacles(final List<Shape> obstacles) {
        final int rayonRobotCm = 15;

        // Zone des PAMI
        if (!rs.pamiRobot()) {
            if (rs.team() == Team.JAUNE) {
                obstacles.add(new Rectangle(0, 155 - rayonRobotCm, 15 + rayonRobotCm, 45 + (2 * rayonRobotCm)));
            } else {
                obstacles.add(new Rectangle(285 - rayonRobotCm, 155 - rayonRobotCm, 15 + rayonRobotCm, 45 + (2 * rayonRobotCm)));
            }
        }

        // Zones adverses
        if (rs.team() == Team.JAUNE) {
            // Backstage bleu
            obstacles.add(new Rectangle(105 - rayonRobotCm, 155 - rayonRobotCm, 195, 45 + rayonRobotCm));
            // Mileu grand bleu
            obstacles.add(new Rectangle(0, 65 - rayonRobotCm, 45 + rayonRobotCm, 45 + (2 * rayonRobotCm)));
            // Bas gauche petit bleu
            obstacles.add(new Rectangle(0, 0, 45 + rayonRobotCm, 15 + rayonRobotCm));
            // Bas milieu grand bleu
            obstacles.add(new Rectangle(155 - rayonRobotCm, 0, 45 + (2 * rayonRobotCm), 45 + rayonRobotCm));
            // Bas droit petit bleu
            obstacles.add(new Rectangle(200 - rayonRobotCm, 0, 45 + (2 * rayonRobotCm), 15 + rayonRobotCm));
        } else {
            // Backstage jaune
            obstacles.add(new Rectangle(0, 155 - rayonRobotCm, 195 + rayonRobotCm, 45 + rayonRobotCm));
            // Mileu grand jaune
            obstacles.add(new Rectangle(255 - rayonRobotCm, 65 - rayonRobotCm, 45 + rayonRobotCm, 45 + (2 * rayonRobotCm)));
            // Bas droit petit jaune
            obstacles.add(new Rectangle(255 - rayonRobotCm, 0, 45 + rayonRobotCm, 15 + rayonRobotCm));
            // Bas milieu grand jaune
            obstacles.add(new Rectangle(100 - rayonRobotCm, 0, 45 + (2 * rayonRobotCm), 45 + rayonRobotCm));
            // Bas droit petit jaune
            obstacles.add(new Rectangle(55 - rayonRobotCm, 0, 45 + (2 * rayonRobotCm), 15 + rayonRobotCm));
        }

        if (rs.getRemainingTime() > EurobotConfig.validRetourBackstageRemainingTimeNerell) {
            // Ajout des gradins bruts (en stocks)
            for (GradinBrut gradin : rs.gradinBrutStocks()) {
                if (gradin.present()) {
                    double x = gradin.getX();
                    double y = gradin.getY();
                    obstacles.add(new Rectangle((int) (x - 20 - rayonRobotCm), (int) (y - 5 - rayonRobotCm), 40 + (2 * rayonRobotCm), 10 + (2 * rayonRobotCm)));
                }
            }
        }

        super.setObstacles(obstacles);
    }
}
