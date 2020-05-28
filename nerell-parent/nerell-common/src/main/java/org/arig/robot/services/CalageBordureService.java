package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellStatus;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalageBordureService {

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private IIOService ioService;

    @Autowired
    private NerellStatus rs;

    @Autowired
    private CommandeRobot cmdRobot;

    public void process() {
        if (rs.isCalageBordure()) {
            boolean done;

            if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                done = ioService.calageBordureDroit() || ioService.calageBordureGauche();
            } else {
                done = ioService.calageBordureDroit() && ioService.calageBordureGauche();
            }

            if (done) {
                trajectoryManager.calageBordureDone();
                rs.disableCalageBordure();
            }
        }
    }
}
