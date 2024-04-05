package org.arig.robot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PamiEnergyServiceSimulator extends AbstractEnergyService {

    @Autowired
    private PamiIOServiceSimulator ioService;

    @Override
    public double tensionServos() {
        return ioService.alimServos() ? 6 : 0;
    }

    @Override
    public double courantServos() {
        return 0;
    }

    @Override
    public double tensionMoteurs() {
        return ioService.alimMoteurs() ? 13 : 0;
    }

    @Override
    public double courantMoteurs() {
        return 0;
    }
}
