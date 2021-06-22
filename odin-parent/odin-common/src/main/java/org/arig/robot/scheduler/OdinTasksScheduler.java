package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.filters.common.SignalEdgeFilter.Type;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.services.OdinEcranService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.capteurs.IAlimentationSensor;
import org.arig.robot.system.vacuum.AbstractARIGVacuumController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinTasksScheduler {

    @Autowired
    private OdinRobotStatus rs;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    private ISystemBlockerManager systemBlockerManager;

    @Autowired
    private OdinEcranService ecranService;

    @Autowired
    private IAlimentationSensor alimentationSensor;

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Scheduled(fixedRate = 1000)
    public void ecranTask() {
        ecranService.process();
    }

    @Scheduled(fixedDelay = 50)
    public void obstacleAvoidanceTask() {
        if (rs.avoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void systemBlockerManagerTask() {
        if (rs.matchEnabled()) {
            systemBlockerManager.process();
        }
    }

//    @Scheduled(fixedDelay = 5000)
//    public void systemCheckTensionTaks() {
//        if (rs.matchEnabled()) {
//            try {
//                alimentationSensor.refresh();
//                AlimentationSensorValue servo = alimentationSensor.get((byte) 0);
//                AlimentationSensorValue moteur = alimentationSensor.get((byte) 0);
//
//            } catch (I2CException e) {
//                log.warn("Erreur de refresh des infos alimentations");
//            }
//        }
//    }
}
