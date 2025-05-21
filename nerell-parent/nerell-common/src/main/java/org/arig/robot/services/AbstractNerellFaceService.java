package org.arig.robot.services;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellPriseGradinState;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractNerellFaceService {

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

  protected abstract void deplacementPriseColonnesPinces() throws AvoidingException;

  protected abstract void deplacementPriseColonnesSol() throws AvoidingException;

  protected abstract void echappementPriseGradinBrut(NerellPriseGradinState state) throws AvoidingException;

  protected abstract void deplacementDeposeInit() throws AvoidingException;

  protected abstract void deplacementDeposeColonnesSol(boolean reverse) throws AvoidingException;

  protected abstract void deplacementDeposeEtage() throws AvoidingException;

  protected abstract void deplacementDeposeEtage2() throws AvoidingException;

  protected abstract boolean miseEnStockTiroir();

  protected abstract void verrouillageColonnesSol();

  protected abstract void deposeEtage(ConstructionArea.Etage etage) throws AvoidingException;

  public void preparePriseGradinBrut(GradinBrut gradin) throws AvoidingException {
    log.info("Préparation de la prise du gradin brut : {}", gradin.id());
    // a. Aligne la face qui est disponible
    log.info(" - Alignement de la face");
    aligneFace(gradin);

    // b. Ouvre les pinces pour la prise
    log.info(" - Ouverture face, pour la prise");
    ouvreFacePourPrise();
  }

  public NerellPriseGradinState prendreGradinBrutStockTiroir() throws AvoidingException {
    log.info("Prise du gradin brut pour stock tiroir");

    deplacementPriseColonnesPinces();
    if (!iosPinces()) {
      log.warn("Erreur de chargement du gradin brut dans les pinces (G : {} ; D : {})", ioService.pinceAvantGauche(false), ioService.pinceAvantDroite(false));
      echappementPriseGradinBrut(NerellPriseGradinState.ERREUR_PINCES);
      return NerellPriseGradinState.ERREUR_PINCES;
    }
    updatePincesState(true, true);

    log.info(" - Mise en stock du gradin brut");
    if (!miseEnStockTiroir()) {
      log.warn("Erreur de mise en stock du gradin brut (B : {} ; H : {})", ioService.tiroirAvantBas(false), ioService.tiroirAvantHaut(false));
      echappementPriseGradinBrut(NerellPriseGradinState.ERREUR_TIROIR);
      return NerellPriseGradinState.ERREUR_TIROIR;
    }
    updateTiroirState(true, true);

    log.info(" - Mise en stock des colonnes au sol");
    deplacementPriseColonnesSol();
    if (!iosColonnesSol()) {
      log.warn("Erreur de prise des colonnes au sol (G : {} ; D : {})", ioService.solAvantGauche(false), ioService.solAvantDroite(false));
      echappementPriseGradinBrut(NerellPriseGradinState.ERREUR_COLONNES);
      return NerellPriseGradinState.ERREUR_COLONNES;
    }
    updateColonnesSolState(true, true);

    log.info(" - Vérouillage des colonnes au sol");
    verrouillageColonnesSol();
    return NerellPriseGradinState.OK;
  }

  public void deposeGradin(ConstructionArea constructionArea, Point rangPosition,
                           ConstructionArea.Rang rang, ConstructionArea.Etage etage,
                           int nbEtageRequis) throws AvoidingException {
    aligneFace(rangPosition);
    deplacementDeposeInit();
    deposeEtage(etage);
    constructionArea.addGradin(rang, etage);
    if (nbEtageRequis == 2 && etage == ConstructionArea.Etage.ETAGE_1) {
      deposeEtage(ConstructionArea.Etage.ETAGE_2);
      constructionArea.addGradin(rang, ConstructionArea.Etage.ETAGE_2);
    }
  }
}
