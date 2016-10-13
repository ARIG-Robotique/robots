package org.arig.eurobot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.CalageBordureService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 23/12/14.
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
    private IAvoidingService avoidingService;

    @Autowired
    private CalageBordureService calageBordure;

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void asservissementTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        } else {
            mouvementManager.stop();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void obstacleAvoidanceTask() {
        if (rs.isAvoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 200)
    public void calageBordureTask() {
        if (rs.isCalageBordureEnabled()) {
            calageBordure.process();
        }
    }

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
