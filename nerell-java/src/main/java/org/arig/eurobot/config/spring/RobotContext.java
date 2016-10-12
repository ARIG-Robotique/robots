package org.arig.eurobot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.Ordonanceur;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.avoiding.BasicAvoidingService;
import org.arig.eurobot.services.avoiding.CompleteAvoidingService;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.RampFilter;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
@PropertySource({"file:./application.properties"})
public class RobotContext {

    @Autowired
    private Environment env;

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesRobot.countPerMm, IConstantesRobot.countPerDeg);
    }

    @Bean
    public MouvementManager mouvementManager() {
        MouvementManager mv = new MouvementManager(IConstantesRobot.arretDistanceMm, IConstantesRobot.approcheDistanceMm,
                IConstantesRobot.arretOrientDeg, IConstantesRobot.approcheOrientationDeg,
                IConstantesRobot.angleReculDeg);
        mv.setDistanceMiniEntrePointMm(IConstantesRobot.distanceMiniEntrePointMm);
        mv.setDistanceChangementVitesse(IConstantesRobot.distanceChangementVitesse);
        mv.setVitesseLente(IConstantesRobot.vitesseLente);
        return mv;
    }

    @Bean
    public IAvoidingService avoidingService() {
        IConstantesRobot.AvoidingSelection avoidingImplementation = env.getProperty("avoidance.service.implementation", IConstantesRobot.AvoidingSelection.class);
        if (avoidingImplementation == IConstantesRobot.AvoidingSelection.BASIC) {
            return new BasicAvoidingService();
        } else {
            return new CompleteAvoidingService();
        }
    }

    @Bean
    public IAsservissementPolaire asservissement() { return new AsservissementPolaire(); }

    @Bean
    public IOdometrie odometrie() { return new OdometrieLineaire(); }

    @Bean
    public CommandeRobot cmdRobot() { return new CommandeRobot(); }

    @Bean(name = "currentPosition")
    public Position currentPosition() { return new Position(); }

    @Bean(name = "pidDistance")
    public IPidFilter pidDistance() {
        log.info("Configuration PID Distance");
        CompletePidFilter pid = new CompletePidFilter("pid_distance");
        pid.setSampleTime((int) IConstantesRobot.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(IConstantesRobot.kpDistance, IConstantesRobot.kiDistance, IConstantesRobot.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        CompletePidFilter pid = new CompletePidFilter("pid_orientation");
        pid.setSampleTime((int) IConstantesRobot.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(IConstantesRobot.kpOrientation, IConstantesRobot.kiOrientation, IConstantesRobot.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public IRampFilter rampDistance() {
        log.info("Configuration RampFilter Distance");
        return new RampFilter("ramp_distance", IConstantesRobot.asservTimeMs, IConstantesRobot.rampAccDistance, IConstantesRobot.rampDecDistance);
    }

    @Bean(name = "rampOrientation")
    public IRampFilter rampOrientation() {
        log.info("Configuration RampFilter Orientation");
        return new RampFilter("ramp_orientation", IConstantesRobot.asservTimeMs, IConstantesRobot.rampAccOrientation, IConstantesRobot.rampDecOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setAllowDiagonal(true);
        pf.setAlgorithm(IConstantesRobot.pathFindingAlgo);
        File pathDir = new File("./logs/paths");
        pf.setPathDir(pathDir);
        pf.setNbTileX(200);
        pf.setNbTileY(300);

        return pf;
    }

    @Bean
    public RobotStatus robotStatus() { return new RobotStatus(); }

    @Bean
    public Ordonanceur ordonanceur() { return Ordonanceur.getInstance(); }
}
