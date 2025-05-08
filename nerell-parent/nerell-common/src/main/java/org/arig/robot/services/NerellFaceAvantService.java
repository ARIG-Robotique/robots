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

  // TODO : Mettre dans un scheduler
  @Override
  protected void updateStockRobot(boolean expectedSimulator) {
    rs.faceAvant()
        .pinceDroite(ioService.pinceAvantDroite(expectedSimulator))
        .pinceGauche(ioService.pinceAvantGauche(expectedSimulator))
        .solGauche(ioService.solAvantGauche(expectedSimulator))
        .solDroite(ioService.solAvantDroite(expectedSimulator))
        .tiroirBas(ioService.tiroirAvantBas(expectedSimulator))
        .tiroirHaut(ioService.tiroirAvantHaut(expectedSimulator));
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
}
