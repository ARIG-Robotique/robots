package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellEnergyServiceRobot extends AbstractEnergyService {

    @Autowired
    private SD21Servos sd21Servos;

    @Autowired
    private NerellIOServiceRobot iioService;

    @Override
    public double tensionServos() {
        if (iioService.auOk() && iioService.puissanceServosOk()) {
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
        return iioService.auOk() && iioService.puissanceMoteursOk() ? 12 : 0;
    }

    @Override
    public double courantMoteurs() {
        return 0;
    }
}
