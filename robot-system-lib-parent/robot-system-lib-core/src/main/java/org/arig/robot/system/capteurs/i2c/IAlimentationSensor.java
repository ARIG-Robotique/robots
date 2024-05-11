package org.arig.robot.system.capteurs.i2c;

import org.arig.robot.model.capteurs.AlimentationSensorValue;

public interface IAlimentationSensor {

    AlimentationSensorValue get(byte channel);

    default void refresh() throws Exception {
        // NOP
    };

    void printVersion() throws Exception;
}
