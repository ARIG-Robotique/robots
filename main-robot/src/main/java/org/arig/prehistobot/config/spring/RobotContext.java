package org.arig.prehistobot.config.spring;

import org.arig.prehistobot.constants.ConstantesRobot;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePID;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
import org.arig.robot.system.RobotManager;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motion.OdometrieLineaire;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Position;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

/**
 * Created by gdepuille on 23/12/14.
 */
@Configuration
@Profile("raspi")
public class RobotContext {

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(ConstantesRobot.countPerMm, ConstantesRobot.countPerDeg);
    }

    @Bean
    public RobotManager robotManager() {
        return new RobotManager(1, 1, 95);
    }

    @Bean
    public IAsservissement asservissement() {
        AsservissementPolaire asserv = new AsservissementPolaire();
        asserv.setMinFenetreDistance(convertisseur().mmToPulse(1));
        asserv.setMinFenetreOrientation(convertisseur().degToPulse(0.1));
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
        SimplePID pid = new SimplePID();
        pid.setTunings(ConstantesRobot.kpDistance, ConstantesRobot.kiDistance, ConstantesRobot.kdDistance);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        SimplePID pid = new SimplePID();
        pid.setTunings(ConstantesRobot.kpOrientation, ConstantesRobot.kiOrientation, ConstantesRobot.kdOrientation);
        return pid;
    }

    @Bean(name = "rampDistance")
    public IRampFilter rampDistance() {
        return new Ramp(10, ConstantesRobot.rampAccDistance, ConstantesRobot.rampDecDistance);
    }

    @Bean(name = "rampOrientation")
    public IRampFilter rampOrientation() {
        return new Ramp(10, ConstantesRobot.rampAccOrientation, ConstantesRobot.rampDecOrientation);
    }
}
