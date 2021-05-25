package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServosNerell;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.motion.AsservissementPolaireDistanceOrientation;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.GameMultiPathFinderImpl;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.system.pathfinding.impl.NoPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class NerellCommonContext {

    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(IConstantesNerellConfig.asservTimeMs)
                .calageTimeMs(IConstantesNerellConfig.calageTimeMs)
                .i2cReadTimeMs(IConstantesNerellConfig.i2cReadTimeMs)

                .pathFindingTailleObstacle(IConstantesNerellConfig.pathFindingTailleObstacle)
                .lidarOffsetPointMm(IConstantesNerellConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(IConstantesNerellConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(IConstantesNerellConfig.avoidanceWaitTimeMs)
                .pathFindingSeuilProximite(IConstantesNerellConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(IConstantesNerellConfig.pathFindingSeuilProximiteSafe)
                .pathFindingAngle(IConstantesNerellConfig.pathFindingAngle)
                .pathFindingAngleSafe(IConstantesNerellConfig.pathFindingAngleSafe)

                .seuilAlimentationServos(IConstantesServosNerell.SEUIL_ALIMENTATION_VOLTS)
                .servosMinTimeMax(IConstantesServosNerell.MIN_TIME_MAX)
                .servosBatch(IConstantesServosNerell.BATCH_CONFIG)

                .vitesse(IConstantesNerellConfig.vitesseMin, IConstantesNerellConfig.vitesseMax, 100)
                .vitesseOrientation(IConstantesNerellConfig.vitesseOrientationMin, IConstantesNerellConfig.vitesseOrientationMax, 100)
                .fenetreArretDistance(conv.mmToPulse(IConstantesNerellConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(IConstantesNerellConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(IConstantesNerellConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(IConstantesNerellConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(IConstantesNerellConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(IConstantesNerellConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(IConstantesNerellConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(IConstantesNerellConfig.startAngleLimitVitesseDistance))
                .sampleTimeS(IConstantesNerellConfig.asservTimeS);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesNerellConfig.countPerMm, IConstantesNerellConfig.countPerDeg);
    }

    @Bean
    public IAsservissementPolaire asservissement() {
        // Positive Min moteur Gauche : 102
        // Negative Min moteur Gauche : -37
        LimiterFilter limiterMoteurGauche = new LimiterFilter(35d, 4095d, LimiterType.MIRROR);

        // Positive Min moteur Droit : 93
        // Negative Min moteur Droit : -78
        LimiterFilter limiterMoteurDroit = new LimiterFilter(35d, 4095d, LimiterType.MIRROR);

        return new AsservissementPolaireDistanceOrientation(limiterMoteurGauche, limiterMoteurDroit);
    }

    @Bean
    public IOdometrie odometrie() {
        return new OdometrieLineaire();
    }

    @Bean
    public CommandeRobot cmdRobot() {
        return new CommandeRobot();
    }

    @Bean(name = "currentPosition")
    public Position currentPosition() {
        return new Position();
    }

    @Bean(name = "pidDistance")
    public IPidFilter pidDistance() {
        log.info("Configuration PID Distance");
        SimplePidFilter pid = new SimplePidFilter("distance");
        pid.setTunings(IConstantesNerellConfig.kpDistance, IConstantesNerellConfig.kiDistance, IConstantesNerellConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
        pid.setTunings(IConstantesNerellConfig.kpOrientation, IConstantesNerellConfig.kiOrientation, IConstantesNerellConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccDistance, IConstantesNerellConfig.rampDecDistance, IConstantesNerellConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccOrientation, IConstantesNerellConfig.rampDecOrientation, IConstantesNerellConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(IConstantesNerellConfig.pathFindingAlgo);
            pf.setSaveImages(env.getProperty("robot.pathfinding.saveImages", Boolean.class, true));
            return pf;
        } else {
            return new NoPathFinderImpl();
        }
    }

    @Bean
    public NerellRobotStatus robotStatus() {
        return new NerellRobotStatus();
    }

    @Bean
    @DependsOn({"ecran", "rplidar"})
    public NerellOrdonanceur ordonanceur() {
        return new NerellOrdonanceur();
    }

    @Bean
    public ISystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManager(
                conv.mmToPulse(IConstantesNerellConfig.seuilErreurDistanceMm),
                conv.degToPulse(IConstantesNerellConfig.seuilErreurOrientationDeg)
        );
    }
}
