package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Team;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

@Slf4j
public class GameMultiPathFinderImpl extends MultiPathFinderImpl {

  private static final int rayonRobotCm = 16;
  private static final int rayonPamiCm = 10;

  @Autowired
  private EurobotStatus rs;

  @Autowired
  private RobotName robotName;

  @Autowired
  private TableUtils tableUtils;

  @Override
  public void setObstacles(final List<Shape> obstacles) {
    if (!rs.pamiRobot()) {
      obstaclesRobot(obstacles);
    } else {
      obstaclesPami(obstacles);
    }

    super.setObstacles(obstacles);
  }

  private void obstaclesPami(List<Shape> obstacles) {
    // Scene adverse
    if (rs.team() == Team.JAUNE) {
      obstacles.add(new Rectangle(150 - rayonPamiCm, 155 - rayonPamiCm, 150 + (2 * rayonPamiCm), 45 + rayonPamiCm));
    } else {
      obstacles.add(new Rectangle(0, 155 - rayonPamiCm, 150 + (2 * rayonPamiCm), 45 + rayonPamiCm));
    }

    // Scene equipe
    if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
      // Super star, pas de "table", que de la scene
      if (rs.team() == Team.JAUNE) {
        obstacles.add(new Rectangle(0, 155, 105 + rayonPamiCm, 20 + rayonPamiCm));
      } else {
        obstacles.add(new Rectangle(195 - rayonPamiCm, 155, 105 + rayonPamiCm, 20 + rayonPamiCm));
      }
    } else {
      // PAMI, pas de "scene", que de la table
      ajoutScene(obstacles);

      // Zone retour de Nerell
      if (rs.team() == Team.JAUNE) {
        obstacles.add(new Rectangle(0, 110, 60, 45));
      } else {
        obstacles.add(new Rectangle(240, 110, 60, 45));
      }

      // Zone table sans interret
      obstacles.add(new Rectangle(0, 0, 300, 110));
    }
  }

  private void obstaclesRobot(List<Shape> obstacles) {
    // Zone démarrage des PAMI
    if (rs.team() == Team.JAUNE) {
      obstacles.add(new Rectangle(0, 155 - rayonRobotCm, 15 + rayonRobotCm, 45 + (2 * rayonRobotCm)));
    } else {
      obstacles.add(new Rectangle(285 - rayonRobotCm, 155 - rayonRobotCm, 15 + rayonRobotCm, 45 + (2 * rayonRobotCm)));
    }

    // Pas de scene pour les robots
    ajoutScene(obstacles);

    // Zone de déplacement des pamis
    if (rs.getRemainingTime() <= EurobotConfig.validRetourBackstageRemainingTime) {

      // /!\ Create polygon obstacle travaille en MM, et retourne en CM
      Polygon pol = tableUtils.createPolygonObstacle(new Point(1500, 2000), (100 + rayonRobotCm) * 20);
      obstacles.add(pol);
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

    // Zone de dépose des gradins
    if (!rs.grandGradinEquipe().isEmpty()) {
      int x = tableUtils.getX(rs.team() == Team.BLEU, 1225) / 10;
      int y;
      if (rs.grandGradinEquipe().data()[0][0]) {
        y = EurobotConfig.rang1Coord / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.HORIZONTAL));
      }
      if (rs.grandGradinEquipe().data()[1][0]) {
        y = EurobotConfig.rang2Coord / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.HORIZONTAL));
      }
      if (rs.grandGradinEquipe().data()[2][0]) {
        y = EurobotConfig.rang3Coord / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.HORIZONTAL));
      }
    }
    if (!rs.grandGradinAdverse().isEmpty()) {
      int y = 87;
      int x;
      if (rs.grandGradinAdverse().data()[0][0]) {
        x = tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang1Coord) / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.VERTICAL));
      }
      if (rs.grandGradinAdverse().data()[1][0]) {
        x = tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang2Coord) / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.VERTICAL));
      }
      if (rs.grandGradinAdverse().data()[2][0]) {
        x = tableUtils.getX(rs.team() == Team.JAUNE, EurobotConfig.rang3Coord) / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.VERTICAL));
      }
    }
    if (!rs.petitGradinEquipe().isEmpty()) {
      int x = tableUtils.getX(rs.team() == Team.BLEU, 775) / 10;
      int y;
      if (rs.petitGradinEquipe().data()[0][0]) {
        y = EurobotConfig.rang1Coord / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.HORIZONTAL));
      }
    }
    if (!rs.petitGradinAdverse().isEmpty()) {
      int x = tableUtils.getX(rs.team() == Team.BLEU, 2775) / 10;
      int y;
      if (rs.petitGradinAdverse().data()[0][0]) {
        y = EurobotConfig.rang1Coord / 10;
        obstacles.add(getObstacleGradin(x, y, GradinBrut.Orientation.HORIZONTAL));
      }
    }

    // Ajout des gradins bruts présent sur la table
    if (rs.getRemainingTime() > EurobotConfig.validRetourBackstageRemainingTime) {
      for (GradinBrut gradin : rs.gradinBrutStocks()) {
        if (gradin.present()) {
          double x = gradin.getX() / 10;
          double y = gradin.getY() / 10;
          obstacles.add(getObstacleGradin((int) x, (int) y, gradin.orientation()));
        }
      }
    }
  }

  private void ajoutScene(List<Shape> obstacles) {
    obstacles.add(new Rectangle(65 - rayonPamiCm, 180 - rayonPamiCm, 235 + (2 * rayonPamiCm), 20 + rayonPamiCm));
    obstacles.add(new Rectangle(105 - rayonPamiCm, 155 - rayonPamiCm, 90 + (2 * rayonPamiCm), 45 + rayonPamiCm));
  }

  private Rectangle getObstacleGradin(int x, int y, GradinBrut.Orientation orientation) {
    if (orientation == GradinBrut.Orientation.HORIZONTAL) {
      return new Rectangle(x - 20 - rayonRobotCm, y - 5 - rayonRobotCm, 40 + (2 * rayonRobotCm), 10 + (2 * rayonRobotCm));
    } else {
      return new Rectangle(x - 5 - rayonRobotCm, y - 20 - rayonRobotCm, 10 + (2 * rayonRobotCm), 40 + (2 * rayonRobotCm));
    }
  }
}
