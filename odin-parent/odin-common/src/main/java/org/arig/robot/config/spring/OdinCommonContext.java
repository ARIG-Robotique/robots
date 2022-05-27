package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.OdinOrdonanceur;
import org.arig.robot.constants.OdinConstantesConfig;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranState;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManagerImpl;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.system.capteurs.EcranOverSocket;
import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.group.RobotGroup;
import org.arig.robot.system.motion.AsservissementPolaireDistanceOrientation;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.GameMultiPathFinderImpl;
import org.arig.robot.system.pathfinding.MultiPathFinderImpl;
import org.arig.robot.system.pathfinding.NoPathFinderImpl;
import org.arig.robot.system.pathfinding.PathFinder;
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
                .asservTimeMs(OdinConstantesConfig.asservTimeMs)
                .calageTimeMs(OdinConstantesConfig.calageGlobalTimeMs, OdinConstantesConfig.calageCourtTimeMs)
                .i2cReadTimeMs(OdinConstantesConfig.i2cReadTimeMs)
                .sampleTimeS(OdinConstantesConfig.asservTimeS)

                .pathFindingTailleObstacle(OdinConstantesConfig.pathFindingTailleObstacle)
                .pathFindingTailleObstacleArig(OdinConstantesConfig.pathFindingTailleObstacleArig)
                .lidarOffsetPointMm(OdinConstantesConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(OdinConstantesConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(OdinConstantesConfig.avoidanceWaitTimeMs)
                .avoidanceWaitTimeLongMs(OdinConstantesConfig.avoidanceWaitTimeLongMs)
                .pathFindingSeuilProximite(OdinConstantesConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(OdinConstantesConfig.pathFindingSeuilProximiteSafe)
                .pathFindingSeuilProximiteArig(OdinConstantesConfig.pathFindingSeuilProximiteArig)
                .pathFindingAngle(OdinConstantesConfig.pathFindingAngle)
                .pathFindingAngleSafe(OdinConstantesConfig.pathFindingAngleSafe)

                .seuilTensionServos(OdinConstantesConfig.seuilAlimentationServosVolts)
                .seuilTensionMoteurs(OdinConstantesConfig.seuilAlimentationMoteursVolts)

                .distanceCalageAvant(OdinConstantesConfig.dstCallage)
                .distanceCalageArriere(OdinConstantesConfig.dstCallage)

                .vitesse(OdinConstantesConfig.vitesseMin, OdinConstantesConfig.vitesseMax, 100)
                .rampeDistance(OdinConstantesConfig.rampAccDistance, OdinConstantesConfig.rampDecDistance)
                .vitesseOrientation(OdinConstantesConfig.vitesseOrientationMin, OdinConstantesConfig.vitesseOrientationMax, 100)
                .rampeOrientation(OdinConstantesConfig.rampAccOrientation, OdinConstantesConfig.rampDecOrientation)
                .fenetreArretDistance(conv.mmToPulse(OdinConstantesConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(OdinConstantesConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(OdinConstantesConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(OdinConstantesConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(OdinConstantesConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(OdinConstantesConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(OdinConstantesConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(OdinConstantesConfig.startAngleLimitVitesseDistance))

                .waitLed(OdinConstantesConfig.WAIT_LED)
                .timeoutPompe(OdinConstantesConfig.TIMEOUT_POMPE)
                .timeoutColor(OdinConstantesConfig.TIMEOUT_COLOR);
    }

    @Bean
    public MonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(OdinConstantesConfig.countPerMm, OdinConstantesConfig.entraxe, true);
    }

    @Bean
    public IAsservissementPolaire asservissement() {
        LimiterFilter limiterMoteurGauche = new LimiterFilter(100d, 4095d, LimiterType.MIRROR);
        LimiterFilter limiterMoteurDroit = new LimiterFilter(100d, 4095d, LimiterType.MIRROR);
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
    public PidFilter pidDistance() {
        log.info("Configuration PID Distance");
        SimplePidFilter pid = new SimplePidFilter("distance");
        pid.setTunings(OdinConstantesConfig.kpDistance, OdinConstantesConfig.kiDistance, OdinConstantesConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public PidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
        pid.setTunings(OdinConstantesConfig.kpOrientation, OdinConstantesConfig.kiOrientation, OdinConstantesConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", OdinConstantesConfig.asservTimeMs, OdinConstantesConfig.rampAccDistance, OdinConstantesConfig.rampDecDistance, OdinConstantesConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", OdinConstantesConfig.asservTimeMs, OdinConstantesConfig.rampAccOrientation, OdinConstantesConfig.rampDecOrientation, OdinConstantesConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public PathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(OdinConstantesConfig.pathFindingAlgo);
            pf.setSaveImages(env.getProperty("robot.pathfinding.saveImages", Boolean.class, true));
            return pf;
        } else {
            return new NoPathFinderImpl();
        }
    }

    @Bean
    public OdinRobotStatus robotStatus(CarreFouilleReader carreFouilleReader) {
        return new OdinRobotStatus(carreFouilleReader);
    }

    @Bean
    public RobotGroup robotGroup(OdinRobotStatus odinRobotStatus, Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.port", Integer.class);
        final String nerellHost = env.getRequiredProperty("nerell.socket.host");
        final Integer nerellPort = env.getRequiredProperty("nerell.socket.port", Integer.class);
        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(odinRobotStatus, serverPort, nerellHost, nerellPort, threadPoolTaskExecutor);
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
    public IEcran<EcranConfig, EcranState> ecran(EcranProcess ecranProcess) throws Exception {
        final File socketFile = new File(ecranProcess.getSocketPath());
        return new EcranOverSocket(socketFile);
    }

    @Bean
    @DependsOn({"ecran", "rplidar"})
    public OdinOrdonanceur ordonanceur() {
        return new OdinOrdonanceur();
    }

    @Bean
    public SystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManagerImpl(
                conv.mmToPulse(OdinConstantesConfig.seuilErreurDistanceMm),
                conv.degToPulse(OdinConstantesConfig.seuilErreurOrientationDeg),
                OdinConstantesConfig.maxErrorSumDistance,
                OdinConstantesConfig.maxErrorSumOrientation
        );
    }
}
