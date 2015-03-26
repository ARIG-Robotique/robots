package org.arig.prehistobot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.prehistobot.Ordonanceur;
import org.arig.prehistobot.constants.IConstantesRobot;
import org.arig.prehistobot.model.RobotStatus;
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
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Position;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
@Profile("raspi")
public class RobotContext {

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesRobot.countPerMm, IConstantesRobot.countPerDeg);
    }

    @Bean
    public MouvementManager mouvementManager() {
        return new MouvementManager(IConstantesRobot.arretDistanceMm, IConstantesRobot.arretOrientDeg, IConstantesRobot.angleReculDeg);
    }

    @Bean
    public IAsservissementPolaire asservissement(ConvertionRobotUnit convertisseur) {
        AsservissementPolaire asserv = new AsservissementPolaire();
        asserv.setMinFenetreDistance(convertisseur.mmToPulse(1));
        asserv.setMinFenetreOrientation(convertisseur.degToPulse(0.1));
        return asserv;
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
        CompletePID pid = new CompletePID();
        pid.setSampleTime((int) IConstantesRobot.asservTimeMs);
        pid.setTunings(IConstantesRobot.kpDistance, IConstantesRobot.kiDistance, IConstantesRobot.kdDistance);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        CompletePID pid = new CompletePID();
        pid.setSampleTime((int) IConstantesRobot.asservTimeMs);
        pid.setTunings(IConstantesRobot.kpOrientation, IConstantesRobot.kiOrientation, IConstantesRobot.kdOrientation);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
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
    public RobotStatus robotStatus() {
        return new RobotStatus();
    }

    @Bean
    public Ordonanceur ordonenceur() {
        return Ordonanceur.getInstance();
    }

    @Bean
    public CsvCollector csvCollector() {
        return new CsvCollector();
    }
}
