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
public class NerellFaceAvantService extends AbstractNerellFaceService {

  public NerellFaceAvantService(NerellRobotStatus rs, TrajectoryManager mv,
                                NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFace(Point gradin) throws AvoidingException {
    mv.alignFrontTo(gradin);
  }

  @Override
  protected void ouvreFacePourPrise() {
    servos.tiroirAvantPrise(false);
    servos.becAvantOuvert(false);
    servos.groupeDoigtsAvantLache(false);
    servos.ascenseurAvantBas(false);
    servos.groupePincesAvantPrise(true);
  }

  @Override
  protected void deplacementPriseColonnesPinces() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(20, 100);
    mv.avanceMM(DEPL_PRISE_COLONNES_PINCES_1);
    mv.setVitessePercent(0, 100);
    mv.avanceMM(DEPL_PRISE_COLONNES_PINCES_2);
  }

  @Override
  protected void deplacementPriseColonnesSol() throws AvoidingException {
    rs.enableCalage(TypeCalage.AVANT, TypeCalage.FORCE);
    mv.setVitessePercent(10, 100);
    mv.avanceMM(DEPL_PRISE_COLONNES_SOL);
  }

  @Override
  protected void echappementPriseGradinBrut(PriseGradinState state) throws AvoidingException {
    log.info("Echappement de la prise du gradin brut. Erreur {}", state.name());

    servos.groupeBlockColonneAvantOuvert(false);
    servos.becAvantOuvert(false);
    servos.tiroirAvantDepose(false);
    servos.groupeDoigtsAvantLache(false);
    servos.groupePincesAvantPrise(false);

    mv.setVitessePercent(100, 100);
    mv.reculeMM(DEPL_DEPOSE_ETAGE);

    servos.becAvantFerme(false);
    servos.ascenseurAvantRepos(false);
    servos.groupeDoigtsAvantLache(false);
    servos.ascenseurAvantReposHaut(false);
    servos.groupePincesAvantStock(false);
    servos.tiroirAvantStock(false);
  }

  @Override
  protected void deplacementDeposeInit() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(100, 100);
    mv.avanceMM(DEPL_INIT_PRISE);
  }

  @Override
  protected void deplacementDepose2Gradins() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(20, 100);
    mv.avanceMM(DEPL_INIT_PRISE);
  }

  @Override
  protected void deplacementDeposeColonnesSol(boolean reverse) throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(100, 100);
    if (!reverse) {
      mv.reculeMM(DEPL_DEPOSE_COLONNES_SOL);
    } else {
      mv.avanceMM(DEPL_DEPOSE_COLONNES_SOL);
    }
  }

  protected void deplacementDeposeEtage(boolean reverse) throws AvoidingException {
    mv.setVitessePercent(100, 100);
    if (!reverse) {
      mv.reculeMM(DEPL_DEPOSE_ETAGE);
    } else {
      mv.avanceMM(DEPL_DEPOSE_ETAGE);
    }
  }

  @Override
  protected boolean iosPinces() {
    return ThreadUtils.waitUntil(
      () -> ioService.pinceAvantGauche(true) && ioService.pinceAvantDroite(true),
      20, 1000
    );
  }

  @Override
  protected boolean iosTiroir() {
    return ThreadUtils.waitUntil(
      () -> ioService.tiroirAvantBas(true) && ioService.tiroirAvantHaut(true),
      20, 1000
    );
  }

  @Override
  protected boolean iosColonnesSol() {
    return ThreadUtils.waitUntil(
      () -> ioService.solAvantGauche(true) && ioService.solAvantDroite(true),
      20, 1000
    );
  }

  @Override
  protected void updatePincesState(boolean gauche, boolean droite) {
    log.info("Mise à jour du state des pinces : G {} ; D {}", gauche, droite);
    rs.faceAvant().pinceGauche(gauche).pinceDroite(droite);
  }

  @Override
  protected void updateColonnesSolState(boolean gauche, boolean droite) {
    log.info("Mise à jour du state des colonnes sol : G {} ; D {}", gauche, droite);
    rs.faceAvant().solGauche(gauche).solDroite(droite);
  }

  @Override
  protected void updateTiroirState(boolean bas, boolean haut) {
    log.info("Mise à jour du state du tiroir : B {} ; H {}", bas, haut);
    rs.faceAvant().tiroirBas(bas).tiroirHaut(haut);
  }

  @Override
  protected boolean miseEnStockTiroir() {
    servos.groupeDoigtsAvantSerre(true);
    servos.ascenseurAvantHaut(true);
    if (!iosTiroir()) {
      log.warn("Erreur de mise en stock du tiroir avant");
      return false;
    }

    int nbTries = 1;
    do {
      if (nbTries > 1) {
        log.info(" - Réouverture du tiroir avant pour le stock. Essai n°{}", nbTries);
        servos.tiroirAvantPrise(true, true);
        servos.becAvantOuvert(true);
        servos.tiroirAvantDepose(true, true);
        servos.ascenseurAvantHaut(true);

        servos.becAvantFerme(true);
        servos.becAvantOuvert(true);
      } else {
        log.info(" - Ouverture du tiroir pour mise en stock. Essai n°{}", nbTries);
        servos.tiroirAvantDepose(true, true);
      }
      servos.becAvantFerme(true);
      servos.tiroirAvantStock(true);
      servos.ascenseurAvantStock(true);

      if (nbTries++ > 3) {
        break;
      }
    } while (!iosTiroir());

    servos.groupePincesAvantStock(false);
    return nbTries <= 3;
  }

  @Override
  protected void verrouillageColonnesSol() {
    servos.groupeBlockColonneAvantFerme(true);
  }

  @Override
  protected void deposeEtage(Etage etage, StockPosition stockPosition) throws AvoidingException {
    StockFace face = rs.faceAvant();
    log.info("Tentative de dépose face AVANT de l'étage {} depuis le stock {}", etage.name(), stockPosition.name());
    if (!face.tiroirBas()) {
      log.warn("Tiroir avant bas vide, on ne peut pas déposer");
      return;
    }

    if (stockPosition == StockPosition.BOTTOM || stockPosition == StockPosition.BOTTOM_FAST) {
      log.info("Récupération des colonnes en stock depuis le sol");
      servos.groupeBlockColonneAvantPriseSol(false);
      servos.groupePincesAvantPriseSol(false);
      servos.groupeDoigtsAvantOuvert(false);
      deplacementDeposeColonnesSol(false);
      servos.ascenseurAvantBas(true);
      servos.groupeDoigtsAvantPriseSol(false);
      deplacementDeposeColonnesSol(true);
      servos.groupePincesAvantStock(true);
      servos.groupeDoigtsAvantSerre(true);
      updateColonnesSolState(false, false);
      updatePincesState(true, true);
      servos.ascenseurAvantStock(true);
      servos.tiroirAvantLibreAutreTiroir(false);
      servos.groupePincesAvantPrise(true);
      servos.ascenseurAvantHaut(true);
      servos.groupeBlockColonneAvantOuvert(false);
      if (etage == Etage.ETAGE_2) {
        deplacementDeposeEtage(true);
      }
    }

    log.info("Dépose de l'étage {} depuis les pinces", etage.name());
    servos.tiroirAvantLibreAutreTiroir(false);
    servos.groupePincesAvantPrise(true);
    servos.ascenseurAvantHaut(true);
    servos.tiroirAvantDepose(true);
    servos.becAvantOuvert(false);

    if (face.tiroirHaut()) {
      log.info("Split tiroir avant pour la dépose");
      servos.ascenseurAvantSplit(true);
      servos.becAvantFerme(true);
      servos.tiroirAvantStock(true);
      updateTiroirState(true, false);
      if (etage == Etage.ETAGE_1) {
        servos.ascenseurAvantBas(true);
      } else {
        servos.ascenseurAvantEtage2(true);
      }
    } else {
      log.info("Pas de split tiroir");
      if (etage == Etage.ETAGE_1) {
        servos.ascenseurAvantBas(true);
        servos.tiroirAvantStock(false);
      } else {
        servos.ascenseurAvantEtage2(true);
        servos.tiroirAvantStock(true);
      }
      updateTiroirState(false, false);
    }

    if (etage == Etage.ETAGE_1 && stockPosition == StockPosition.TOP) {
      servos.groupeDoigtsAvantSuperOuvert(true);
    } else {
      servos.groupeDoigtsAvantLache(true);
    }

    if (etage == Etage.ETAGE_1) {
      ThreadUtils.sleep(250);
    }
    deplacementDeposeEtage(false);
    updatePincesState(false, false);
    servos.becAvantFerme(false);
    servos.groupeDoigtsAvantFerme(false);
    servos.ascenseurAvantStock(true);
    servos.groupePincesAvantRepos(false);
  }

  @Override
  protected void ouvreFacePourPrise2Etages() {
    servos.groupePincesAvantPrise(false);
    servos.groupeDoigtsAvantLache(false);
    if (ioService.tiroirArriereBas(true)) {
      servos.groupePincesArrierePrise(true);
      servos.ascenseurArriereHaut(false);
    }
    servos.tiroirArriereLibreAutreTiroir(false);
    servos.tiroirAvantLever2Etages(false);
    servos.ascenseurAvantBas(true);
  }

  @Override
  protected void leverGradin2Etages() {
    servos.groupeDoigtsAvantSerre(true);
    servos.becAvantLever2Etages(true);
    servos.ascenseurAvantHaut(true);
  }

  @Override
  protected void poserGradin2Etages() throws AvoidingException {
    servos.ascenseurAvantEtage2(true);
    servos.groupeDoigtsAvantLache(true);
    deplacementDeposeEtage(false);
    updatePincesState(false, false);
    servos.groupeDoigtsAvantFerme(false);
    servos.tiroirAvantStock(false);
    servos.becAvantFerme(false);
    servos.tiroirArriereStock(true);
    if (ioService.tiroirArriereBas(true)) {
      servos.ascenseurArriereStock(true);
      servos.groupePincesArriereStock(false);
    }
    servos.ascenseurAvantRepos(true);
    servos.groupePincesAvantRepos(false);
  }
}
