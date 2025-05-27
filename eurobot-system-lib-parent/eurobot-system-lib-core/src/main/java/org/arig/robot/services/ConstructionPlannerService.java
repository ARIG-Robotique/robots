package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Construction2FloorAction;
import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionElementSource;
import org.arig.robot.model.ConstructionFloorAction;
import org.arig.robot.model.ConstructionMoveAction;
import org.arig.robot.model.ConstructionPlanResult;
import org.arig.robot.model.ConstructionTake2Action;
import org.arig.robot.model.Etage;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Face;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockVirtuel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConstructionPlannerService {

  private final EurobotStatus rs;

  public ConstructionPlanResult plan(ConstructionArea rootArea) {
    return plan(rootArea, false);
  }

  public ConstructionPlanResult plan(ConstructionArea rootArea, boolean skipLog) {
    ConstructionArea area = rootArea.clone();
    List<ConstructionAction> actions = new ArrayList<>();
    ConstructionPlanResult result = new ConstructionPlanResult(area, actions);
    StockVirtuel stock = new StockVirtuel(rs.faceAvant().nbEtageConstructible(), rs.faceArriere().nbEtageConstructible());

    if (!skipLog) {
      logBeforePlan(area);
    }
    while(stock.totalSize() > 0) {
      Rang rang = area.getFirstConstructibleRang();
      int nbElementsInRang = area.getNbElementsInRang(rang);

      // On peu faire des piles de 3
      if (!rs.limiter2Etages() && area.nbRang() > 1) {
        // Etape 1, on pose un élément seul sur le rang, seulement si il est vide
        if (nbElementsInRang == 0 && stock.totalSize() >= 3) {
          // Pile de 1 pour commencer une pile de 3
          build1Floor(actions, stock, rang, area);
          continue;
        }

        // Etape 2, on cherche un rang vide pour faire une pile de 2
        Rang rangVide = area.getFirstRangWithElement(0);
        if (rangVide != null && stock.totalSize() >= 2) {
          build2Floor(actions, 0, stock, rangVide, area);
          continue;
        }

        // Etape 3 est-ce que l'on peut faire une pile de 3 ?
        Rang rangOneElement = area.getFirstRangWithElement(1);
        Rang rangTwoElements = area.getFirstRangWithElement(2);
        Face emptyFace = stock.emptyFace(); // TODO: Optim face avec que BOTTOM, ou empty face
        if (rangOneElement != null && rangTwoElements != null && rangOneElement.before(rangTwoElements) && emptyFace != null) {
          actions.add(new ConstructionMoveAction(emptyFace, rangTwoElements));
          actions.add(new ConstructionTake2Action(emptyFace, rangTwoElements));
          actions.add(new ConstructionMoveAction(emptyFace, rangOneElement));
          actions.add(new Construction2FloorAction(emptyFace, rangOneElement));
          area.removeGradin(rangTwoElements, Etage.ETAGE_1, true);
          area.removeGradin(rangTwoElements, Etage.ETAGE_2, true);
          area.addGradin(rangOneElement, Etage.ETAGE_2, true);
          area.addGradin(rangOneElement, Etage.ETAGE_3, true);
          continue;
        }
      }

      // On ne peut pas faire de pile de 3, on fait une pile de 2 ou 1
      if (rang != null && stock.totalSize() >= 2) {
        build2Floor(actions, nbElementsInRang, stock, rang, area);
        continue;

      } else if (rang != null) {
        build1Floor(actions, stock, rang, area);
        continue;
      }

      // On ne peut plus rien faire, on sort de la boucle
      break;
    }

    if (!skipLog) {
      logPlan(actions);
    }
    return result;
  }

  private void build2Floor(List<ConstructionAction> actions, int nbElementsInRang, StockVirtuel stock, Rang rang, ConstructionArea area) {
    // Pile de 2
    int nbElementsToPlace = Math.min(2 - nbElementsInRang, 2);

    List<ConstructionElementSource> sources = stock.takeElements(nbElementsToPlace);
    actions.add(new ConstructionMoveAction(sources.get(0).face(), rang));
    for (ConstructionElementSource source : sources) {
      Etage targetEtage = nbElementsToPlace == 2 ? Etage.ETAGE_1 : Etage.ETAGE_2;
      actions.add(new ConstructionFloorAction(source.face(), rang, targetEtage, source.stockPosition()));
      area.addGradin(rang, targetEtage, true);
      nbElementsToPlace--;
    }
  }

  private void build1Floor(List<ConstructionAction> actions, StockVirtuel stock, Rang rang, ConstructionArea area) {
    // Pile de 1
    ConstructionElementSource source = stock.takeElements(1).get(0);
    actions.add(new ConstructionMoveAction(source.face(), rang));
    actions.add(new ConstructionFloorAction(source.face(), rang, Etage.ETAGE_1, source.stockPosition()));
    area.addGradin(rang, Etage.ETAGE_1, true);
  }

  private void logBeforePlan(ConstructionArea area) {
    log.info("Planning pour zone de construction {} {}", area.name(), rs.limiter2Etages() ? "avec limitation a 2 étages" : "sans limitation");
    log.info(" - Etat zone : {}", area);
    log.info("Stock robot : ");
    log.info(" - Avant [{}]", rs.faceAvant());
    log.info(" - Arriere [{}]", rs.faceArriere().toString());
  }

  private void logPlan(List<ConstructionAction> actions) {
    if (actions.isEmpty()) {
      log.info("Plan : Aucune action");
      return;
    }
    log.info("Plan : ");
    int i = 0;
    for (ConstructionAction action : actions) {
      log.info("  - {} : {}", i++, action);
    }
  }
}
