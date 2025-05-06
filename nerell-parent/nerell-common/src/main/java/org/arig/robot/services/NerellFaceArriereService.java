package org.arig.robot.services;

import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;

public class NerellFaceArriereService extends AbstractNerellFaceService {

  public NerellFaceArriereService(NerellRobotStatus rs, TrajectoryManager mv,
                                  NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFaceGradinBrut(GradinBrut gradin) {

  }

  @Override
  protected void ouvreFacePourPrise() {

  }

  @Override
  protected void fermeFaceEchecPrise() {

  }

  @Override
  protected void fermeFaceEchecStockTiroir() {

  }

  @Override
  protected void deplacementPriseColonnesPinces() {

  }

  @Override
  protected void deplacementPriseColonnesSol() {

  }

  @Override
  protected void deplacementEchappementGradinBrut() {

  }

  @Override
  protected boolean checkIOPinces() {
    return false;
  }

  @Override
  protected void prepareMiseEnStockTiroir() {

  }

  @Override
  protected void miseEnStockTiroir() {

  }

  @Override
  protected boolean checkIOTiroir() {
    return false;
  }

  @Override
  protected boolean checkIOColonnesSol() {
    return false;
  }

  @Override
  protected void verrouillageColonnesSol() {

  }
}
