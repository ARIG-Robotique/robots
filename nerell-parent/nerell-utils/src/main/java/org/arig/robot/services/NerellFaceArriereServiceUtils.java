package org.arig.robot.services;

import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;

public class NerellFaceArriereServiceUtils extends NerellFaceArriereService {

  public NerellFaceArriereServiceUtils(NerellRobotStatus rs, TrajectoryManager mv,
                                       NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFaceGradinBrut(GradinBrut gradin) { /* NOP */ }

  @Override
  protected void deplacementPriseColonnesPinces() { /* NOP */ }

  @Override
  protected void deplacementPriseColonnesSol() { /* NOP */ }

  @Override
  protected void deplacementEchappementGradinBrut() { /* NOP */ }
}
