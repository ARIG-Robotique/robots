package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.PamiOrdonanceur;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranMatchInfo;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.model.ecran.EcranState;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManagerImpl;
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
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class PamiCommonContext {
    @Autowired
    private Environment env;

    @Bean
    public RobotConfig robotConfig(ConvertionRobotUnit conv) {
        return new RobotConfig()
                .asservTimeMs(PamiConstantesConfig.asservTimeMs)
                .calageTimeMs(PamiConstantesConfig.calageGlobalTimeMs, PamiConstantesConfig.calageCourtTimeMs)
                .i2cReadTimeMs(PamiConstantesConfig.i2cReadTimeMs)
                .sampleTimeS(PamiConstantesConfig.asservTimeS)

                .pathFindingTailleObstacle(PamiConstantesConfig.pathFindingTailleObstacle)
                .pathFindingTailleObstacleArig(PamiConstantesConfig.pathFindingTailleObstacleArig)
                .lidarOffsetPointMm(PamiConstantesConfig.lidarOffsetPointMm)
                .lidarClusterSizeMm(PamiConstantesConfig.lidarClusterSizeMm)
                .avoidanceWaitTimeMs(PamiConstantesConfig.avoidanceWaitTimeMs)
                .avoidanceWaitTimeLongMs(PamiConstantesConfig.avoidanceWaitTimeLongMs)
                .pathFindingSeuilProximite(PamiConstantesConfig.pathFindingSeuilProximite)
                .pathFindingSeuilProximiteSafe(PamiConstantesConfig.pathFindingSeuilProximiteSafe)
                .pathFindingSeuilProximiteArig(PamiConstantesConfig.pathFindingSeuilProximiteArig)
                .pathFindingAngle(PamiConstantesConfig.pathFindingAngle)
                .pathFindingAngleSafe(PamiConstantesConfig.pathFindingAngleSafe)

                .seuilTensionServos(PamiConstantesConfig.seuilAlimentationServosVolts)
                .seuilTensionMoteurs(PamiConstantesConfig.seuilAlimentationMoteursVolts)

                .distanceCalageAvant(PamiConstantesConfig.dstCallage)
                .distanceCalageArriere(PamiConstantesConfig.dstCallage)

                .vitesse(PamiConstantesConfig.vitesseMin, PamiConstantesConfig.vitesseMax, 100)
                .rampeDistance(PamiConstantesConfig.rampAccDistance, PamiConstantesConfig.rampDecDistance)
                .vitesseOrientation(PamiConstantesConfig.vitesseOrientationMin, PamiConstantesConfig.vitesseOrientationMax, 100)
                .rampeOrientation(PamiConstantesConfig.rampAccOrientation, PamiConstantesConfig.rampDecOrientation)
                .fenetreArretDistance(conv.mmToPulse(PamiConstantesConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(PamiConstantesConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(PamiConstantesConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(PamiConstantesConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(PamiConstantesConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(PamiConstantesConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(PamiConstantesConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(PamiConstantesConfig.startAngleLimitVitesseDistance))

                .waitLed(PamiConstantesConfig.WAIT_LED)
                .timeoutPompe(PamiConstantesConfig.TIMEOUT_POMPE)
                .timeoutColor(PamiConstantesConfig.TIMEOUT_COLOR);
    }

    @Bean
    public MonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("robot.monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(PamiConstantesConfig.countPerMm, PamiConstantesConfig.countPerDeg);
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
        SimplePidFilter pid = new SimplePidFilter("distance", 128d);
        pid.setTunings(PamiConstantesConfig.kpDistance, PamiConstantesConfig.kiDistance, PamiConstantesConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public PidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation", 128d);
        pid.setTunings(PamiConstantesConfig.kpOrientation, PamiConstantesConfig.kiOrientation, PamiConstantesConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", PamiConstantesConfig.asservTimeMs, PamiConstantesConfig.rampAccDistance, PamiConstantesConfig.rampDecDistance, PamiConstantesConfig.gainVitesseRampeDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", PamiConstantesConfig.asservTimeMs, PamiConstantesConfig.rampAccOrientation, PamiConstantesConfig.rampDecOrientation, PamiConstantesConfig.gainVitesseRampeOrientation);
    }

    @Bean
    public PathFinder pathFinder() {
        boolean enable = env.getProperty("robot.pathfinding.enable", Boolean.class, true);

        if (enable) {
            MultiPathFinderImpl pf = new GameMultiPathFinderImpl();
            pf.setAlgorithm(PamiConstantesConfig.pathFindingAlgo);
            pf.setSaveImages(env.getProperty("robot.pathfinding.saveImages", Boolean.class, true));
            return pf;
        } else {
            return new NoPathFinderImpl();
        }
    }

    @Bean
    public PamiRobotStatus robotStatus() {
        return new PamiRobotStatus();
    }

    @Bean
    public RobotGroup robotGroup(PamiRobotStatus pamiRobotStatus, Environment env, ThreadPoolExecutor threadPoolTaskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.port", Integer.class);
        final String nerellHost = env.getRequiredProperty("nerell.socket.host");
        final Integer nerellPort = env.getRequiredProperty("nerell.socket.port", Integer.class);
        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(pamiRobotStatus, serverPort, nerellHost, nerellPort, threadPoolTaskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public PamiOrdonanceur ordonanceur() {
        return new PamiOrdonanceur();
    }

    @Bean
    public SystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManagerImpl(
                conv.mmToPulse(PamiConstantesConfig.seuilErreurDistanceMm),
                conv.degToPulse(PamiConstantesConfig.seuilErreurOrientationDeg),
                PamiConstantesConfig.maxErrorSumDistance,
                PamiConstantesConfig.maxErrorSumOrientation
        );
    }

    @Bean
    public IEcran<EcranConfig, EcranState> ecran() {
        return new IEcran<>() {
            @Override
            public void end() { }

            @Override
            public boolean setParams(EcranParams params) {
                return true;
            }

            @Override
            public EcranConfig configInfos() {
                return new EcranConfig();
            }

            @Override
            public void updateState(EcranState data) { }

            @Override
            public void updateMatch(EcranMatchInfo data) { }

            @Override
            public void updatePhoto(EcranPhoto photo) { }
        };
    }
}
