package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.CalageBordureService;
import org.arig.robot.services.IIOService;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Component
public class TasksScheduler implements InitializingBean {

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

    @Autowired
    private CalageBordureService calageBordure;

    @Autowired
    private IIOService ioService;

    @Override
    public void afterPropertiesSet() {
        new Thread(() -> {
            long lastTimeAsserv = System.nanoTime();
            long lastTimeAsservCarousel = lastTimeAsserv;
            long lastTimeI2C = lastTimeAsserv;
            long lastTimeCalage = lastTimeAsserv;

            while (true) {
                long timeStartCalage = System.nanoTime();
                long ellapsedCalage = timeStartCalage - lastTimeCalage;

                if (ellapsedCalage >= IConstantesNerellConfig.calageTimeMs * 1000000) {
                    lastTimeCalage = timeStartCalage;

                    if (rs.getCalageBordure() != null) {
                        if (calageBordure.process() || trajectoryManager.isTrajetAtteint() || trajectoryManager.isTrajetEnApproche()) {
                            // Calage effectué, on arrete
                            rs.disableCalageBordure();
                        }
                    }
                }

                long timeStartAsserv = System.nanoTime();
                long ellapsedAsserv = timeStartAsserv - lastTimeAsserv;

                if (ellapsedAsserv >= IConstantesNerellConfig.asservTimeMs * 1000000) {
                    lastTimeAsserv = timeStartAsserv;

                    if (rs.isAsservEnabled()) {
                        trajectoryManager.process();
                    } else {
                        trajectoryManager.stop();
                    }

                    MonitorTimeSerie serie = new MonitorTimeSerie()
                            .measurementName("tasks")
                            .addTag(MonitorTimeSerie.TAG_NAME, "asservissementPropulsions")
                            .addField("rate", IConstantesNerellConfig.asservTimeMs)
                            .addField("runTime", System.nanoTime() - timeStartAsserv)
                            .addField("execTime", ellapsedAsserv);

                    monitoringWrapper.addTimeSeriePoint(serie);
                }

                long timeStartAsservCarousel = System.nanoTime();
                long ellapsedAsservCarousel = timeStartAsservCarousel - lastTimeAsservCarousel;

                if (ellapsedAsservCarousel >= IConstantesNerellConfig.asservTimeCarouselMs * 1000000) {
                    lastTimeAsservCarousel = timeStartAsservCarousel;

                    if (rs.isCarouselInitialized() && rs.isAsservCarouselEnabled()) {
                        carouselManager.process();
                    } else if (rs.isCarouselInitialized()) {
                        carouselManager.stop();
                    }

                    MonitorTimeSerie serie = new MonitorTimeSerie()
                            .measurementName("tasks")
                            .addTag(MonitorTimeSerie.TAG_NAME, "asservissementCarousel")
                            .addField("rate", IConstantesNerellConfig.asservTimeCarouselMs)
                            .addField("runTime", System.nanoTime() - timeStartAsservCarousel)
                            .addField("execTime", ellapsedAsservCarousel);

                    monitoringWrapper.addTimeSeriePoint(serie);
                }

                long timeStartI2C = System.nanoTime();
                long ellapsedI2C = timeStartI2C - lastTimeI2C;

                if (ellapsedI2C >= IConstantesNerellConfig.i2cReadTimeMs * 1000000) {
                    lastTimeI2C = timeStartI2C;

                    ioService.refreshAllPcf();

                    MonitorTimeSerie serie = new MonitorTimeSerie()
                            .measurementName("tasks")
                            .addTag(MonitorTimeSerie.TAG_NAME, "lectureI2C")
                            .addField("rate", IConstantesNerellConfig.i2cReadTimeMs)
                            .addField("runTime", System.nanoTime() - timeStartI2C)
                            .addField("execTime", ellapsedI2C);

                    monitoringWrapper.addTimeSeriePoint(serie);
                }
            }
        }).start();
    }

    @Scheduled(fixedDelay = 1)
    public void strategyTask() {
        if (rs.isMatchEnabled()) {
            strategyManager.execute();
        }
    }
}
