package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellEnergyService extends AbstractEnergyService {

    @Autowired
    private SD21Servos sd21Servos;

    @Autowired
    private NerellIOService iioService;

    @Override
    public double tensionServos() {
        if (iioService.auOk() && iioService.alimPuissance5VOk()) {
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
        return iioService.auOk() && iioService.alimPuissance12VOk() ? 12 : 0;
    }

    @Override
    public double courantMoteurs() {
        return 0;
    }
}