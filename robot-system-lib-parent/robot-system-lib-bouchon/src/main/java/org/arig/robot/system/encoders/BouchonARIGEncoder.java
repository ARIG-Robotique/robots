package org.arig.robot.system.encoders;

import org.arig.robot.model.bouchon.BouchonEncoderValue;
import org.arig.robot.system.motors.AbstractMotor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BouchonARIGEncoder extends ARIGI2CEncoder {

    @Autowired
    private AbstractMotor motor;

    private final Random rand = new Random();
    private final List<BouchonEncoderValue> values;

    public BouchonARIGEncoder(final String deviceName, final List<BouchonEncoderValue> values) {
        super(deviceName);
        this.values = values;
    }

    @Override
    protected double lecture() {
        int vitesse = motor.currentSpeed();
        return getRandomValueForVitesse(vitesse).value();
    }

    private BouchonEncoderValue getRandomValueForVitesse(int vitesse) {
        List<BouchonEncoderValue> filter = values.parallelStream()
                .filter(v -> v.vitesseMoteur() == vitesse)
                .collect(Collectors.toList());

        return filter.get(rand.nextInt(filter.size()));
    }
}
