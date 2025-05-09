package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.utils.ThreadUtils;

@Slf4j
public class NerellFaceArriereServiceUtils extends NerellFaceArriereService {

  public NerellFaceArriereServiceUtils(NerellRobotStatus rs, TrajectoryManager mv,
                                       NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFace(Point gradin) {
    log.info("Aligne le dos avec le gradin");
    ThreadUtils.sleep(500);
  }

  @Override
  protected void deplacementPriseColonnesPinces() {
    log.info("Déplacement pour prise colonnes pinces");
    ThreadUtils.sleep(500);
  }

  @Override
  protected void deplacementPriseColonnesSol() {
    log.info("Déplacement pour prise colonnes sol");
    ThreadUtils.sleep(500);
  }

  @Override
  protected void deplacementDeposeEtage() throws AvoidingException {
    log.info("Enleve la tirette une fois la construction enlevé");
    while(ioService.tirette()) {
      ThreadUtils.sleep(300);
    }
  }
}
