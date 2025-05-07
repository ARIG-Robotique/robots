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
  protected void deplacementPriseColonnesPinces() {

  }

  @Override
  protected void deplacementPriseColonnesSol() {

  }

  @Override
  protected void updateStockRobot(boolean expectedSimulator) {

  }

  @Override
  protected boolean checkIOsPinces() {
    return false;
  }

  @Override
  protected boolean miseEnStockTiroir() {
    return false;
  }

  @Override
  protected boolean checkIOsTiroir() {
    return false;
  }

  @Override
  protected boolean checkIOsColonnesSol() {
    return false;
  }

  @Override
  protected boolean verrouillageColonnesSol() {
    return false;
  }
}
