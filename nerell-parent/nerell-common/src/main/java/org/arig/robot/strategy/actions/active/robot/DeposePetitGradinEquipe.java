package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rang;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposePetitGradinEquipe extends AbstractDeposeGradin {

  private static final int CENTER_X = 775;

  @Override
  public boolean isValid() {
    final GradinBrut gradinBrutBloquant;
    if (rs.team() == Team.BLEU) {
      gradinBrutBloquant = rs.gradinBrutStocks().get(GradinBrut.ID.BLEU_BAS_CENTRE);
    } else {
      gradinBrutBloquant = rs.gradinBrutStocks().get(GradinBrut.ID.JAUNE_BAS_CENTRE);
    }

    if (gradinBrutBloquant.present()) {
      return false;
    }
    return super.isValid();
  }

  @Override
  protected ConstructionArea constructionArea() {
    return rs.petitGradinEquipe();
  }

  @Override
  protected Point rangPosition(Rang rang) {
    return new Point(getX(CENTER_X), EurobotConfig.rang1Coord);
  }
}
