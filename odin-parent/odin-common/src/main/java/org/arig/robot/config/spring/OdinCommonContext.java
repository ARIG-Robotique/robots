package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.constants.IOdinConstantesConfig;
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
import org.arig.robot.system.capteurs.EcranOverSocket;
import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.group.IRobotGroup;
import org.arig.robot.system.motion.AsservissementPolaireDistanceOrientation;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.GameMultiPathFinderImpl;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.system.pathfinding.impl.NoPathFinderImpl;
import org.arig.robot.system.process.EcranProcess;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class OdinCommonContext {
    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(IOdinConstantesConfig.asservTimeMs)
                .calageTimeMs(IOdinConstantesConfig.calageTimeMs)
                .i2cReadTimeMs(IOdinConstantesConfig.i2cReadTimeMs)

                .pathFindingTailleObstacle(IOdinConstantesConfig.pathFindingTailleObstacle)
                .lidarOffsetPointMm(IOdinConstantesConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(IOdinConstantesConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(IOdinConstantesConfig.avoidanceWaitTimeMs)
                .pathFindingSeuilProximite(IOdinConstantesConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(IOdinConstantesConfig.pathFindingSeuilProximiteSafe)
                .pathFindingAngle(IOdinConstantesConfig.pathFindingAngle)
                .pathFindingAngleSafe(IOdinConstantesConfig.pathFindingAngleSafe)

                .seuilTensionServos(IOdinConstantesConfig.seuilAlimentationServosVolts)
                .seuilTensionMoteurs(IOdinConstantesConfig.seuilAlimentationMoteursVolts)

                .vitesse(IOdinConstantesConfig.vitesseMin, IOdinConstantesConfig.vitesseMax, 100)
                .vitesseOrientation(IOdinConstantesConfig.vitesseOrientationMin, IOdinConstantesConfig.vitesseOrientationMax, 100)
                .fenetreArretDistance(conv.mmToPulse(IOdinConstantesConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(IOdinConstantesConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(IOdinConstantesConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(IOdinConstantesConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(IOdinConstantesConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(IOdinConstantesConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(IOdinConstantesConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(IOdinConstantesConfig.startAngleLimitVitesseDistance))
                .sampleTimeS(IOdinConstantesConfig.asservTimeS);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IOdinConstantesConfig.countPerMm, IOdinConstantesConfig.countPerDeg);
    }

    @Bean
    public IAsservissementPolaire asservissement() {
        LimiterFilter limiterMoteurGauche = new LimiterFilter(0d, 4095d, LimiterType.MIRROR);
        LimiterFilter limiterMoteurDroit = new LimiterFilter(0d, 4095d, LimiterType.MIRROR);
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
        pid.setTunings(IOdinConstantesConfig.kpDistance, IOdinConstantesConfig.kiDistance, IOdinConstantesConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
        pid.setTunings(IOdinConstantesConfig.kpOrientation, IOdinConstantesConfig.kiOrientation, IOdinConstantesConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", IOdinConstantesConfig.asservTimeMs, IOdinConstantesConfig.rampAccDistance, IOdinConstantesConfig.rampDecDistance, IOdinConstantesConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", IOdinConstantesConfig.asservTimeMs, IOdinConstantesConfig.rampAccOrientation, IOdinConstantesConfig.rampDecOrientation, IOdinConstantesConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(IOdinConstantesConfig.pathFindingAlgo);
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
    public IRobotGroup robotGroup(Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.port", Integer.class);
        final String nerellHost = env.getRequiredProperty("nerell.socket.host");
        final Integer nerellPort = env.getRequiredProperty("nerell.socket.port", Integer.class);
        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(serverPort, nerellHost, nerellPort, threadPoolTaskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public EcranProcess ecranProcess(Environment env) {
        final String ecranSocket = env.getRequiredProperty("ecran.socket");
        final String ecranBinary = env.getRequiredProperty("ecran.binary");
        return new EcranProcess(ecranBinary, ecranSocket);
    }

    @Bean
    public IEcran ecran(EcranProcess ecranProcess) throws Exception {
        final File socketFile = new File(ecranProcess.getSocketPath());
        return new EcranOverSocket(socketFile);
    }

    @Bean
    @DependsOn({"ecran", "rplidar"})
    public OdinOrdonanceur ordonanceur() {
        return new OdinOrdonanceur();
    }

    @Bean
    public ISystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManager(
                conv.mmToPulse(IOdinConstantesConfig.seuilErreurDistanceMm),
                conv.degToPulse(IOdinConstantesConfig.seuilErreurOrientationDeg)
        );
    }
}
