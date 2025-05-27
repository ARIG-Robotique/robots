package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionFloorAction;
import org.arig.robot.model.ConstructionMoveAction;
import org.arig.robot.model.ConstructionPlanResult;
import org.arig.robot.model.Etage;
import org.arig.robot.model.Face;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockPosition;
import org.arig.robot.services.AbstractNerellFaceService;
import org.arig.robot.services.ConstructionPlannerService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDeposeGradin extends AbstractNerellAction {

  @Autowired
  private ConstructionPlannerService constructionPlannerService;

  protected abstract ConstructionArea constructionArea();
  protected abstract Point rangPosition(Rang rang);

  @Override
  public String name() {
    return EurobotConfig.ACTION_DEPOSE_GRADIN_PREFIX + constructionArea().name();
  }

  @Override
  public final Point entryPoint() {
    return entryPoint(constructionPlannerService.plan(constructionArea(), true));
  }

  private Point entryPoint(ConstructionPlanResult planResult) {
    Rang rang = planResult.actions().stream()
      .filter(ConstructionMoveAction.class::isInstance)
      .map(ConstructionAction::rang)
      .findFirst().orElse(Rang.RANG_1);
    Point entry = rangPosition(rang);
    applyOffsetRangPosition(entry);
    return entry;
  }

  protected void applyOffsetRangPosition(Point position) {
    position.addDeltaY(EurobotConfig.offsetDeposeGradin);
  }


  @Override
  public int order() {
    final ConstructionPlanResult planResult = constructionPlannerService.plan(constructionArea(), true);
    int order = planResult.newArea().score() - constructionArea().score();
    return order + tableUtils.alterOrder(entryPoint(planResult));
  }

  @Override
  public boolean isValid() {
    // Rien en stock dans le robot, rien a déposer
    if (rs.faceAvant().isEmpty() && rs.faceArriere().isEmpty()) {
      return false;
    }

    // Ne pas autoriser les déposes sans stock complet sur les deux faces quand deux faces actives
    if (rs.useTwoFaces() && rs.getRemainingTime() >= EurobotConfig.validDeposeDeuxFacesNonPleineRemainingTime
      && (rs.faceAvant().isEmpty() || rs.faceArriere().isEmpty())) {
      return false;
    }

    // Plus le temps de construire, on ne peut pas déposer
    if (rs.getRemainingTime() < EurobotConfig.validDeposeRemainingTime) {
      return false;
    }

    // SI aucun rang n'est constructible, on ne peut pas déposer
    final ConstructionPlanResult planResult = constructionPlannerService.plan(constructionArea(), true);
    return isTimeValid() && !planResult.actions().isEmpty();
  }

  @Override
  public int executionTimeMs() {
    return 0;
  }

  @Override
  public void execute() {
    mv.setVitessePercent(100, 100);

    try {
      final ConstructionPlanResult planResult = constructionPlannerService.plan(constructionArea());
      log.info("Plan de construction pour {}", constructionArea().name());
      for (ConstructionAction action : planResult.actions()) {
        log.info(" - {}", action);
      }
      Rang currentRang = null;
      boolean firstDepose = true;
      for (ConstructionAction action : planResult.actions()) {
        if (action instanceof ConstructionMoveAction moveAction) {
          firstDepose = true;
          if (currentRang == null) {
            currentRang = moveAction.rang();
            mv.pathTo(entryPoint(planResult));
          } else {
            Point pt = rangPosition(currentRang);
            applyOffsetRangPosition(pt);
            mv.gotoPoint(pt);
          }

        } else if (action instanceof ConstructionFloorAction floorAction) {
          Face face = floorAction.face();
          Rang rang = floorAction.rang();
          Etage etage = floorAction.etage();
          StockPosition stockPosition = floorAction.stockPosition();

          Point rangPosition = rangPosition(rang);

          AbstractNerellFaceService faceService = faceWrapper.getFaceService(face);
          faceService.prepareDeposeGradin(rangPosition, firstDepose);
          firstDepose = false;
          faceService.deposeGradin(etage);
          constructionArea().addGradin(rang, etage);
        }
      }
    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Erreur prise {} : {}", name(), e.toString());
      updateValidTime();
    } finally {
      if (constructionArea().isUnconstructable()) {
        // On a déposé tous les gradins, on ne peut plus rien faire
        complete();
      }
    }
  }
}
