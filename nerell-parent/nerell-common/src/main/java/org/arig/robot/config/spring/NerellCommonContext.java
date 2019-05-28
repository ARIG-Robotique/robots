package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IRobotSide;
import org.arig.robot.services.LeftSideService;
import org.arig.robot.services.RightSideService;
import org.arig.robot.system.CarouselManager;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.system.TrajectoryManagerAsync;
import org.arig.robot.system.blockermanager.ISystemBlockerManager;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.encoders.AbstractEncoder;
import org.arig.robot.system.motion.AsservissementPolaireDistanceOrientation;
import org.arig.robot.system.motion.AsservissementPosition;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ConvertionCarouselUnit;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author gdepuille on 23/12/14.
 */
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
    public ConvertionCarouselUnit convertisseurCarousel() {
        return new ConvertionCarouselUnit(IConstantesNerellConfig.countPerCarouselIndex);
    }

    @Bean
    public TableUtils tableUtils() {
        return new TableUtils(IConstantesNerellConfig.minX, IConstantesNerellConfig.maxX,
                IConstantesNerellConfig.minY, IConstantesNerellConfig.maxY);
    }

    @Bean
    @Primary
    public ITrajectoryManager trajectoryManager() {
        return new TrajectoryManager(IConstantesNerellConfig.arretDistanceMm, IConstantesNerellConfig.approcheDistanceMm,
                IConstantesNerellConfig.arretOrientDeg, IConstantesNerellConfig.approcheOrientationDeg,
                IConstantesNerellConfig.angleReculDeg);
    }

    @Bean(value = "trajectoryManagerAsync")
    public ITrajectoryManager trajectoryManagerAsync() {
        return new TrajectoryManagerAsync(trajectoryManager());
    }

    @Bean
    public ICarouselManager carouselManager() {
        return new CarouselManager(IConstantesNerellConfig.arretCarouselPulse);
    }

    @Bean
    public IAsservissementPolaire asservissement() {
        return new AsservissementPolaireDistanceOrientation();
    }

    @Bean
    public IAsservissement asservissementCarousel(CommandeAsservissementPosition cmdAsservCarousel, AbstractEncoder carouselEncoder, IPidFilter pidCarousel, TrapezoidalRampFilter rampCarousel) {
        return new AsservissementPosition(cmdAsservCarousel, carouselEncoder, pidCarousel, rampCarousel);
    }

    @Bean
    public IOdometrie odometrie() {
        return new OdometrieLineaire();
    }

    @Bean
    public CommandeRobot cmdRobot() {
        return new CommandeRobot();
    }

    @Bean
    public CommandeAsservissementPosition cmdAsservCarousel() {
        return new CommandeAsservissementPosition();
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

    @Bean(name = "pidCarousel")
    public IPidFilter pidCarousel() {
        log.info("Configuration PID Carousel");
        SimplePidFilter pid = new SimplePidFilter("carousel");
        pid.setTunings(IConstantesNerellConfig.kpCarousel, IConstantesNerellConfig.kiCarousel, IConstantesNerellConfig.kdCarousel);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccDistance, IConstantesNerellConfig.rampDecDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccOrientation, IConstantesNerellConfig.rampDecOrientation);
    }

    @Bean(name = "rampCarousel")
    public TrapezoidalRampFilter rampCarousel() {
        log.info("Configuration TrapezoidalRampFilter Carousel");
        return new TrapezoidalRampFilter("carousel", IConstantesNerellConfig.asservTimeCarouselMs, IConstantesNerellConfig.rampAccCarousel, IConstantesNerellConfig.rampDecCarousel);
    }

    @Bean
    public IPathFinder pathFinder() {
        MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setAlgorithm(IConstantesNerellConfig.pathFindingAlgo);
        pf.setSaveImages(env.getProperty("robot.pathfinding.saveImages", Boolean.class, true));
        return pf;
    }

    @Bean
    public RobotStatus robotStatus() {
        return new RobotStatus();
    }

    @Bean
    public Ordonanceur ordonanceur() {
        return Ordonanceur.getInstance();
    }

    @Bean(name = "sideServices")
    public Map<ESide, IRobotSide> sideServices(RightSideService rightSideService, LeftSideService leftSideService) {
        final Map<ESide, IRobotSide> services = new EnumMap<>(ESide.class);
        services.put(ESide.DROITE, rightSideService);
        services.put(ESide.GAUCHE, leftSideService);
        return services;
    }

    @Bean
    public ISystemBlockerManager systemBlockerManager() {
        return new SystemBlockerManager(IConstantesNerellConfig.seuilErreurPidDistance, IConstantesNerellConfig.seuilErreurPidOrientation);
    }
}
