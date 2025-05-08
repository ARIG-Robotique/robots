package org.arig.robot.services;

import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;

public class NerellFaceArriereService extends AbstractNerellFaceService {

  public NerellFaceArriereService(NerellRobotStatus rs, TrajectoryManager mv,
                                  NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFace(Point gradin) {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method aligneFaceGradinBrut is not yet implemented.");
  }

  @Override
  protected void ouvreFacePourPrise() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method ouvreFacePourPrise is not yet implemented.");
  }

  @Override
  protected void deplacementPriseColonnesPinces() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method deplacementPriseColonnesPinces is not yet implemented.");
  }

  @Override
  protected void deplacementPriseColonnesSol() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method deplacementPriseColonnesSol is not yet implemented.");
  }

  @Override
  protected void updateStockRobot(boolean expectedSimulator) {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method updateStockRobot is not yet implemented.");
  }

  @Override
  protected boolean iosPinces() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method checkIOsPinces is not yet implemented.");
  }

  @Override
  protected boolean miseEnStockTiroir() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method miseEnStockTiroir is not yet implemented.");
  }

  @Override
  protected boolean iosTiroir() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method checkIOsTiroir is not yet implemented.");
  }

  @Override
  protected boolean iosColonnesSol() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method checkIOsColonnesSol is not yet implemented.");
  }

  @Override
  protected void verrouillageColonnesSol() {
      // TODO: Implement this method
      throw new UnsupportedOperationException("Method verrouillageColonnesSol is not yet implemented.");
  }

  @Override
  protected void deverouillageColonnesSol() {
    // TODO: Implement this method
    throw new UnsupportedOperationException("Method verrouillageColonnesSol is not yet implemented.");
  }
}
