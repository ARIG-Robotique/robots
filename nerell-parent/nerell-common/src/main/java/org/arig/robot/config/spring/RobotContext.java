package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.RampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.system.TrajectoryManagerAsync;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.awt.*;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
@PropertySource({"classpath:application.properties"})
public class RobotContext {

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
    public ITrajectoryManager mouvementManager() {
        return new TrajectoryManager(IConstantesNerellConfig.arretDistanceMm, IConstantesNerellConfig.approcheDistanceMm,
                IConstantesNerellConfig.arretOrientDeg, IConstantesNerellConfig.approcheOrientationDeg,
                IConstantesNerellConfig.angleReculDeg);
    }

    @Bean(value = "mouvementManagerAsync")
    public ITrajectoryManager mouvementManagerAsync() {
        return new TrajectoryManagerAsync(mouvementManager());
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
        CompletePidFilter pid = new CompletePidFilter("pid_distance");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(IConstantesNerellConfig.kpDistance, IConstantesNerellConfig.kiDistance, IConstantesNerellConfig.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        CompletePidFilter pid = new CompletePidFilter("pid_orientation");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(IConstantesNerellConfig.kpOrientation, IConstantesNerellConfig.kiOrientation, IConstantesNerellConfig.kdOrientation);
        return pid;
    }

    @Bean(name = "pidMoteurDroit")
    public IPidFilter pidMoteurDroit(AbstractPropulsionsMotors motors) {
        log.info("Configuration PID moteur droit");
        CompletePidFilter pid = new CompletePidFilter("pid_mot_droit");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setOutputLimits(motors.getMinSpeed(), motors.getMaxSpeed());

        pid.setTunings(IConstantesNerellConfig.kpMotDroit, IConstantesNerellConfig.kiMotDroit, IConstantesNerellConfig.kdMotDroit);
        return pid;
    }

    @Bean(name = "pidMoteurGauche")
    public IPidFilter pidMoteurGauche(AbstractPropulsionsMotors motors) {
        log.info("Configuration PID moteur gauche");
        CompletePidFilter pid = new CompletePidFilter("pid_mot_gauche");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setOutputLimits(motors.getMinSpeed(), motors.getMaxSpeed());

        pid.setTunings(IConstantesNerellConfig.kpMotGauche, IConstantesNerellConfig.kiMotGauche, IConstantesNerellConfig.kdMotGauche);
        return pid;
    }

    @Bean(name = "rampDistance")
    public IRampFilter rampDistance() {
        log.info("Configuration RampFilter Distance");
        return new RampFilter("ramp_distance", IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccDistance, IConstantesNerellConfig.rampDecDistance);
    }

    @Bean(name = "rampOrientation")
    public IRampFilter rampOrientation() {
        log.info("Configuration RampFilter Orientation");
        return new RampFilter("ramp_orientation", IConstantesNerellConfig.asservTimeMs, IConstantesNerellConfig.rampAccOrientation, IConstantesNerellConfig.rampDecOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setMaxDistanceDepart(1.0);
        pf.setMaxDistanceArrivee(1.0);
        pf.setAllowDiagonal(true);
        pf.setAlgorithm(IConstantesNerellConfig.pathFindingAlgo);
        pf.setNbTileX(300);
        pf.setNbTileY(200);
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
}
