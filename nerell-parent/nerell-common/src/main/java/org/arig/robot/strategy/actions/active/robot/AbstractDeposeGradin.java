package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Point;
import org.arig.robot.services.AbstractNerellFaceService;
import org.arig.robot.services.NerellFaceWrapper;
import org.arig.robot.strategy.actions.AbstractNerellAction;

@Slf4j
public abstract class AbstractDeposeGradin extends AbstractNerellAction {

  protected abstract ConstructionArea constructionArea();
  protected abstract Point rangPosition(ConstructionArea.Rang rang);

  @Override
  public String name() {
    return EurobotConfig.ACTION_DEPOSE_GRADIN_PREFIX + constructionArea().name();
  }

  @Override
  public Point entryPoint() {
    ConstructionArea.Rang rang = constructionArea().getFirstConstructibleRang(rs.limiter2Etages());
    Point entry = rangPosition(rang);
    entry.addDeltaY(EurobotConfig.offsetDeposeGradin);
    return entry;
  }

  @Override
  public int order() {
    // TODO : Prendre en compte la dépose des gradins exact pour le score
    int order = 0;
    int nbEtageAvant = rs.faceAvant().nbEtageConstructible();
    int nbEtageArriere = rs.faceArriere().nbEtageConstructible();
    if (nbEtageAvant > 0) {
      order += nbEtageAvant == 2 ? 12 : 4;
    }
    if (nbEtageArriere > 0) {
      order += nbEtageArriere == 2 ? 12 : 4;
    }

    if (constructionArea().nbRang() == 1) {
      if (rs.limiter2Etages() && order > 12) {
        order = 12;
      }
    }

    return order + tableUtils.alterOrder(entryPoint()); // Deux niveaux
  }

  @Override
  public boolean isValid() {
    // Rien en stock dans le robot, rien a déposer
    if (rs.faceAvant().isEmpty() && rs.faceArriere().isEmpty()) {
      return false;
    }

    // Ne pas autoriser les déposes sans stock complet sur les deux faces quand deux faces actives
    if (rs.useTwoFaces() && rs.getRemainingTime() >= EurobotConfig.validDeposeDeuxFacesNonPleineRemainingTimeNerell
        && (rs.faceAvant().isEmpty() || rs.faceArriere().isEmpty()))
    {
      return false;
    }

    // Plus le temps de construire, on ne peut pas déposer
    if (rs.getRemainingTime() < EurobotConfig.validTimeConstruction) {
      return false;
    }

    // SI aucun rang n'est constructible, on ne peut pas déposer
    ConstructionArea.Rang rang = constructionArea().getFirstConstructibleRang(rs.limiter2Etages());
    ConstructionArea.Etage etage = constructionArea().getFirstConstructibleEtage(rang, rs.limiter2Etages());
    return isTimeValid() && rang != null && etage != null;
  }

  @Override
  public int executionTimeMs() {
    return 0;
  }

  @Override
  public void execute() {
    mv.setVitessePercent(100, 100);

    try {
      int nbDeposeCombine = !rs.faceAvant().isEmpty() ? 1 : 0;
      nbDeposeCombine += !rs.faceArriere().isEmpty() ? 1 : 0;
      for (int i = 0; i < nbDeposeCombine; i++) {
        mv.pathTo(entryPoint());

        ConstructionArea.Rang rang = constructionArea().getFirstConstructibleRang(rs.limiter2Etages());
        ConstructionArea.Etage etage = constructionArea().getFirstConstructibleEtage(rang, rs.limiter2Etages());

        log.info("Dépose dans le {} sur {}", rang.name(), etage.name());

        final int nbEtageRequis;
        if (rs.limiter2Etages()) {
          // Si on limite a 2 étage soit 1 ou 2 étage
          nbEtageRequis = etage == ConstructionArea.Etage.ETAGE_1 ? 2 : 1;
        } else {
          throw new RuntimeException("Impossible de déposer sur un rang sans limite a 2 étages. Pour le moment 😅");
        }
        log.info("Demande de construction de {} etage(s).", nbEtageRequis);

        NerellFaceWrapper.Face face = faceWrapper.getConstructionFace(nbEtageRequis);
        if (face == null) {
          log.warn("Pas de face pour la dépose de {} étage(s)", nbEtageRequis);
          return;
        }
        AbstractNerellFaceService faceService = faceWrapper.getFaceService(face);

        Point rangPosition = rangPosition(rang);
        faceService.deposeGradin(constructionArea(), rangPosition, rang, etage, nbEtageRequis);
      }
    } catch (NoPathFoundException | AvoidingException e) {
      log.warn("Erreur prise {} : {}", name(), e.toString());
      updateValidTime();
    } finally {
      if (constructionArea().getFirstConstructibleRang(rs.limiter2Etages()) == null) {
        // On a déposé tous les gradins, on ne peut plus rien faire
        complete();
      }
    }
  }
}
