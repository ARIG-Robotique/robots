package org.arig.robot.services;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Component
public class NerellFaceAvantService extends AbstractNerellFaceService {

  public NerellFaceAvantService(NerellRobotStatus rs, TrajectoryManager mv,
                                NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFaceGradinBrut(GradinBrut gradin) throws AvoidingException {
    mv.alignFrontTo(gradin);
  }

  @Override
  protected void ouvreFacePourPrise() {
    servos.tiroirAvantOuvert(false);
    servos.becAvantOuvert(false);
    servos.groupeBlockColonneAvantOuvert(false);
    servos.groupePincesAvantOuvertPrise(true);
    servos.ascenseurAvantBas(false);
    servos.groupeDoigtsAvantOuvert(true);
  }

  @Override
  protected void fermeFaceEchecPrise() {
    servos.groupeBlockColonneAvantOuvert(false);
    servos.groupeDoigtsAvantFerme(true);
    servos.ascenseurAvantBasPrise(true);
    servos.groupePincesAvantFerme(true);
    servos.tiroirAvantStock(false);
    servos.becAvantFerme(false);
  }

  @Override
  protected void fermeFaceEchecStockTiroir() {
    fermeFaceEchecPrise();
  }

  @Override
  protected void deplacementPriseColonnesPinces() throws AvoidingException {
    rs.enableCalageBordure(TypeCalage.FORCE);
    mv.setVitessePercent(20, 100);
    mv.avanceMM(90);
  }

  @Override
  protected void deplacementPriseColonnesSol() throws AvoidingException {
    rs.enableCalageBordure(TypeCalage.AVANT, TypeCalage.FORCE);
    mv.setVitessePercent(70, 100);
    mv.avanceMM(90);
  }

  @Override
  protected void deplacementEchappementGradinBrut() throws AvoidingException {
    mv.setVitessePercent(100, 100);
    mv.reculeMM(100);
  }

  @Override
  protected boolean checkIOPinces() {
    return ThreadUtils.waitUntil(
      () -> ioService.pinceAvantGauche(true) && ioService.pinceArriereDroite(true),
      20, 1000
    );
  }

  @Override
  protected void prepareMiseEnStockTiroir() {
    servos.groupeDoigtsAvantPrise(true);
    servos.ascenseurAvantHaut(true);
  }

  @Override
  protected void miseEnStockTiroir() {
    servos.becAvantFerme(true);
    servos.tiroirAvantStock(true);
    servos.ascenseurAvantStock(true);
    servos.groupePincesAvantStock(false);
  }

  @Override
  protected boolean checkIOTiroir() {
    return ThreadUtils.waitUntil(
        () -> ioService.tiroirAvantBas(true) && ioService.tiroirAvantHaut(true),
        20, 1000
    );
  }

  @Override
  protected boolean checkIOColonnesSol() {
    return ThreadUtils.waitUntil(
        () -> ioService.solAvantGauche(true) && ioService.solAvantDroite(true),
        20, 1000
    );
  }

  @Override
  protected void verrouillageColonnesSol() {
    servos.groupeBlockColonneAvantFerme(true);
  }
}
