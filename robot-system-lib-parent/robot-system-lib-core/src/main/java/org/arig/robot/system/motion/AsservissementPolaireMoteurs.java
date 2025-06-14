package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AsservissementPolaireMoteurs.
 *
 * @author gdepuille
 */
public class AsservissementPolaireMoteurs implements IAsservissementPolaire {

  @Autowired
  protected MonitoringWrapper monitoringWrapper;

  @Autowired
  private CommandeRobot cmdRobot;

  @Autowired
  private Abstract2WheelsEncoders encoders;

  @Autowired
  @Qualifier("pidMoteurDroit")
  private PidFilter pidMoteurDroit;

  @Autowired
  @Qualifier("pidMoteurGauche")
  private PidFilter pidMoteurGauche;

  @Autowired
  @Qualifier("rampDistance")
  private TrapezoidalRampFilter rampDistance;

  @Autowired
  @Qualifier("rampOrientation")
  private TrapezoidalRampFilter rampOrientation;

  /**
   * Instantiates a new asservissement polaire.
   */
  public AsservissementPolaireMoteurs() {
    super();
  }

  @Override
  public void reset(final boolean resetFilters) {
    pidMoteurDroit.reset();
    pidMoteurGauche.reset();

    if (resetFilters) {
      rampDistance.reset();
      rampOrientation.reset();
    }
  }

  @Override
  public void process(final long timeStepMs, boolean obstacleDetected) {
    final double positionDistance, positionOrientation;

    // Distance
    if (cmdRobot.isType(TypeConsigne.DIST) || cmdRobot.isType(TypeConsigne.XY)) {
      rampDistance.setConsigneVitesse(cmdRobot.getVitesse().getDistance());
      rampDistance.setFrein(cmdRobot.isFrein());
      positionDistance = rampDistance.filter(cmdRobot.getConsigne().getDistance());
    } else {
      positionDistance = 0;
    }

    // Orientation
    if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
      rampOrientation.setConsigneVitesse(cmdRobot.getVitesse().getOrientation());
      rampOrientation.setFrein(true);
      positionOrientation = rampOrientation.filter(cmdRobot.getConsigne().getOrientation());
    } else {
      positionOrientation = 0;
    }

    // Consigne moteurs
    double consigneMotDroit = positionDistance + positionOrientation;
    double consigneMotGauche = positionDistance - positionOrientation;

    pidMoteurDroit.setSampleTimeMs(timeStepMs);
    pidMoteurDroit.consigne(consigneMotDroit);
    double cmdMotDroit = pidMoteurDroit.filter(encoders.getDroit());

    pidMoteurGauche.setSampleTimeMs(timeStepMs);
    pidMoteurGauche.consigne(consigneMotGauche);
    double cmdMotGauche = pidMoteurGauche.filter(encoders.getGauche());

    cmdRobot.getMoteur().setDroit((int) cmdMotDroit);
    cmdRobot.getMoteur().setGauche((int) cmdMotGauche);

    final MonitorTimeSerie serie = new MonitorTimeSerie()
      .measurementName("asservissement")
      .addTag(MonitorTimeSerie.TAG_NAME, "polaire")
      .addField("mot_d", cmdMotDroit)
      .addField("mot_g", cmdMotGauche);

    monitoringWrapper.addTimeSeriePoint(serie);
  }

  @Override
  public void setRampDistance(double accel, double decel) {
    rampDistance.setRamps(accel, decel);
  }

  @Override
  public void setRampOrientation(double accel, double decel) {
    rampOrientation.setRamps(accel, decel);
  }
}
