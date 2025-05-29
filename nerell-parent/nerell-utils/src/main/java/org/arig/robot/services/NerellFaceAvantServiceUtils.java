package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.PriseGradinState;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.utils.ThreadUtils;

@Slf4j
public class NerellFaceAvantServiceUtils extends NerellFaceAvantService {

  public NerellFaceAvantServiceUtils(NerellRobotStatus rs, TrajectoryManager mv,
                                     NerellRobotServosService servos, NerellIOService ioService) {
    super(rs, mv, servos, ioService);
  }

  @Override
  protected void aligneFace(Point gradin) {
    log.info("Aligne la face avec le gradin");
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
  protected void echappementPriseGradinBrut(PriseGradinState state) throws AvoidingException {
    log.info("Echappement prise gradin brut {}", state.name());
    ThreadUtils.sleep(5000);
    servos.groupeBlockColonneAvantOuvert(false);
    servos.tiroirAvantStock(false);
    servos.becAvantFerme(false);
    servos.ascenseurAvantRepos(false);
    servos.groupeDoigtsAvantFerme(false);
    servos.groupePincesAvantRepos(false);
  }

  @Override
  protected void deplacementDeposeColonnesSol(boolean reverse) throws AvoidingException {
    log.info("Déplacement pour prise colonnes sol");
    ThreadUtils.sleep(5000);
  }

  @Override
  protected void deplacementDeposeEtage(boolean reverse) throws AvoidingException {
    log.info("Enleve la tirette une fois la construction enlevé");
    while (ioService.tirette()) {
      ThreadUtils.sleep(300);
    }
  }
}
