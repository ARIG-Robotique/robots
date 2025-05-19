package org.arig.robot.system.motion;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.enums.TypeOdometrie;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Odometrie.
 *
 * @author gdepuille
 */
public class OdometrieLineaire extends AbstractOdometrie {

  @Autowired
  private Abstract2WheelsEncoders encoders;

  @Autowired
  private ConvertionRobotUnit conv;

  @Setter
  @Accessors(fluent = true)
  private double corrfuge = 0;

  private final boolean correctionCentrifuge;

  public OdometrieLineaire() {
    this(false);
  }

  public OdometrieLineaire(boolean correctionCentrifuge) {
    super(TypeOdometrie.LINEAIRE);
    this.correctionCentrifuge = correctionCentrifuge;
  }

  /**
   * Ce calcul est effectué avec le postulat que durant le laps de temps ecoulé le robot a roulé droit (pas en
   * courbe). On parle donc d'approximation linéaire.
   */
  @Override
  protected void process() {
    double newTheta = getPosition().getAngle() + encoders.getOrientation();

    // Ajustement a PI près
    if (newTheta > conv.piPulse()) {
      newTheta -= conv.pi2Pulse();
    } else if (newTheta < -conv.piPulse()) {
      newTheta += conv.pi2Pulse();
    }

    // Calcul du déplacement sur X et Y en fonction de l'angle et de la distance
    final double thetaRad = conv.pulseToRad(newTheta);
    final double dX = encoders.getDistance() * Math.cos(thetaRad);
    final double dY = encoders.getDistance() * Math.sin(thetaRad);

    // Sauvegarde nouvelle position (en pulse)
    getPosition().setAngle(newTheta);
    getPosition().addDeltaX(dX);
    getPosition().addDeltaY(dY);

    if (correctionCentrifuge) {
      double courbureRad = conv.pulseToRad(encoders.getOrientation());
      double corrX = (dY * courbureRad) * corrfuge;
      double corrY = -(dX * courbureRad) * corrfuge;

      getPosition().addDeltaX(corrX);
      getPosition().addDeltaY(corrY);
    }
  }
}
