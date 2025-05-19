package org.arig.robot.system.encoders;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.bouchon.BouchonEncoderValues;
import org.arig.robot.system.encoders.i2c.ARIGI2C2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 31/10/16.
 */
@Slf4j
public class BouchonARIG2WheelsEncoders extends ARIGI2C2WheelsEncoders {

  @Autowired
  private AbstractPropulsionsMotors motors;

  private final Random rand = new Random();
  private final List<BouchonEncoderValues> values;

  public BouchonARIG2WheelsEncoders(final List<BouchonEncoderValues> values) {
    super("Codeur Gauche", "Codeur Droit");
    this.values = values;
  }

  public void printLimiterValues() {
    Optional<Integer> minGauche, minDroit;
    // Positive
    minGauche = values.parallelStream()
      .filter(BouchonEncoderValues::vitessePositive)
      .filter(b -> b.gauche() != 0)
      .sorted(Comparator.comparingInt(BouchonEncoderValues::vitesseMoteur))
      .findFirst()
      .map(BouchonEncoderValues::vitesseMoteur);

    minDroit = values.parallelStream()
      .filter(BouchonEncoderValues::vitessePositive)
      .filter(b -> b.droit() != 0)
      .sorted(Comparator.comparingInt(BouchonEncoderValues::vitesseMoteur))
      .findFirst()
      .map(BouchonEncoderValues::vitesseMoteur);

    log.info("Positive Min moteur Gauche : {}", minGauche.get());
    log.info("Positive Min moteur Droit : {}", minDroit.get());

    // Negative
    minGauche = values.parallelStream()
      .filter(BouchonEncoderValues::vitesseNegative)
      .filter(b -> b.gauche() != 0)
      .sorted(Collections.reverseOrder(Comparator.comparingInt(BouchonEncoderValues::vitesseMoteur)))
      .findFirst()
      .map(BouchonEncoderValues::vitesseMoteur);

    minDroit = values.parallelStream()
      .filter(BouchonEncoderValues::vitesseNegative)
      .filter(b -> b.droit() != 0)
      .sorted(Collections.reverseOrder(Comparator.comparingInt(BouchonEncoderValues::vitesseMoteur)))
      .findFirst()
      .map(BouchonEncoderValues::vitesseMoteur);

    log.info("Negative Min moteur Gauche : {}", minGauche.get());
    log.info("Negative Min moteur Droit : {}", minDroit.get());
  }

  @Override
  protected double lectureDroit() {
    int vitesse = motors.currentSpeedDroit();
    return getRandomValueForVitesse(vitesse).droit();
  }

  @Override
  protected double lectureGauche() {
    int vitesse = motors.currentSpeedGauche();
    return getRandomValueForVitesse(vitesse).gauche();
  }

  private BouchonEncoderValues getRandomValueForVitesse(int vitesse) {
    List<BouchonEncoderValues> filter = values.parallelStream()
      .filter(v -> v.vitesseMoteur() == vitesse)
      .collect(Collectors.toList());

    return filter.get(rand.nextInt(filter.size()));
  }
}
