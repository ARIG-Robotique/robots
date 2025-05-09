package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
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
    servos.tiroirAvantOuvert(false);
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
  }

  @Override
  protected void deplacementPriseColonnesSol() throws AvoidingException {
    rs.enableCalage(TypeCalage.AVANT, TypeCalage.FORCE);
    mv.setVitessePercent(70, 100);
    mv.avanceMM(90);
  }

  protected void deplacementDeposeEtage() throws AvoidingException {
    mv.setVitessePercent(100, 100);
    mv.reculeMM(90);
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
        servos.tiroirAvantOuvert(true);
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
  protected void deverouillageColonnesSol() {
    servos.groupeBlockColonneAvantOuvert(true);
  }

  @Override
  protected void deposeEtage1() throws AvoidingException {
    if (ioService.tiroirAvantBas(true) &&
        ioService.pinceAvantGauche(true) &&
        ioService.pinceAvantDroite(true)
    ) {
      log.info("Dépose de l'étage 1 depuis les pinces");
      servos.groupePincesAvantPrise(true);
      servos.ascenseurAvantHaut(true);
      servos.tiroirAvantOuvert(true);
      servos.becAvantOuvert(true);
      // TODO : Optimiser split
      servos.ascenseurAvantSplit(true);
      servos.becAvantFerme(true);
      servos.tiroirAvantStock(true);
      // FIN TODO
      servos.ascenseurAvantBas(true);
      servos.groupeDoigtsAvantLache(true);
      deplacementDeposeEtage();
      servos.groupeDoigtsAvantFerme(false);
      if (ioService.solAvantDroite(true) || ioService.solAvantGauche(true)) {
        servos.ascenseurAvantStock(false);
      } else {
        servos.ascenseurAvantRepos(false);
      }
      servos.groupePincesAvantRepos(false);

      return;
    }
    if (ioService.tiroirAvantBas(true) &&
        ioService.solAvantGauche(true) &&
        ioService.solAvantDroite(true)
    ) {
      log.info("Dépose de l'étage 1 depuis le sol");
      // TODO
    }
  }

  @Override
  protected void deposeEtage2() throws AvoidingException {
    if (ioService.tiroirAvantBas(true) &&
        ioService.pinceAvantGauche(true) &&
        ioService.pinceAvantDroite(true)
    ) {
      log.info("Dépose de l'étage 2 depuis les pinces");
      servos.groupePincesAvantPrise(true);
      servos.ascenseurAvantHaut(true);
      servos.tiroirAvantOuvert(true);
      servos.becAvantOuvert(true);
      // TODO : Optimiser split
      servos.ascenseurAvantSplit(true);
      servos.becAvantFerme(true);
      servos.tiroirAvantStock(true);
      // FIN TODO
      servos.ascenseurAvantBas(true);
      servos.groupeDoigtsAvantLache(true);
      deplacementDeposeEtage();
      servos.groupeDoigtsAvantFerme(false);
      if (ioService.solAvantDroite(true) || ioService.solAvantGauche(true)) {
        servos.ascenseurAvantStock(false);
      } else {
        servos.ascenseurAvantRepos(false);
      }
      servos.groupePincesAvantRepos(false);

      return;
    }
    if (ioService.tiroirAvantBas(true) &&
        ioService.solAvantGauche(true) &&
        ioService.solAvantDroite(true)
    ) {
      log.info("Dépose de l'étage 1 depuis le sol");
      // TODO
    }
  }
}
