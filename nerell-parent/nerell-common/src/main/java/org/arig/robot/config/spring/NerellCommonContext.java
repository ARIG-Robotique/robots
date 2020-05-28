package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.NerellOrdonanceur;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.common.LimiterFilter;
import org.arig.robot.filters.common.LimiterFilter.LimiterType;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellStatus;
import org.arig.robot.model.Position;
import org.arig.robot.system.ILidarService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.LidarService;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.system.TrajectoryManagerConfig;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.motion.AsservissementPolaireDistanceOrientation;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.system.pathfinding.impl.NoPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class NerellCommonContext {

    @Autowired
    private Environment env;

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesNerellConfig.countPerMm, IConstantesNerellConfig.countPerDeg);
    }

    @Bean
    public TableUtils tableUtils() {
        return new TableUtils(IConstantesNerellConfig.minX, IConstantesNerellConfig.maxX,
                IConstantesNerellConfig.minY, IConstantesNerellConfig.maxY);
    }

    @Bean
    public ILidarService lidarService() {
        return new LidarService(
                IConstantesNerellConfig.pathFindingTailleObstacle,
                IConstantesNerellConfig.lidarOffsetPointMm,
                IConstantesNerellConfig.lidarClusterSizeMm
        );
    }

    @Bean
    @Primary
    public ITrajectoryManager trajectoryManager(ConvertionRobotUnit conv) {
        final TrajectoryManagerConfig config = TrajectoryManagerConfig.builder()
                .fenetreArretDistance(conv.mmToPulse(IConstantesNerellConfig.arretDistanceMm))
                .fenetreApprocheAvecFreinDistance(conv.mmToPulse(IConstantesNerellConfig.approcheAvecFreinDistanceMm))
                .fenetreApprocheSansFreinDistance(conv.mmToPulse(IConstantesNerellConfig.approcheSansFreinDistanceMm))
                .fenetreArretOrientation(conv.degToPulse(IConstantesNerellConfig.arretOrientDeg))
                .fenetreApprocheAvecFreinOrientation(conv.degToPulse(IConstantesNerellConfig.approcheAvecFreinOrientationDeg))
                .fenetreApprocheSansFreinOrientation(conv.degToPulse(IConstantesNerellConfig.approcheSansFreinOrientationDeg))
                .startAngleDemiTour(conv.degToPulse(IConstantesNerellConfig.startAngleDemiTourDeg))
                .startAngleLimitSpeedDistance(conv.degToPulse(IConstantesNerellConfig.startAngleLimitVitesseDistance))
                .build();

        return new TrajectoryManager(config);
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
            MultiPathFinderImpl pf = new MultiPathFinderImpl();
            pf.setAlgorithm(IConstantesNerellConfig.pathFindingAlgo);
            pf.setSaveImages(env.getProperty("robot.pathfinding.saveImages", Boolean.class, true));
            return pf;
        } else {
            return new NoPathFinderImpl();
        }
    }

    @Bean
    public NerellStatus robotStatus() {
        return new NerellStatus(IConstantesNerellConfig.matchTimeMs);
    }

    @Bean
    @DependsOn({"ecran", "rplidar"})
    public NerellOrdonanceur ordonanceur() {
        return NerellOrdonanceur.getInstance();
    }

    @Bean
    public ISystemBlockerManager systemBlockerManager(ConvertionRobotUnit conv) {
        return new SystemBlockerManager(
                conv.mmToPulse(IConstantesNerellConfig.seuilErreurDistanceMm),
                conv.degToPulse(IConstantesNerellConfig.seuilErreurOrientationDeg)
        );
    }
}
