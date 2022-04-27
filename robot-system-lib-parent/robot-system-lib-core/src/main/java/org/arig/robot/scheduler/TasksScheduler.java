package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.CalageService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.StrategyManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TasksScheduler implements InitializingBean {

    @Autowired
    private MonitoringWrapper monitoringWrapper;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private CalageService calageBordure;

    @Autowired
    private IOService ioService;

    @Autowired
    private RobotConfig robotConfig;

    @Override
    public void afterPropertiesSet() {
        Thread processThread = new Thread(() -> {
            long lastTimeAsserv = System.nanoTime();
            long lastTimeI2C = lastTimeAsserv;
            long lastTimeCalage = lastTimeAsserv;

            rs.enableMainThread();
            while (rs.mainThread()) {
                long timeStartCalage = System.nanoTime();
                long ellapsedCalage = timeStartCalage - lastTimeCalage;

                boolean calageCourt = rs.calage().contains(TypeCalage.LATTERAL_DROIT) || rs.calage().contains(TypeCalage.PRISE_ECHANTILLON);
                if (ellapsedCalage >= robotConfig.calageTimeMs(calageCourt) * 1000000) {
                    lastTimeCalage = timeStartCalage;

                    if (!rs.calage().isEmpty()) {
                        calageBordure.process();
                    }
                }

                long timeStartAsserv = System.nanoTime();
                long ellapsedAsserv = timeStartAsserv - lastTimeAsserv;

                if (ellapsedAsserv >= robotConfig.asservTimeMs() * 1000000) {
                    lastTimeAsserv = timeStartAsserv;

                    if (rs.asservEnabled()) {
                        trajectoryManager.process(TimeUnit.MICROSECONDS.toMillis(ellapsedAsserv));
                    } else {
                        if (!rs.captureEnabled()) {
                            trajectoryManager.stop();
                        }
                    }

                    MonitorTimeSerie serie = new MonitorTimeSerie()
                            .measurementName("tasks")
                            .addTag(MonitorTimeSerie.TAG_NAME, "asservissementPropulsions")
                            .addField("rate", robotConfig.asservTimeMs())
                            .addField("runTime", System.nanoTime() - timeStartAsserv)
                            .addField("execTime", ellapsedAsserv);

                    monitoringWrapper.addTimeSeriePoint(serie);
                }

                long timeStartI2C = System.nanoTime();
                long ellapsedI2C = timeStartI2C - lastTimeI2C;

                if (ellapsedI2C >= robotConfig.i2cReadTimeMs() * 1000000) {
                    lastTimeI2C = timeStartI2C;

                    ioService.refreshAllIO();

                    MonitorTimeSerie serie = new MonitorTimeSerie()
                            .measurementName("tasks")
                            .addTag(MonitorTimeSerie.TAG_NAME, "lectureI2C")
                            .addField("rate", robotConfig.i2cReadTimeMs())
                            .addField("runTime", System.nanoTime() - timeStartI2C)
                            .addField("execTime", ellapsedI2C);

                    monitoringWrapper.addTimeSeriePoint(serie);
                }
            }
        });

        processThread.setName("process");
        processThread.start();
    }

    @Scheduled(fixedDelay = 1)
    public void strategyTask() {
        if (rs.matchEnabled()) {
            strategyManager.execute();
        }
    }
}
