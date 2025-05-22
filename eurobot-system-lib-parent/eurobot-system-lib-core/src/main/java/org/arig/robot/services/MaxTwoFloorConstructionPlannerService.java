package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionElementSource;
import org.arig.robot.model.ConstructionFloorAction;
import org.arig.robot.model.ConstructionMoveAction;
import org.arig.robot.model.ConstructionPlanResult;
import org.arig.robot.model.Etage;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockVirtuel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class MaxTwoFloorConstructionPlannerService implements ConstructionPlannerService {

  private static final boolean limit2Etages = true;

  private final EurobotStatus rs;

  @Override
  public ConstructionPlanResult plan(ConstructionArea rootArea) {
    ConstructionArea area = rootArea.clone();
    List<ConstructionAction> actions = new ArrayList<>();
    ConstructionPlanResult result = new ConstructionPlanResult(area, actions);
    StockVirtuel stock = new StockVirtuel(rs.faceAvant().nbEtageConstructible(), rs.faceArriere().nbEtageConstructible());

    while(stock.totalSize() > 0) {
      Rang rang = area.getFirstConstructibleRang(limit2Etages);
      if (rang == null) {
        break;
      }

      if (stock.totalSize() >= 2) {
        // Pile de 2
        int nbElementsInRang = area.getNbElementsInRang(rang);
        int nbElementsToPlace = Math.min(2 - nbElementsInRang, 2);

        List<ConstructionElementSource> sources = stock.takeElements(nbElementsToPlace);
        actions.add(new ConstructionMoveAction(sources.get(0).face(), rang));
        for (ConstructionElementSource source : sources) {
          Etage targetEtage = nbElementsToPlace == 2 ? Etage.ETAGE_1 : Etage.ETAGE_2;
          actions.add(new ConstructionFloorAction(source.face(), rang, targetEtage, source.stockPosition()));
          area.addGradin(rang, targetEtage, true);
          nbElementsToPlace--;
        }

      } else {
        // Pile de 1
        ConstructionElementSource source = stock.takeElements(1).get(0);
        actions.add(new ConstructionMoveAction(source.face(), rang));
        actions.add(new ConstructionFloorAction(source.face(), rang, Etage.ETAGE_1, source.stockPosition()));
        area.addGradin(rang, Etage.ETAGE_1, true);
      }
    }

    return result;
  }
}
