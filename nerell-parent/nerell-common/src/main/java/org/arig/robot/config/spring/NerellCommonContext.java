package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranState;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.RobotGroupService;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManagerImpl;
import org.arig.robot.system.capteurs.EcranOverSocket;
import org.arig.robot.system.capteurs.socket.IEcran;
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
public class NerellCommonContext {

    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(NerellConstantesConfig.asservTimeMs)
                .calageTimeMs(NerellConstantesConfig.calageGlobalTimeMs, NerellConstantesConfig.calageCourtTimeMs)
                .i2cReadTimeMs(NerellConstantesConfig.i2cReadTimeMs)
                .sampleTimeS(NerellConstantesConfig.asservTimeS)

                .pathFindingTailleObstacle(NerellConstantesConfig.pathFindingTailleObstacle)
                .pathFindingTailleObstacleArig(NerellConstantesConfig.pathFindingTailleObstacleArig)
                .lidarOffsetPointMm(NerellConstantesConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(NerellConstantesConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(NerellConstantesConfig.avoidanceWaitTimeMs)
                .avoidanceWaitTimeLongMs(NerellConstantesConfig.avoidanceWaitTimeLongMs)
                .pathFindingSeuilProximite(NerellConstantesConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(NerellConstantesConfig.pathFindingSeuilProximiteSafe)
                .pathFindingSeuilProximiteArig(NerellConstantesConfig.pathFindingSeuilProximiteArig)
                .pathFindingAngle(NerellConstantesConfig.pathFindingAngle)
                .pathFindingAngleSafe(NerellConstantesConfig.pathFindingAngleSafe)

                .seuilTensionServos(NerellConstantesConfig.seuilAlimentationServosVolts)
                .seuilTensionMoteurs(NerellConstantesConfig.seuilAlimentationMoteursVolts)

                .distanceCalageAvant(NerellConstantesConfig.dstCallage)
                .distanceCalageArriere(NerellConstantesConfig.dstCallage)

                .vitesse(NerellConstantesConfig.vitesseMin, NerellConstantesConfig.vitesseMax, 100)
                .rampeDistance(NerellConstantesConfig.rampAccDistance, NerellConstantesConfig.rampDecDistance)
                .vitesseOrientation(NerellConstantesConfig.vitesseOrientationMin, NerellConstantesConfig.vitesseOrientationMax, 100)
                .rampeOrientation(NerellConstantesConfig.rampAccOrientation, NerellConstantesConfig.rampDecOrientation)
                .fenetreArretDistance(conv.mmToPulse(NerellConstantesConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(NerellConstantesConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(NerellConstantesConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(NerellConstantesConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(NerellConstantesConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(NerellConstantesConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(NerellConstantesConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(NerellConstantesConfig.startAngleLimitVitesseDistance))

                .waitLed(NerellConstantesConfig.WAIT_LED)
                .timeoutPompe(NerellConstantesConfig.TIMEOUT_POMPE)
                .timeoutColor(NerellConstantesConfig.TIMEOUT_COLOR);
    }

    @Bean
    public MonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(NerellConstantesConfig.countPerMm, NerellConstantesConfig.entraxe, true);
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
    public PidFilter pidDistance() {
        log.info("Configuration PID Distance");
        SimplePidFilter pid = new SimplePidFilter("distance", 4096d);
        pid.setTunings(NerellConstantesConfig.kpDistance, NerellConstantesConfig.kiDistance, NerellConstantesConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public PidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation", 4096d);
        pid.setTunings(NerellConstantesConfig.kpOrientation, NerellConstantesConfig.kiOrientation, NerellConstantesConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", NerellConstantesConfig.asservTimeMs, NerellConstantesConfig.rampAccDistance, NerellConstantesConfig.rampDecDistance, NerellConstantesConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", NerellConstantesConfig.asservTimeMs, NerellConstantesConfig.rampAccOrientation, NerellConstantesConfig.rampDecOrientation, NerellConstantesConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public PathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(NerellConstantesConfig.pathFindingAlgo);
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
    public RobotGroup pamiTriangleGroup(NerellRobotStatus nerellRobotStatus, Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.pamis.triangle.port", Integer.class);

        final String otherHost = env.getRequiredProperty("pamis.triangle.socket.host");
        final Integer otherPort = env.getRequiredProperty("pamis.triangle.socket.port", Integer.class);

        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(nerellRobotStatus, AbstractRobotStatus::pamiTriangleGroupOk, serverPort, otherHost, otherPort, threadPoolTaskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public RobotGroupService pamiTriangleGroupService(final EurobotStatus rs, final RobotGroup pamiTriangleGroup, final ThreadPoolExecutor threadPoolTaskExecutor) {
        return new RobotGroupService(rs, pamiTriangleGroup, threadPoolTaskExecutor);
    }

    @Bean
    public RobotGroup pamiCarreGroup(NerellRobotStatus nerellRobotStatus, Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.pamis.carre.port", Integer.class);

        final String otherHost = env.getRequiredProperty("pamis.carre.socket.host");
        final Integer otherPort = env.getRequiredProperty("pamis.carre.socket.port", Integer.class);

        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(nerellRobotStatus, AbstractRobotStatus::pamiCarreGroupOk, serverPort, otherHost, otherPort, threadPoolTaskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public RobotGroupService pamiCarreGroupService(final EurobotStatus rs, final RobotGroup pamiCarreGroup, final ThreadPoolExecutor threadPoolTaskExecutor) {
        return new RobotGroupService(rs, pamiCarreGroup, threadPoolTaskExecutor);
    }

    /*
    @Bean
    public RobotGroup pamiRondGroup(NerellRobotStatus nerellRobotStatus, Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.pamis.rond.port", Integer.class);

        final String otherHost = env.getRequiredProperty("pamis.rond.socket.host");
        final Integer otherPort = env.getRequiredProperty("pamis.rond.socket.port", Integer.class);

        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(nerellRobotStatus, AbstractRobotStatus::pamiRondGroupOk, serverPort, otherHost, otherPort, threadPoolTaskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public RobotGroupService pamiRondGroupService(final EurobotStatus rs, final RobotGroup pamiRondGroup, final ThreadPoolExecutor threadPoolTaskExecutor) {
        return new RobotGroupService(rs, pamiRondGroup, threadPoolTaskExecutor);
    }

    @Bean
    public RobotGroup pamiStarGroup(NerellRobotStatus nerellRobotStatus, Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.pamis.star.port", Integer.class);

        final String otherHost = env.getRequiredProperty("pamis.star.socket.host");
        final Integer otherPort = env.getRequiredProperty("pamis.star.socket.port", Integer.class);

        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(nerellRobotStatus, AbstractRobotStatus::pamiStarGroupOk, serverPort, otherHost, otherPort, threadPoolTaskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public RobotGroupService pamiStarGroupService(final EurobotStatus rs, final RobotGroup pamiStarGroup, final ThreadPoolExecutor threadPoolTaskExecutor) {
        return new RobotGroupService(rs, pamiStarGroup, threadPoolTaskExecutor);
    }
    */

    @Bean
    public EcranProcess ecranProcess(Environment env) {
        final String ecranSocket = env.getRequiredProperty("ecran.socket.file");
        final String ecranBinary = env.getRequiredProperty("ecran.binary");
        return new EcranProcess(ecranBinary, ecranSocket);
    }

    @Bean
    public IEcran<EcranConfig, EcranState> ecran(EcranProcess ecranProcess) {
        final File socketFile = new File(ecranProcess.getSocketPath());
        return new EcranOverSocket(socketFile);
    }

    @Bean
    @DependsOn({"ecran", "rplidar"})
    public NerellOrdonanceur ordonanceur() {
        return new NerellOrdonanceur();
    }

    @Bean
    public SystemBlockerManager systemBlockerManager() {
        return new SystemBlockerManagerImpl();
    }
}
