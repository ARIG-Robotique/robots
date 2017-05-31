package org.arig.robot.system.encoders;

import org.arig.robot.model.bouchon.BouchonEncoderValues;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 31/10/16.
 */
public class BouchonARIG2WheelsEncoders extends ARIG2WheelsEncoders {

    @Autowired
    private AbstractPropulsionsMotors motors;

    private final Random rand = new Random();
    private final List<BouchonEncoderValues> values;

    public BouchonARIG2WheelsEncoders(final String deviceNameGauche, final String deviceNameDroit, final List<BouchonEncoderValues> values) {
        super(deviceNameGauche, deviceNameDroit);
        this.values = values;
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
