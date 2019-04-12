package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.IRampFilter.RampType;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IRobotSide;
import org.arig.robot.services.LeftSideService;
import org.arig.robot.services.RightSideService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.system.TrajectoryManagerAsync;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import java.awt.*;
import java.util.HashMap;
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
    public TableUtils tableUtils() {
        TableUtils t = new TableUtils(IConstantesNerellConfig.minX, IConstantesNerellConfig.maxX,
                IConstantesNerellConfig.minY, IConstantesNerellConfig.maxY);

        // Ajout des zones de départ secondaire
        t.addDeadZone(new Rectangle.Double(0, 0, 710, 360)); // Jaune
        t.addDeadZone(new Rectangle.Double(2290, 0, 710, 360)); // Bleu

        // Ajout des fusées
        t.addDeadZone(new Rectangle.Double(0, 1250, 150, 200)); // Polychrome Jaune
        t.addDeadZone(new Rectangle.Double(1050, 0, 200, 150)); // Monochrome Jaune
        t.addDeadZone(new Rectangle.Double(2870, 1250, 150, 200)); // Polychrome Bleu
        t.addDeadZone(new Rectangle.Double(1750, 0, 200, 150)); // Monochrome Bleu

        return t;
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
    public IAsservissementPolaire asservissement() {
        return new AsservissementPolaire();
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
        return new TrapezoidalRampFilter("distance", RampType.LINEAR, IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccDistance, IConstantesNerellConfig.rampDecDistance);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", RampType.ANGULAR, IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccOrientation, IConstantesNerellConfig.rampDecOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        MultiPathFinderImpl pf = new MultiPathFinderImpl();

        pf.setAlgorithm(IConstantesNerellConfig.pathFindingAlgo);
        pf.setSaveImages(env.getProperty("pathfinding.save.images", Boolean.class, true));

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
        final Map<ESide, IRobotSide> services = new HashMap<>();
        services.put(ESide.DROITE, rightSideService);
        services.put(ESide.GAUCHE, leftSideService);
        return services;
    }
}
