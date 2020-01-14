package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.RobotStatus;
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
    private RobotStatus rs;

    @Autowired
    private CommandeRobot cmdRobot;

    public boolean process() {
        boolean done = false;

        switch (rs.getCalageBordure()) {
            case AVANT:
                throw new RuntimeException("Calage avant non implemnenté");
//                done = ioService.distanceTelemetreAvantDroit() <= rs.getCalageBordureDistance() || ioService.distanceTelemetreAvantGauche() <= rs.getCalageBordureDistance();
//                break;

            case ARRIERE:
                if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                    done = ioService.calageBordureArriereDroit() || ioService.calageBordureArriereGauche();
                } else {
                    done = ioService.calageBordureArriereDroit() && ioService.calageBordureArriereGauche();
                }
                break;
        }

        if (done) {
            trajectoryManager.calageBordureDone();
            rs.disableCalageBordure();
        }

        return done;
    }
}
