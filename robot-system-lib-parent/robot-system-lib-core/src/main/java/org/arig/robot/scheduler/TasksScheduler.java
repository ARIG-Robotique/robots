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
import org.arig.robot.system.group.RobotGroup;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TasksScheduler implements InitializingBean {

    private static final int MS_TO_NS = 1000000;

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

    /*
    @Autowired
    private RobotGroup group;
    */

    @Override
    public void afterPropertiesSet() {
        Thread processThread = new Thread(() -> {
            long lastTimeAsserv = System.nanoTime();
            long lastTimeI2C = lastTimeAsserv;
            long lastTimeCalage = lastTimeAsserv;
            long lastTimeRefreshState = lastTimeAsserv;
            long lastTimeRefreshPosition = lastTimeAsserv;

            rs.enableMainThread();
            while (rs.mainThread()) {
                // calage
                long timeStartCalage = System.nanoTime();
                long ellapsedCalage = timeStartCalage - lastTimeCalage;

                boolean calageCourt = rs.calage().contains(TypeCalage.PRISE_ELECTROAIMANT);
                if (ellapsedCalage >= robotConfig.calageTimeMs(calageCourt) * MS_TO_NS) {
                    lastTimeCalage = timeStartCalage;

                    if (!rs.calage().isEmpty()) {
                        calageBordure.process();
                    }
                }

                // asservissement
                long timeStartAsserv = System.nanoTime();
                long ellapsedAsserv = timeStartAsserv - lastTimeAsserv;

                if (ellapsedAsserv >= robotConfig.asservTimeMs() * MS_TO_NS) {
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

                // lecture I2C
                long timeStartI2C = System.nanoTime();
                long ellapsedI2C = timeStartI2C - lastTimeI2C;

                if (ellapsedI2C >= robotConfig.i2cReadTimeMs() * MS_TO_NS) {
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

                // position
                /*
                if (rs.groupOk() && rs.matchEnabled()) {
                    long timeStartRefreshPosition = System.nanoTime();
                    long ellapsedRefreshPosition = timeStartRefreshPosition - lastTimeRefreshPosition;
                    if (ellapsedRefreshPosition >= 500 * MS_TO_NS) {
                        group.setCurrentPosition((int) trajectoryManager.currentXMm(), (int) trajectoryManager.currentXMm());
                        lastTimeRefreshPosition = timeStartRefreshPosition;
                    }
                }
                */

                // state
                long timeStartRefreshState = System.nanoTime();
                long ellapsedRefreshState = timeStartRefreshState - lastTimeRefreshState;
                if (ellapsedRefreshState >= 1000 * MS_TO_NS) {
                    lastTimeRefreshState = timeStartRefreshState;
                    rs.refreshState();
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
