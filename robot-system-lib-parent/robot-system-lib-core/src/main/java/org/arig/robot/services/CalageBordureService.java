package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.enums.TypeConsigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalageBordureService {

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private IIOService ioService;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private CommandeRobot cmdRobot;

    public void process() {
        if (rs.calageBordure() != null) {
            boolean done;

            if (!rs.matchEnabled() && !ioService.auOk()) {
                done = true;
            } else if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                if (rs.calageBordure() == TypeCalage.STANDARD) {
                    done = ioService.calageBordureDroit() || ioService.calageBordureGauche();
                } else {
                    done = ioService.calageBordureCustomDroit() || ioService.calageBordureCustomGauche();
                }
            } else {
                if (rs.calageBordure() == TypeCalage.STANDARD) {
                    done = ioService.calageBordureDroit() && ioService.calageBordureGauche();
                } else {
                    done = ioService.calageBordureCustomDroit() && ioService.calageBordureCustomGauche();
                }
            }

            if (done) {
                trajectoryManager.calageBordureDone();
                rs.disableCalageBordure();
            }
        }
    }
}
