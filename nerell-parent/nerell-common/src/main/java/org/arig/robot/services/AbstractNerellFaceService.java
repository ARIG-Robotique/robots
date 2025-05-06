package org.arig.robot.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNerellFaceService {

  protected final NerellRobotStatus rs;
  protected final TrajectoryManager mv;
  protected final NerellRobotServosService servos;
  protected final NerellIOService ioService;

  protected abstract void aligneFaceGradinBrut(GradinBrut gradin) throws AvoidingException;
  protected abstract void ouvreFacePourPrise();
  protected abstract void fermeFaceEchecPrise();
  protected abstract void fermeFaceEchecStockTiroir();
  protected abstract void deplacementPriseColonnesPinces() throws AvoidingException;
  protected abstract void deplacementPriseColonnesSol() throws AvoidingException;
  protected abstract void deplacementEchappementGradinBrut() throws AvoidingException;
  protected abstract boolean checkIOPinces();
  protected abstract void prepareMiseEnStockTiroir();
  protected abstract void miseEnStockTiroir();
  protected abstract boolean checkIOTiroir();
  protected abstract boolean checkIOColonnesSol();
  protected abstract void verrouillageColonnesSol();

  public void preparePriseGradinBrut(GradinBrut gradin) throws AvoidingException {
    log.info("Préparation de la prise du gradin brut : {}", gradin.id());
    // a. Aligne la face qui est disponible
    log.info(" - Alignement de la face");
    aligneFaceGradinBrut(gradin);

    // b. Ouvre les pinces pour la prise
    log.info(" - Ouverture face, pour la prise");
    ouvreFacePourPrise();
  }

  public boolean prendreGradinBrutStockTiroir() throws AvoidingException {
    log.info("Prise du gradin brut pour stock tiroir");
    deplacementPriseColonnesPinces();
    /*if (!checkIOPinces()) {
      log.warn("Erreur de prise du gradin brut");
      log.info(" - Echappement prise du gradin brut");
      deplacementEchappementGradinBrut();
      log.info(" - Fermeture face, échec de la prise");
      fermeFaceEchecPrise();
      return false;
    }*/
    log.info(" - Prepare mise en stock tiroir du gradin brut");
    prepareMiseEnStockTiroir();
    /*if (!checkIOTiroir()) {
      log.warn("Erreur de mise en stock du gradin brut");
      log.info(" - Fermeture face, échec de la mise en stock");
      fermeFaceEchecStockTiroir();
      return false;
    }*/
    log.info(" - Mise en stock du gradin brut");
    miseEnStockTiroir();
    log.info(" - Mise en stock des colonnes au sol");
    deplacementPriseColonnesSol();
    /*if (!checkIOColonnesSol()) {
      log.warn("Erreur de prise des colonnes au sol");
      log.info(" - Fermeture face, échec de la prise des colonnes au sol");
      deplacementEchappementGradinBrut();
    }*/
    log.info(" - Vérouillage des colonnes au sol");
    verrouillageColonnesSol();

    return true;
  }
}
