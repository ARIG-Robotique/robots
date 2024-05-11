package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.NerellEcranService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.capteurs.i2c.GP2D12Telemeter;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellTasksScheduler {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private AvoidingService avoidingService;

    @Autowired
    private NerellIOService ioService;

    @Autowired
    private SystemBlockerManager systemBlockerManager;

    @Autowired
    private NerellEcranService ecranService;

    @Autowired
    private BaliseService baliseService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    @Qualifier("gp2d")
    private ILidarTelemeter gp2D12Telemeter;

    @Scheduled(fixedRate = 1000)
    public void ecranTask() {
        if (rs.ecranEnabled()) {
            ecranService.process();
        }
    }

    @Scheduled(fixedDelay = 20)
    public void obstacleAvoidanceTask() {
        if (rs.avoidanceEnabled()) {
            /*if (rs.getRemainingTime() < 10000 && !gp2D12Telemeter.enabled()) {
                gp2D12Telemeter.enabled(true);
            }*/

            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 200)
    public void systemBlockerManagerTask() {
        if (rs.matchEnabled() && !rs.simulateur()) {
            systemBlockerManager.process();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTaks() {
        if (rs.matchEnabled()) {
            if (!energyService.checkServos()) {
                ioService.disableAlimServos();
            }
            if (!energyService.checkMoteurs()) {
                ioService.disableAlimMoteurs();
            }
        }
    }

    @Scheduled(fixedDelay = 2500)
    public void getBaliseStatus() {
        if (rs.matchEnabled() || !rs.baliseEnabled()) return;

        if (!baliseService.isOK()) {
            baliseService.startDetection();
        } else {
            baliseService.updateStatus();
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void updateBaliseData() {
        if (!rs.matchEnabled() || !rs.baliseEnabled()) return;

        if (baliseService.isOK()) {
            baliseService.updateData();
        } else {
            baliseService.startDetection();
        }
    }
}
