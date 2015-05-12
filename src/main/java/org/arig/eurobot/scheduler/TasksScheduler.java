package org.arig.eurobot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.AvoidanceService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Component
public class TasksScheduler {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private ServosService servosService;

    @Autowired
    private AvoidanceService avoidanceService;

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void robotManagerTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        } else {
            mouvementManager.stop();
        }
    }

    /*@Scheduled(fixedDelay = 200)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidanceService.process();
        }
    }*/

    @Scheduled(fixedDelay = 100)
    public void strategyTask() {
        if (rs.isMatchEnabled()) {
            strategyManager.execute();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void ascenseurTask() {
        if (rs.isAscenseurEnabled()) {
            servosService.checkAscenseur();
        }
    }
}
