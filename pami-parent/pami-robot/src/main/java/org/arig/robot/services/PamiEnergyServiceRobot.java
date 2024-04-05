package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.i2c.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiEnergyServiceRobot extends AbstractEnergyService {

    @Autowired
    private SD21Servos sd21Servos;

    @Autowired
    private PamiIOServiceRobot iioService;

    @Override
    public double tensionServos() {
        if (iioService.auOk()) {
            return sd21Servos.getTension();
        } else {
            return 0;
        }
    }

    @Override
    public double courantServos() {
        return 0;
    }

    @Override
    public double tensionMoteurs() {
        return iioService.auOk() ? 12 : 0;
    }

    @Override
    public double courantMoteurs() {
        return 0;
    }

    /*
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
    }*/
}
