package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesNerellConfig.AsservPolaireSelection;
import org.arig.robot.constants.IConstantesServos;
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
import org.arig.robot.system.encoders.AbstractEncoder;
import org.arig.robot.system.motion.AsservissementPolaireDistanceOrientation;
import org.arig.robot.system.motion.AsservissementPolaireMoteurs;
import org.arig.robot.system.motion.AsservissementPosition;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.PropulsionsSD21Motors;
import org.arig.robot.system.motors.SD21Motor;
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
        TableUtils t = new TableUtils(IConstantesNerellConfig.minX, IConstantesNerellConfig.maxX,
                IConstantesNerellConfig.minY, IConstantesNerellConfig.maxY);

        // Ajout des zones de départ secondaire
//        t.addDeadZone(new Rectangle.Double(0, 0, 710, 360)); // Jaune
//        t.addDeadZone(new Rectangle.Double(2290, 0, 710, 360)); // Bleu

        // Ajout des fusées
//        t.addDeadZone(new Rectangle.Double(0, 1250, 150, 200)); // Polychrome Jaune
//        t.addDeadZone(new Rectangle.Double(1050, 0, 200, 150)); // Monochrome Jaune
//        t.addDeadZone(new Rectangle.Double(2870, 1250, 150, 200)); // Polychrome Bleu
//        t.addDeadZone(new Rectangle.Double(1750, 0, 200, 150)); // Monochrome Bleu

        return t;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final PropulsionsSD21Motors motors = new PropulsionsSD21Motors(IConstantesServos.MOTOR_DROIT, IConstantesServos.MOTOR_GAUCHE);
        motors.assignMotors(IConstantesNerellConfig.numeroMoteurGauche, IConstantesNerellConfig.numeroMoteurDroit);
        return motors;
    }

    @Bean
    public AbstractMotor motorCarousel() {
        return new SD21Motor(IConstantesServos.MOTOR_CAROUSEL);
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
        IConstantesNerellConfig.AsservPolaireSelection asservImplementation = env.getProperty("robot.asservissement.polaire.implementation", IConstantesNerellConfig.AsservPolaireSelection.class);
        if (asservImplementation == AsservPolaireSelection.DISTANCE_ORIENTATION) {
            return new AsservissementPolaireDistanceOrientation();
        } else {
            return new AsservissementPolaireMoteurs();
        }
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
    public IPidFilter pidDistance(AbstractPropulsionsMotors motors) {
        log.info("Configuration PID Distance");
        SimplePidFilter pid = new SimplePidFilter("distance", motors.getMinSpeed(), motors.getMaxSpeed());
        pid.setTunings(IConstantesNerellConfig.kpDistance, IConstantesNerellConfig.kiDistance, IConstantesNerellConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation(AbstractPropulsionsMotors motors) {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation", motors.getMinSpeed(), motors.getMaxSpeed());
        pid.setTunings(IConstantesNerellConfig.kpOrientation, IConstantesNerellConfig.kiOrientation, IConstantesNerellConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "pidMoteurDroit")
    public IPidFilter pidMoteurDroit(AbstractPropulsionsMotors motors) {
        log.info("Configuration PID moteur droit");
        SimplePidFilter pid = new SimplePidFilter("pid_mot_droit", motors.getMinSpeed(), motors.getMaxSpeed());
        pid.setTunings(IConstantesNerellConfig.kpMotDroit, IConstantesNerellConfig.kiMotDroit, IConstantesNerellConfig.kdMotDroit);
        return pid;
    }

    @Bean(name = "pidMoteurGauche")
    public IPidFilter pidMoteurGauche(AbstractPropulsionsMotors motors) {
        log.info("Configuration PID moteur gauche");
        SimplePidFilter pid = new SimplePidFilter("pid_mot_gauche", motors.getMinSpeed(), motors.getMaxSpeed());
        pid.setTunings(IConstantesNerellConfig.kpMotGauche, IConstantesNerellConfig.kiMotGauche, IConstantesNerellConfig.kdMotGauche);
        return pid;
    }

    @Bean(name = "pidCarousel")
    public IPidFilter pidCarousel(AbstractMotor motor) {
        log.info("Configuration PID Carousel");
        SimplePidFilter pid = new SimplePidFilter("carousel", motor.getMinSpeed(), motor.getMaxSpeed());
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
        return new TrapezoidalRampFilter("orientation", IConstantesNerellConfig.asservTimeCarouselMs, IConstantesNerellConfig.rampAccCarousel, IConstantesNerellConfig.rampDecCarousel);
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
}
