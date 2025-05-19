package org.arig.robot.system.blockermanager;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SystemBlockerManagerImpl implements SystemBlockerManager {

  private static final byte MAX_ERROR_DISTANCE = 5;
  private static final byte MAX_ERROR_ORIENTATION = 5;

  @Autowired
  private CommandeRobot cmdRobot;

  @Autowired
  private TrajectoryManager trajectoryManager;

  @Autowired
  private Abstract2WheelsEncoders encoders;

  @Autowired
  protected MonitoringWrapper monitoringWrapper;

  private byte countError = 0;

  @Override
  public void reset() {
    countError = 0;
  }

  @Override
  public void process() {
    int motDroit = cmdRobot.getMoteur().getDroit();
    int motGauche = cmdRobot.getMoteur().getGauche();

    // Detection du non-deplacement ou de saturation de commande d'asservissement
    if (!trajectoryManager.isTrajetAtteint() &&
      (Math.abs(motDroit) > 500 || Math.abs(motGauche) > 500) &&
      Math.abs(encoders.getDroit()) < 2 &&
      Math.abs(encoders.getGauche()) < 2) {
      countError++;

    } else {
      countError = 0;
    }

    // Construction du monitoring
    final MonitorTimeSerie serie = new MonitorTimeSerie()
      .measurementName("blocker")
      .addField("maxErrorDistance", MAX_ERROR_DISTANCE)
      .addField("maxErrorOrientation", MAX_ERROR_ORIENTATION)
      .addField("countError", countError);

    monitoringWrapper.addTimeSeriePoint(serie);

    // x itérations de 500 ms (cf Scheduler)
    if (countError >= MAX_ERROR_DISTANCE) {
      log.warn("Détection de blocage trop importante");

      trajectoryManager.cancelMouvement();
      reset();
    }
  }
}
