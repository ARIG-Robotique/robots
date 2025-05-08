package org.arig.robot.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNerellFaceService {

  protected final NerellRobotStatus rs;
  protected final TrajectoryManager mv;
  protected final NerellRobotServosService servos;
  protected final NerellIOService ioService;

  public enum PriseGradinState {
    OK, ERREUR_PINCES, ERREUR_TIROIR, ERREUR_COLONNES
  }

  protected abstract boolean iosPinces();
  protected abstract boolean iosTiroir();
  protected abstract boolean iosColonnesSol();

  protected abstract void updateStockRobot(boolean expectedSimulator);

  protected abstract void aligneFace(Point gradin) throws AvoidingException;
  protected abstract void ouvreFacePourPrise();
  protected abstract void deplacementPriseColonnesPinces() throws AvoidingException;
  protected abstract void deplacementPriseColonnesSol() throws AvoidingException;

  protected abstract boolean miseEnStockTiroir();
  protected abstract void verrouillageColonnesSol();
  protected abstract void deverouillageColonnesSol();

  public void preparePriseGradinBrut(GradinBrut gradin) throws AvoidingException {
    log.info("Préparation de la prise du gradin brut : {}", gradin.id());
    // a. Aligne la face qui est disponible
    log.info(" - Alignement de la face");
    aligneFace(gradin);

    // b. Ouvre les pinces pour la prise
    log.info(" - Ouverture face, pour la prise");
    ouvreFacePourPrise();
  }

  public void prepareDeposeGradin(Point pointDepose) {
    log.info("Préparation de la dépose du gradin");

  }

  public PriseGradinState prendreGradinBrutStockTiroir() throws AvoidingException {
    try {
      log.info("Prise du gradin brut pour stock tiroir");

      deplacementPriseColonnesPinces();
      if (!iosPinces()) {
        log.warn("Erreur de chargement du gradin brut dans les pinces");
        return PriseGradinState.ERREUR_PINCES;
      }
      log.info(" - Mise en stock du gradin brut");
      if (!miseEnStockTiroir()) {
        log.warn("Erreur de mise en stock du gradin brut");
        return PriseGradinState.ERREUR_TIROIR;
      }

      log.info(" - Mise en stock des colonnes au sol");
      deplacementPriseColonnesSol();
      if (!iosColonnesSol()) {
        log.warn("Erreur de prise des colonnes au sol");
        return PriseGradinState.ERREUR_COLONNES;
      }
      log.info(" - Vérouillage des colonnes au sol");
      verrouillageColonnesSol();
      return PriseGradinState.OK;

    } finally {
      updateStockRobot(true);

    }
  }

  public void deposeGradin(Point rangPosition, ConstructionArea.Etage etage, int nbEtageRequis) {

  }
}
