package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.capteurs.IAlimentationSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OdinEnergyService extends AbstractEnergyService {

    private static final byte CHANNEL_SERVOS = 1;
    private static final byte CHANNEL_MOTEURS = 2;

    @Autowired
    private IAlimentationSensor alimentationSensor;

    private StopWatch refreshNeeded;

    @PostConstruct
    public void init() {
        refreshNeeded = new StopWatch();
        refreshNeeded.start();
    }

    @Override
    public double tensionServos() {
        refresh();
        return alimentationSensor.get(CHANNEL_SERVOS).tension();
    }

    @Override
    public double courantServos() {
        refresh();
        return alimentationSensor.get(CHANNEL_SERVOS).current();
    }

    @Override
    public double tensionMoteurs() {
        refresh();
        return alimentationSensor.get(CHANNEL_MOTEURS).tension();
    }

    @Override
    public double courantMoteurs() {
        refresh();
        return alimentationSensor.get(CHANNEL_MOTEURS).current();
    }

    private void refresh() {
        if (refreshNeeded.getTime(TimeUnit.SECONDS) > 2) {
            refreshNeeded.reset();
            refreshNeeded.start();

            try {
                alimentationSensor.refresh();
            } catch (I2CException e) {
                log.error("Refresh des infos d'alimentation impossible", e);
            }
        }
    }
}
