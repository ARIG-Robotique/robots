package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.i2c.IAlimentationSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiEnergyServiceRobot extends AbstractEnergyService {

    private static final byte CHANNEL_MOTEURS = 1;

    @Autowired
    private IAlimentationSensor alimentationController;

    @Autowired
    private PamiIOServiceRobot ioService;

    @Override
    public double tensionServos() {
        return ioService.auOk() ? 6 : 0;
    }

    @Override
    public double courantServos() {
        return 0;
    }

    @Override
    public double tensionMoteurs() {
        return alimentationController.get(CHANNEL_MOTEURS).tension();
    }

    @Override
    public double courantMoteurs() {
        return alimentationController.get(CHANNEL_MOTEURS).current();
    }
}
