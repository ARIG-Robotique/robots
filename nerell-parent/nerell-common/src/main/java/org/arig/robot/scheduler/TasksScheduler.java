package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Component
public class TasksScheduler  {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private ITrajectoryManager trajectoryManager;

    @Autowired
    private ICarouselManager carouselManager;

    @Scheduled(fixedRate = (long) IConstantesNerellConfig.asservTimeMs)
    public void asservissementPropulsionsTask() {
        StopWatch exec = new StopWatch();
        exec.start();
        if (rs.isAsservEnabled()) {
            trajectoryManager.process();
        } else {
            trajectoryManager.stop();
        }
        exec.stop();

        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("tasks")
                .addTag(MonitorTimeSerie.TAG_NAME, "asservissementPropulsions")
                .addField("rate", IConstantesNerellConfig.asservTimeMs)
                .addField("execTime", exec.getTime());

        monitoringWrapper.addTimeSeriePoint(serie);
    }

    @Scheduled(fixedRate = (long) IConstantesNerellConfig.asservTimeCarouselMs)
    public void asservissementCarouselTask() {
        StopWatch exec = new StopWatch();
        exec.start();
        if (rs.isAsservCarouselEnabled()) {
            carouselManager.process();
        } else {
            carouselManager.stop();
        }
        exec.stop();

        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("tasks")
                .addTag(MonitorTimeSerie.TAG_NAME, "asservissementCarousel")
                .addField("rate", IConstantesNerellConfig.asservTimeCarouselMs)
                .addField("execTime", exec.getTime());

        monitoringWrapper.addTimeSeriePoint(serie);
    }

    @Scheduled(fixedDelay = 1)
    public void strategyTask() {
        if (rs.isMatchEnabled()) {
            strategyManager.execute();
        }
    }
}
