package org.arig.robot.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Etage;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.PriseGradinState;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockPosition;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNerellFaceService {

  protected static final int DEPL_INIT_PRISE = 130;
  protected static final int DEPL_PRISE_COLONNES_SOL = 90;
  protected static final int DEPL_PRISE_COLONNES_PINCES_1 = 90;
  protected static final int DEPL_PRISE_COLONNES_PINCES_2 = 20;
  protected static final int DEPL_DEPOSE_COLONNES_SOL = 60;
  protected static final int DEPL_DEPOSE_ETAGE = 100;

  protected final NerellRobotStatus rs;
  protected final TrajectoryManager mv;
  protected final NerellRobotServosService servos;
  protected final NerellIOService ioService;

  protected abstract boolean iosPinces();

  protected abstract boolean iosTiroir();

  protected abstract boolean iosColonnesSol();

  protected abstract void updatePincesState(boolean gauche, boolean droite);

  protected abstract void updateColonnesSolState(boolean gauche, boolean droite);

  protected abstract void updateTiroirState(boolean bas, boolean haut);

  protected abstract void aligneFace(Point gradin) throws AvoidingException;

  protected abstract void ouvreFacePourPrise();

  protected abstract void ouvreFacePourPrise2Etages();

  protected abstract void deplacementPriseColonnesPinces() throws AvoidingException;

  protected abstract void deplacementPriseColonnesSol() throws AvoidingException;

  protected abstract void echappementPriseGradinBrut(PriseGradinState state) throws AvoidingException;

  protected abstract void deplacementDeposeInit() throws AvoidingException;

  protected abstract void deplacementDepose2Gradins() throws AvoidingException;

  protected abstract void deplacementDeposeColonnesSol(boolean reverse) throws AvoidingException;

  protected abstract void deplacementDeposeEtage(boolean reverse) throws AvoidingException;

  protected abstract boolean miseEnStockTiroir();

  protected abstract void leverGradin2Etages();

  protected abstract void poserGradin2Etages() throws AvoidingException;

  protected abstract void verrouillageColonnesSol();

  protected abstract void deposeEtage(Etage etage, StockPosition stockPosition) throws AvoidingException;

  public void preparePriseGradinBrut(GradinBrut gradin) throws AvoidingException {
    log.info("Préparation de la prise du gradin brut : {}", gradin.id());
    // a. Aligne la face qui est disponible
    log.info(" - Alignement de la face");
    aligneFace(gradin);

    // b. Ouvre les pinces pour la prise
    log.info(" - Ouverture face, pour la prise");
    ouvreFacePourPrise();
  }

  public PriseGradinState prendreGradinBrutStockTiroir() throws AvoidingException {
    log.info("Prise du gradin brut pour stock tiroir");

    deplacementPriseColonnesPinces();
    if (!iosPinces()) {
      log.warn("Erreur de chargement du gradin brut dans les pinces (G : {} ; D : {})", ioService.pinceAvantGauche(false), ioService.pinceAvantDroite(false));
      echappementPriseGradinBrut(PriseGradinState.ERREUR_PINCES);
      return PriseGradinState.ERREUR_PINCES;
    }
    updatePincesState(true, true);

    log.info(" - Mise en stock du gradin brut");
    if (!miseEnStockTiroir()) {
      log.warn("Erreur de mise en stock du gradin brut (B : {} ; H : {})", ioService.tiroirAvantBas(false), ioService.tiroirAvantHaut(false));
      echappementPriseGradinBrut(PriseGradinState.ERREUR_TIROIR);
      return PriseGradinState.ERREUR_TIROIR;
    }
    updateTiroirState(true, true);

    log.info(" - Mise en stock des colonnes au sol");
    deplacementPriseColonnesSol();
    if (!iosColonnesSol()) {
      log.warn("Erreur de prise des colonnes au sol (G : {} ; D : {})", ioService.solAvantGauche(false), ioService.solAvantDroite(false));
      echappementPriseGradinBrut(PriseGradinState.ERREUR_COLONNES);
      return PriseGradinState.ERREUR_COLONNES;
    }
    updateColonnesSolState(true, true);

    log.info(" - Vérouillage des colonnes au sol");
    verrouillageColonnesSol();
    return PriseGradinState.OK;
  }

  public void prepareDeposeGradin(Point rangPosition, boolean firstDepose) throws AvoidingException {
    aligneFace(rangPosition);
    if (firstDepose) {
      deplacementDeposeInit();
    }
  }

  public void deposeGradin(Etage etage, StockPosition stockPosition) throws AvoidingException {
    deposeEtage(etage, stockPosition);
  }

  public PriseGradinState reprise2Gradin(Rang rangReprise, Etage etage) throws AvoidingException {
    log.info("Rerise du gradin 2 etages {} pour stock dépose 3 etages", rangReprise);
    log.info(" - Préparation de la reprise du gradin 2 étages");
    ouvreFacePourPrise2Etages();

    log.info(" - Prises des colonnes dans les pinces");
    deplacementPriseColonnesPinces();
    if (!iosPinces()) {
      log.warn("Erreur de chargement du gradin 2 étages dans les pinces (G : {} ; D : {})", ioService.pinceAvantGauche(false), ioService.pinceAvantDroite(false));
      deplacementDeposeEtage(false);
      return PriseGradinState.ERREUR_PINCES;
    }
    updatePincesState(true, true);

    log.info(" - Lever des deux étages du gradin");
    leverGradin2Etages();

    return PriseGradinState.OK;
  }

  public void depose2Gradins(Rang rangDepose, Etage etage) throws AvoidingException {
    log.info("Dépose de 2 gradins sur étage 1 du {}", rangDepose);
    log.info(" - Déplacement de dépose des colonnes sur l'étage 1");
    deplacementDepose2Gradins();

    log.info(" - Dépose de gradin de niveau 3");
    poserGradin2Etages();
  }
}
