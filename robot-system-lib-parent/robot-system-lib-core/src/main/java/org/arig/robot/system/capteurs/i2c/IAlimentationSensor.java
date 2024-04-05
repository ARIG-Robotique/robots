package org.arig.robot.system.capteurs.i2c;

import org.arig.robot.exception.I2CException;
import org.arig.robot.model.capteurs.AlimentationSensorValue;

public interface IAlimentationSensor {

    AlimentationSensorValue get(byte channel);

    void refresh() throws I2CException;

    void printVersion() throws I2CException;
}
