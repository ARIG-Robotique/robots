package org.arig.robot.services;

import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MagasinService {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    private boolean inProgress = false;

    public void process() {

        if (!inProgress && rs.hasModuleDansMagasin()) {
            if (!ioService.presenceDevidoir()) {
                inProgress = true;
                servosService.devidoirChargement();
                servosService.waitDevidoire();
            }

            if (!ioService.presenceRouleaux()) {
                inProgress = true;
                servosService.devidoirDechargement();
                servosService.waitDevidoire();

                servosService.devidoirChargement();
            }

            inProgress = false;
        }
    }

}
