package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.CarouselService;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
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
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private ICarouselManager carouselManager;

    @Scheduled(fixedRate = (long) IConstantesNerellConfig.asservTimeMs)
    public void asservissementTask() {
        if (rs.isAsservEnabled()) {
            trajectoryManager.process();
        } else {
            trajectoryManager.stop();
        }
    }

    @Scheduled(fixedRate = (long) IConstantesNerellConfig.asservTimeCarouselMs)
    public void asservissementCarouselTask() {
        if (rs.isAsservCarouselEnabled()) {
            carouselManager.process();
        } else {
            carouselManager.stop();
        }
    }

    @Scheduled(fixedDelay = 1)
    public void strategyTask() {
        if (rs.isMatchEnabled()) {
            strategyManager.execute();
        }
    }
}
