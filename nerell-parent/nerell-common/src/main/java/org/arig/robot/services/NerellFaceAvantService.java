package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
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
    servos.groupeBlockColonneAvantOuvert(false);
    servos.groupePincesAvantPrise(true);
    servos.ascenseurAvantBas(false);
    servos.groupeDoigtsAvantLache(false);
  }

  @Override
  protected void deplacementPriseColonnesPinces() throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(20, 100);
    mv.avanceMM(90);
    mv.setVitessePercent(0, 100);
    mv.avanceMM(10);
  }

  @Override
  protected void deplacementPriseColonnesSol() throws AvoidingException {
    rs.enableCalage(TypeCalage.AVANT, TypeCalage.FORCE);
    mv.setVitessePercent(70, 100);
    mv.avanceMM(90);
  }

  @Override
  protected void echappementPriseGradinBrut(PriseGradinState state) throws AvoidingException {
    log.info("Echappement de la prise du gradin brut. Erreur {}", state.name());
    mv.setVitessePercent(100, 100);
    mv.reculeMM(100);

    servos.groupeBlockColonneAvantOuvert(false);
    servos.tiroirAvantStock(false);
    servos.becAvantFerme(false);
    servos.ascenseurAvantRepos(false);
    servos.groupeDoigtsAvantFerme(false);
    servos.groupePincesAvantRepos(false);
  }

  @Override
  protected void deplacementDeposeColonnesSol(boolean reverse) throws AvoidingException {
    rs.enableCalage(TypeCalage.FORCE);
    mv.setVitessePercent(100, 100);
    if (!reverse) {
      mv.reculeMM(50);
    } else {
      mv.avanceMM(50);
    }
  }

  protected void deplacementDeposeEtage() throws AvoidingException {
    mv.setVitessePercent(100, 100);
    mv.reculeMM(100);
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
        servos.becAvantOuvert(true);
        servos.tiroirAvantPrise(true);
        servos.ascenseurAvantHaut(true);

        servos.becAvantFerme(true);
        servos.becAvantOuvert(true);
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
  protected void deposeEtage(ConstructionArea.Etage etage) throws AvoidingException {
    log.info("Dépose de l'étage {}", etage.name());
    if (!ioService.tiroirAvantBas(true)) {
      log.warn("Tiroir avant bas vide, on ne peut pas déposer");
      return;
    }

    if (ioService.tiroirAvantBas(true) &&
        ioService.solAvantGauche(true) &&
        ioService.solAvantDroite(true) &&
        !ioService.pinceAvantGauche(true) &&
        !ioService.pinceAvantDroite(true)
    ) {
      log.info("Récupération des colonnes en stock depuis le sol");
      servos.groupePincesAvantPriseSol(false);
      servos.groupeDoigtsAvantOuvert(false);
      servos.groupeBlockColonneAvantOuvert(true);
      deplacementDeposeColonnesSol(false);
      servos.ascenseurAvantBas(true);
      deplacementDeposeColonnesSol(true);
      servos.groupePincesAvantStock(true);
      servos.groupeDoigtsAvantSerre(true);
    }

    log.info("Dépose de l'étage {} depuis les pinces", etage.name());
    servos.groupePincesAvantPrise(true);
    servos.ascenseurAvantHaut(true);
    servos.tiroirAvantDepose(true);
    servos.becAvantOuvert(true);

    if (ioService.tiroirAvantHaut(true)) {
      log.info("Split tiroir avant pour la dépose");
      servos.ascenseurAvantSplit(true);
      servos.becAvantFerme(true);
      servos.tiroirAvantStock(true);
      if (etage == ConstructionArea.Etage.ETAGE_1) {
        servos.ascenseurAvantBas(true);
      } else {
        servos.ascenseurAvantEtage2(true);
      }
    } else {
      log.info("Pas de split tiroir");
      if (etage == ConstructionArea.Etage.ETAGE_1) {
        servos.ascenseurAvantBas(true);
      } else {
        servos.ascenseurAvantEtage2(true);
      }
      servos.becAvantFerme(false);
      servos.tiroirAvantStock(false);
    }

    servos.groupeDoigtsAvantLache(true);
    deplacementDeposeEtage();
    servos.groupeDoigtsAvantFerme(false);
      if (ioService.solAvantDroite(true) || ioService.solAvantGauche(true)) {
        servos.ascenseurAvantStock(false);
      } else {
      servos.ascenseurAvantRepos(false);
    }
    servos.groupePincesAvantRepos(false);
  }
}
