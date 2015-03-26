package org.arig.test.robot.system.motion;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.CompletePID;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gdepuille on 19/03/15.
 */
@Slf4j
@Configuration
public class AsservissementPolaireTestContext {

    private static final int SAMPLE_TIME_MS = 10;

    private static final double KP = 0.1;
    private static final double KI = 0.1;
    private static final double KD = 0.1;

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(1, 1);
    }

    @Bean
    public Abstract2WheelsEncoders encoders() {
        Abstract2WheelsEncoders encoders = Mockito.mock(Abstract2WheelsEncoders.class);
        Mockito.when(encoders.getDistance()).thenReturn(1.0);
        Mockito.when(encoders.getOrientation()).thenReturn(0.0);

        return encoders;
    }

    @Bean
    public IAsservissementPolaire asservissement(ConvertionRobotUnit convertisseur) {
        AsservissementPolaire asserv = new AsservissementPolaire();
        asserv.setMinFenetreDistance(convertisseur.mmToPulse(1));
        asserv.setMinFenetreOrientation(convertisseur.degToPulse(0.1));
        return asserv;
    }

    @Bean
    public CommandeRobot cmdRobot() {
        return new CommandeRobot();
    }

    @Bean(name = "pidDistance")
    public IPidFilter pidDistance() {
        log.info("Configuration PID Distance");
        CompletePID pid = new CompletePID();
        pid.setSampleTime(SAMPLE_TIME_MS);
        pid.setTunings(KP, KI, KD);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        CompletePID pid = new CompletePID();
        pid.setSampleTime(SAMPLE_TIME_MS);
        pid.setTunings(KP, KI, KD);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "rampDistance")
    public IRampFilter rampDistance() {
        log.info("Configuration Ramp Distance");
        return new Ramp(SAMPLE_TIME_MS, 50, 50);
    }

    @Bean(name = "rampOrientation")
    public IRampFilter rampOrientation() {
        log.info("Configuration Ramp Orientation");
        return new Ramp(SAMPLE_TIME_MS, 50, 50);
    }
}
