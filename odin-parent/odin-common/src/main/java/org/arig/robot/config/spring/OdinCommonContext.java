package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.constants.IConstantesServosOdin;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.group.IRobotGroup;
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

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Slf4j
@Configuration
public class OdinCommonContext {
    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(IConstantesOdinConfig.asservTimeMs)
                .calageTimeMs(IConstantesOdinConfig.calageTimeMs)
                .i2cReadTimeMs(IConstantesOdinConfig.i2cReadTimeMs)

                .pathFindingTailleObstacle(IConstantesOdinConfig.pathFindingTailleObstacle)
                .lidarOffsetPointMm(IConstantesOdinConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(IConstantesOdinConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(IConstantesOdinConfig.avoidanceWaitTimeMs)
                .pathFindingSeuilProximite(IConstantesOdinConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(IConstantesOdinConfig.pathFindingSeuilProximiteSafe)
                .pathFindingAngle(IConstantesOdinConfig.pathFindingAngle)
                .pathFindingAngleSafe(IConstantesOdinConfig.pathFindingAngleSafe)

                .seuilAlimentationServos(IConstantesServosOdin.SEUIL_ALIMENTATION_VOLTS)
                .servosMinTimeMax(IConstantesServosOdin.MIN_TIME_MAX)
                .servosBatch(IConstantesServosOdin.BATCH_CONFIG)

                .vitesse(IConstantesOdinConfig.vitesseMin, IConstantesOdinConfig.vitesseMax, 100)
                .vitesseOrientation(IConstantesOdinConfig.vitesseOrientationMin, IConstantesOdinConfig.vitesseOrientationMax, 100)
                .fenetreArretDistance(conv.mmToPulse(IConstantesOdinConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(IConstantesOdinConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(IConstantesOdinConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(IConstantesOdinConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(IConstantesOdinConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(IConstantesOdinConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(IConstantesOdinConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(IConstantesOdinConfig.startAngleLimitVitesseDistance))
                .sampleTimeS(IConstantesOdinConfig.asservTimeS);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesOdinConfig.countPerMm, IConstantesOdinConfig.countPerDeg);
    }

    @Bean
    public IAsservissementPolaire asservissement() {
        // TODO Config pour Odin a faire
        // Positive Min moteur Gauche : 102
        // Negative Min moteur Gauche : -37
        LimiterFilter limiterMoteurGauche = new LimiterFilter(35d, 4095d, LimiterType.MIRROR);

        // TODO Config pour Odin a faire
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
        pid.setTunings(IConstantesOdinConfig.kpDistance, IConstantesOdinConfig.kiDistance, IConstantesOdinConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
        pid.setTunings(IConstantesOdinConfig.kpOrientation, IConstantesOdinConfig.kiOrientation, IConstantesOdinConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", IConstantesOdinConfig.asservTimeMs, IConstantesOdinConfig.rampAccDistance, IConstantesOdinConfig.rampDecDistance, IConstantesOdinConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", IConstantesOdinConfig.asservTimeMs, IConstantesOdinConfig.rampAccOrientation, IConstantesOdinConfig.rampDecOrientation, IConstantesOdinConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(IConstantesOdinConfig.pathFindingAlgo);
            pf.setSaveImages(env.getProperty("robot.pathfinding.saveImages", Boolean.class, true));
            return pf;
        } else {
            return new NoPathFinderImpl();
        }
    }

    @Bean
    public OdinRobotStatus robotStatus() {
        return new OdinRobotStatus();
    }

    @Bean
    public IRobotGroup robotGroup(Environment env, ExecutorService taskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.port", Integer.class);
        final String nerellHost = env.getRequiredProperty("nerell.socket.host");
        final Integer nerellPort = env.getRequiredProperty("nerell.socket.port", Integer.class);
        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(serverPort, nerellHost, nerellPort, taskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    @DependsOn({"ecran", "rplidar"})
    public OdinOrdonanceur ordonanceur() {
        return new OdinOrdonanceur();
    }

    @Bean
    public ISystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManager(
                conv.mmToPulse(IConstantesOdinConfig.seuilErreurDistanceMm),
                conv.degToPulse(IConstantesOdinConfig.seuilErreurOrientationDeg)
        );
    }
}
