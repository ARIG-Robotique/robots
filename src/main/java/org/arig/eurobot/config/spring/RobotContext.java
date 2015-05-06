package org.arig.eurobot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.Ordonanceur;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.filters.pid.CompletePID;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Position;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
public class RobotContext {

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
        CompletePID pid = new CompletePID();
        pid.setSampleTime((int) IConstantesRobot.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(IConstantesRobot.kpDistance, IConstantesRobot.kiDistance, IConstantesRobot.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        CompletePID pid = new CompletePID();
        pid.setSampleTime((int) IConstantesRobot.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(IConstantesRobot.kpOrientation, IConstantesRobot.kiOrientation, IConstantesRobot.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public IRampFilter rampDistance() {
        log.info("Configuration Ramp Distance");
        return new Ramp(IConstantesRobot.asservTimeMs, IConstantesRobot.rampAccDistance, IConstantesRobot.rampDecDistance);
    }

    @Bean(name = "rampOrientation")
    public IRampFilter rampOrientation() {
        log.info("Configuration Ramp Orientation");
        return new Ramp(IConstantesRobot.asservTimeMs, IConstantesRobot.rampAccOrientation, IConstantesRobot.rampDecOrientation);
    }

    @Bean
    public IPathFinder pathFinder() {
        MultiPathFinderImpl pf = new MultiPathFinderImpl();
        pf.setAllowDiagonal(true);
        pf.setAlgorithm(IConstantesRobot.pathFindingAlgo);
        File pathDir = new File("./logs/paths");
        if (pathDir.exists()) {
            pathDir.delete();
        }
        pf.setPathDir(pathDir);
        //pf.setNbTileX(300);
        //pf.setNbTileY(200);

        // TODO : A supprimer
        pf.setNbTileX(118);
        pf.setNbTileY(180);

        return pf;
    }

    @Bean
    public RobotStatus robotStatus() { return new RobotStatus(); }

    @Bean
    public Ordonanceur ordonanceur() { return Ordonanceur.getInstance(); }

    @Bean
    public CsvCollector csvCollector() { return new CsvCollector(); }
}
