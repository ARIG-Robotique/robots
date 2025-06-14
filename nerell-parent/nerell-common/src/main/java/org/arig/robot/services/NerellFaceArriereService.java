package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.Etage;
import org.arig.robot.model.PriseGradinState;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockFace;
import org.arig.robot.model.StockPosition;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.utils.ThreadUtils;

@Slf4j
public class NerellFaceArriereService extends AbstractNerellFaceService {

  public NerellFaceArriereService(NerellRobotStatus rs, TrajectoryManager mv,
                                  NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFace(Point gradin) throws AvoidingException {
    mv.alignBackTo(gradin);
  }

  @Override
  protected void ouvreFacePourPrise() {
    servos.tiroirArrierePrise(false);
    servos.becArriereOuvert(false);
    servos.groupeDoigtsArriereLache(false);
    servos.ascenseurArriereBas(false);
    servos.groupePincesArrierePrise(true);
  }

  @Override
  protected void deplacementPriseColonnesPinces() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(20, 100);
    mv.reculeMM(DEPL_PRISE_COLONNES_PINCES_1);
    mv.setVitessePercent(0, 100);
    mv.reculeMM(DEPL_PRISE_COLONNES_PINCES_2);
  }

  @Override
  protected void deplacementPriseColonnesSol() throws AvoidingException {
    rs.enableCalage(TypeCalage.ARRIERE, TypeCalage.FORCE);
    mv.setVitessePercent(10, 100);
    mv.reculeMM(DEPL_PRISE_COLONNES_SOL);
  }

  @Override
  protected void echappementPriseGradinBrut(PriseGradinState state) throws AvoidingException {
    log.info("Echappement de la prise du gradin brut. Erreur {}", state.name());

    servos.groupeBlockColonneArriereOuvert(false);
    servos.becArriereOuvert(false);
    servos.tiroirArriereDepose(false);
    servos.groupeDoigtsArriereLache(false);
    servos.groupePincesArrierePrise(false);

    mv.setVitessePercent(100, 100);
    mv.avanceMM(DEPL_DEPOSE_ETAGE);

    servos.becArriereFerme(false);
    servos.ascenseurArriereRepos(false);
    servos.groupeDoigtsArriereLache(false);
    servos.ascenseurArriereReposHaut(false);
    servos.groupePincesArriereStock(false);
    servos.tiroirArriereStock(false);
  }

  @Override
  protected void deplacementDeposeInit() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(100, 100);
    mv.reculeMM(DEPL_INIT_PRISE);
  }

  @Override
  protected void deplacementDepose2Gradins() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(20, 100);
    mv.reculeMM(DEPL_INIT_PRISE);
  }

  @Override
  protected void deplacementDeposeColonnesSol(boolean reverse) throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(100, 100);
    if (!reverse) {
      mv.avanceMM(DEPL_DEPOSE_COLONNES_SOL);
    } else {
      mv.reculeMM(DEPL_DEPOSE_COLONNES_SOL);
    }
  }

  protected void deplacementDeposeEtage(boolean reverse) throws AvoidingException {
    mv.setVitessePercent(100, 100);
    if (!reverse) {
      mv.avanceMM(DEPL_DEPOSE_ETAGE);
    } else {
      mv.reculeMM(DEPL_DEPOSE_ETAGE);
    }
  }

  @Override
  protected boolean iosPinces() {
    return ThreadUtils.waitUntil(
      () -> ioService.pinceArriereGauche(true) && ioService.pinceArriereDroite(true),
      20, 1000
    );
  }

  @Override
  protected boolean iosTiroir() {
    return ThreadUtils.waitUntil(
      () -> ioService.tiroirArriereBas(true) && ioService.tiroirArriereHaut(true),
      20, 1000
    );
  }

  @Override
  protected boolean iosColonnesSol() {
    return ThreadUtils.waitUntil(
      () -> ioService.solArriereGauche(true) && ioService.solArriereDroite(true),
      20, 1000
    );
  }

  @Override
  protected void updatePincesState(boolean gauche, boolean droite) {
    log.info("Mise à jour du state des pinces : G {} ; D {}", gauche, droite);
    rs.faceArriere().pinceGauche(gauche).pinceDroite(droite);
  }

  @Override
  protected void updateColonnesSolState(boolean gauche, boolean droite) {
    log.info("Mise à jour du state des colonnes sol : G {} ; D {}", gauche, droite);
    rs.faceArriere().solGauche(gauche).solDroite(droite);
  }

  @Override
  protected void updateTiroirState(boolean bas, boolean haut) {
    log.info("Mise à jour du state du tiroir : B {} ; H {}", bas, haut);
    rs.faceArriere().tiroirBas(bas).tiroirHaut(haut);
  }

  @Override
  protected boolean miseEnStockTiroir() {
    servos.groupeDoigtsArriereSerre(true);
    servos.ascenseurArriereHaut(true);
    if (!iosTiroir()) {
      log.warn("Erreur de mise en stock du tiroir arriere");
      return false;
    }

    int nbTries = 1;
    do {
      if (nbTries > 1) {
        log.info(" - Réouverture du tiroir arriere pour le stock. Essai n°{}", nbTries);
        servos.tiroirArrierePrise(true, true);
        servos.becArriereOuvert(true);
        servos.tiroirArriereDepose(true, true);
        servos.ascenseurArriereHaut(true);

        servos.becArriereFerme(true);
        servos.becArriereOuvert(true);
      } else {
        log.info(" - Ouverture du tiroir pour mise en stock. Essai n°{}", nbTries);
        servos.tiroirArriereDepose(true, true);
      }
      servos.becArriereFerme(true);
      servos.tiroirArriereStock(true);
      servos.ascenseurArriereStock(true);

      if (nbTries++ > 3) {
        break;
      }
    } while (!iosTiroir());

    servos.groupePincesArriereStock(false);
    return nbTries <= 3;
  }

  @Override
  protected void verrouillageColonnesSol() {
    servos.groupeBlockColonneArriereFerme(true);
  }

  @Override
  protected void deposeEtage(Etage etage, StockPosition stockPosition) throws AvoidingException {
    StockFace face = rs.faceArriere();
    log.info("Tentative de dépose face ARRIERE de l'étage {} depuis le stock {}", etage.name(), stockPosition.name());
    if (!face.tiroirBas()) {
      log.warn("Tiroir arriere bas vide, on ne peut pas déposer");
      return;
    }

    if (stockPosition == StockPosition.BOTTOM || stockPosition == StockPosition.BOTTOM_FAST) {
      log.info("Récupération des colonnes en stock depuis le sol");
      servos.groupeBlockColonneArrierePriseSol(false);
      servos.groupePincesArrierePriseSol(false);
      servos.groupeDoigtsArriereOuvert(true);
      deplacementDeposeColonnesSol(false);
      servos.ascenseurArriereBas(true);
      servos.groupeDoigtsArrierePriseSol(false);
      deplacementDeposeColonnesSol(true);
      servos.groupePincesArriereStock(true);
      servos.groupeDoigtsArriereSerre(true);
      updateColonnesSolState(false, false);
      updatePincesState(true, true);
      servos.ascenseurArriereStock(true);
      servos.tiroirArriereLibreAutreTiroir(false);
      servos.groupePincesArrierePrise(true);
      servos.ascenseurArriereHaut(true);
      servos.groupeBlockColonneArriereOuvert(false);
      if (etage == Etage.ETAGE_2) {
        deplacementDeposeEtage(true);
      }
    }

    log.info("Dépose de l'étage {} depuis les pinces", etage.name());
    servos.tiroirArriereLibreAutreTiroir(false);
    servos.groupePincesArrierePrise(true);
    servos.ascenseurArriereHaut(true);
    servos.tiroirArriereDepose(true);
    servos.becArriereOuvert(false);

    if (face.tiroirHaut()) {
      log.info("Split tiroir arriere pour la dépose");
      servos.ascenseurArriereSplit(true);
      servos.becArriereFerme(true);
      servos.tiroirArriereStock(true);
      updateTiroirState(true, false);
      if (etage == Etage.ETAGE_1) {
        servos.ascenseurArriereBas(true);
      } else {
        servos.ascenseurArriereEtage2(true);
      }
    } else {
      log.info("Pas de split tiroir");
      if (etage == Etage.ETAGE_1) {
        servos.ascenseurArriereBas(true);
        servos.tiroirArriereStock(false);
      } else {
        servos.ascenseurArriereEtage2(true);
        servos.tiroirArriereStock(true);
      }
      updateTiroirState(false, false);
    }

    if (etage == Etage.ETAGE_1 && stockPosition == StockPosition.TOP) {
      servos.groupeDoigtsArriereSuperOuvert(true);
    } else {
      servos.groupeDoigtsArriereLache(true);
    }

    if (etage == Etage.ETAGE_1) {
      ThreadUtils.sleep(250);
    }
    deplacementDeposeEtage(false);
    updatePincesState(false, false);
    servos.becArriereFerme(false);
    servos.groupeDoigtsArriereFerme(false);
    servos.ascenseurArriereStock(true);
    servos.groupePincesArriereRepos(false);
  }

  @Override
  protected void ouvreFacePourPrise2Etages() {
    servos.groupePincesArrierePrise(false);
    servos.groupeDoigtsArriereLache(false);
    if (ioService.tiroirAvantBas(true)) {
      servos.groupePincesAvantPrise(true);
      servos.ascenseurAvantHaut(false);
    }
    servos.tiroirAvantLibreAutreTiroir(false);
    servos.tiroirArriereLever2Etages(false);
    servos.ascenseurArriereBas(true);
  }

  @Override
  protected void leverGradin2Etages() {
    servos.groupeDoigtsArriereSerre(true);
    servos.becArriereLever2Etages(true);
    servos.ascenseurArriereHaut(true);
  }

  @Override
  protected void poserGradin2Etages() throws AvoidingException {
    servos.ascenseurArriereEtage2(true);
    servos.groupeDoigtsArriereLache(true);
    deplacementDeposeEtage(false);
    updatePincesState(false, false);
    servos.groupeDoigtsArriereFerme(false);
    servos.tiroirArriereStock(false);
    servos.becArriereFerme(false);
    servos.tiroirAvantStock(false);
    if (ioService.tiroirAvantBas(true)) {
      servos.ascenseurAvantStock(true);
      servos.groupePincesAvantStock(false);
    }
    servos.ascenseurArriereRepos(true);
    servos.groupePincesArriereRepos(false);
  }
}
