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
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
@PropertySource({"classpath:application.properties"})
public class RobotContext {

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper("logs/timeDatas");
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesNerellConfig.countPerMm, IConstantesNerellConfig.countPerDeg);
    }

    @Bean
    public MouvementManager mouvementManager() {
        MouvementManager mv = new MouvementManager(IConstantesNerellConfig.arretDistanceMm, IConstantesNerellConfig.approcheDistanceMm,
                IConstantesNerellConfig.arretOrientDeg, IConstantesNerellConfig.approcheOrientationDeg,
                IConstantesNerellConfig.angleReculDeg);
        mv.setDistanceMiniEntrePointMm(IConstantesNerellConfig.distanceMiniEntrePointMm);
        mv.setDistanceChangementVitesse(IConstantesNerellConfig.distanceChangementVitesse);
        mv.setVitesseLente(IConstantesNerellConfig.vitesseLente);
        return mv;
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
        pf.setAllowDiagonal(true);
        pf.setAlgorithm(IConstantesNerellConfig.pathFindingAlgo);
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
