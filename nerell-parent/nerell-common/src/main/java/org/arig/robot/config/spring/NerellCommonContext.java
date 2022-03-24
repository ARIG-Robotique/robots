package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranState;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManagerImpl;
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
public class NerellCommonContext {

    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(NerellConstantesConfig.asservTimeMs)
                .calageTimeMs(NerellConstantesConfig.calageTimeMs)
                .i2cReadTimeMs(NerellConstantesConfig.i2cReadTimeMs)

                .pathFindingTailleObstacle(NerellConstantesConfig.pathFindingTailleObstacle)
                .lidarOffsetPointMm(NerellConstantesConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(NerellConstantesConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(NerellConstantesConfig.avoidanceWaitTimeMs)
                .pathFindingSeuilProximite(NerellConstantesConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(NerellConstantesConfig.pathFindingSeuilProximiteSafe)
                .pathFindingAngle(NerellConstantesConfig.pathFindingAngle)
                .pathFindingAngleSafe(NerellConstantesConfig.pathFindingAngleSafe)

                .seuilTensionServos(NerellConstantesConfig.seuilAlimentationServosVolts)
                .seuilTensionMoteurs(0) // Pas de mesure sur Nerell

                .vitesse(NerellConstantesConfig.vitesseMin, NerellConstantesConfig.vitesseMax, 100)
                .vitesseOrientation(NerellConstantesConfig.vitesseOrientationMin, NerellConstantesConfig.vitesseOrientationMax, 100)
                .fenetreArretDistance(conv.mmToPulse(NerellConstantesConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(NerellConstantesConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(NerellConstantesConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(NerellConstantesConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(NerellConstantesConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(NerellConstantesConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(NerellConstantesConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(NerellConstantesConfig.startAngleLimitVitesseDistance))
                .sampleTimeS(NerellConstantesConfig.asservTimeS);
    }

    @Bean
    public MonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(NerellConstantesConfig.countPerMm, NerellConstantesConfig.countPerDeg);
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
    public PidFilter pidDistance() {
        log.info("Configuration PID Distance");
        SimplePidFilter pid = new SimplePidFilter("distance");
        pid.setTunings(NerellConstantesConfig.kpDistance, NerellConstantesConfig.kiDistance, NerellConstantesConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public PidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
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
    public RobotGroup robotGroup(Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.port", Integer.class);
        final String odinHost = env.getRequiredProperty("odin.socket.host");
        final Integer odinPort = env.getRequiredProperty("odin.socket.port", Integer.class);
        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(serverPort, odinHost, odinPort, threadPoolTaskExecutor);
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
    public SystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManagerImpl(
                conv.mmToPulse(NerellConstantesConfig.seuilErreurDistanceMm),
                conv.degToPulse(NerellConstantesConfig.seuilErreurOrientationDeg)
        );
    }
}
