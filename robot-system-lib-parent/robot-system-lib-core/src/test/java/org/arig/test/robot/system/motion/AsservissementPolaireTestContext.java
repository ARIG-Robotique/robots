package org.arig.test.robot.system.motion;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.RampFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.AsservissementPolaire;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * @author gdepuille on 19/03/15.
 */
@Slf4j
@Configuration
public class AsservissementPolaireTestContext {

    private static final int SAMPLE_TIME_MS = 10;

    private static final double KP = 0.1;
    private static final double KI = 0.1;
    private static final double KD = 0.1;

    @Autowired
    private Environment env;

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
    public IAsservissementPolaire asservissement() {
        return new AsservissementPolaire();
    }

    @Bean
    public CommandeRobot cmdRobot() {
        return new CommandeRobot();
    }

    @Bean(name = "pidDistance")
    public IPidFilter pidDistance() {
        log.info("Configuration PID Distance");
        CompletePidFilter pid = new CompletePidFilter("pid_distance");
        pid.setSampleTime(SAMPLE_TIME_MS);
        pid.setTunings(KP, KI, KD);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public IPidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        CompletePidFilter pid = new CompletePidFilter("pid_orientation");
        pid.setSampleTime(SAMPLE_TIME_MS);
        pid.setTunings(KP, KI, KD);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "pidMoteurDroit")
    public IPidFilter pidMoteurDroit() {
        log.info("Configuration PID moteur droit");
        CompletePidFilter pid = new CompletePidFilter("pid_mot_droit");
        pid.setSampleTime(SAMPLE_TIME_MS);
        pid.setTunings(KP, KI, KD);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "pidMoteurGauche")
    public IPidFilter pidMoteurGauche() {
        log.info("Configuration PID moteur gauche");
        CompletePidFilter pid = new CompletePidFilter("pid_mot_gauche");
        pid.setSampleTime(SAMPLE_TIME_MS);
        pid.setTunings(KP, KI, KD);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        return pid;
    }

    @Bean(name = "rampDistance")
    public IRampFilter rampDistance() {
        log.info("Configuration RampFilter Distance");
        return new RampFilter("ramp_distance", SAMPLE_TIME_MS, 50, 50);
    }

    @Bean(name = "rampOrientation")
    public IRampFilter rampOrientation() {
        log.info("Configuration RampFilter Orientation");
        return new RampFilter("ramp_orientation", SAMPLE_TIME_MS, 50, 50);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        String directory = String.format("%s%s%s", env.getRequiredProperty("java.io.tmpdir"), File.separator, "arig/robot/asservPolaireTest");
        return new MonitoringJsonWrapper(directory);
    }
}
