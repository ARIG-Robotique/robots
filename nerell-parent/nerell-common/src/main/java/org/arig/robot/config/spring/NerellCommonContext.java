package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranState;
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
public class NerellCommonContext {

    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(INerellConstantesConfig.asservTimeMs)
                .calageTimeMs(INerellConstantesConfig.calageTimeMs)
                .i2cReadTimeMs(INerellConstantesConfig.i2cReadTimeMs)

                .pathFindingTailleObstacle(INerellConstantesConfig.pathFindingTailleObstacle)
                .lidarOffsetPointMm(INerellConstantesConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(INerellConstantesConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(INerellConstantesConfig.avoidanceWaitTimeMs)
                .pathFindingSeuilProximite(INerellConstantesConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(INerellConstantesConfig.pathFindingSeuilProximiteSafe)
                .pathFindingAngle(INerellConstantesConfig.pathFindingAngle)
                .pathFindingAngleSafe(INerellConstantesConfig.pathFindingAngleSafe)

                .seuilTensionServos(INerellConstantesConfig.seuilAlimentationServosVolts)
                .seuilTensionMoteurs(0) // Pas de mesure sur Nerell

                .vitesse(INerellConstantesConfig.vitesseMin, INerellConstantesConfig.vitesseMax, 100)
                .vitesseOrientation(INerellConstantesConfig.vitesseOrientationMin, INerellConstantesConfig.vitesseOrientationMax, 100)
                .fenetreArretDistance(conv.mmToPulse(INerellConstantesConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(INerellConstantesConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(INerellConstantesConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(INerellConstantesConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(INerellConstantesConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(INerellConstantesConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(INerellConstantesConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(INerellConstantesConfig.startAngleLimitVitesseDistance))
                .sampleTimeS(INerellConstantesConfig.asservTimeS);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(INerellConstantesConfig.countPerMm, INerellConstantesConfig.countPerDeg);
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
        pid.setTunings(INerellConstantesConfig.kpDistance, INerellConstantesConfig.kiDistance, INerellConstantesConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
        pid.setTunings(INerellConstantesConfig.kpOrientation, INerellConstantesConfig.kiOrientation, INerellConstantesConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", INerellConstantesConfig.asservTimeMs, INerellConstantesConfig.rampAccDistance, INerellConstantesConfig.rampDecDistance, INerellConstantesConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", INerellConstantesConfig.asservTimeMs, INerellConstantesConfig.rampAccOrientation, INerellConstantesConfig.rampDecOrientation, INerellConstantesConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(INerellConstantesConfig.pathFindingAlgo);
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
    public IRobotGroup robotGroup(Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
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
    public ISystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManager(
                conv.mmToPulse(INerellConstantesConfig.seuilErreurDistanceMm),
                conv.degToPulse(INerellConstantesConfig.seuilErreurOrientationDeg)
        );
    }
}
