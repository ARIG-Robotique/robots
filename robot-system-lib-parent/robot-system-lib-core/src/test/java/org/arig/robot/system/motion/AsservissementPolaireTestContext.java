package org.arig.robot.system.motion;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.TestRobotStatus;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public AbstractRobotStatus robotStatus() {
        return new TestRobotStatus();
    }

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
        return new AsservissementPolaireDistanceOrientation();
    }

    @Bean
    public CommandeRobot cmdRobot() {
        return new CommandeRobot();
    }

    @Bean(name = "pidDistance")
    public PidFilter pidDistance() {
        log.info("Configuration PID Distance");
        SimplePidFilter pid = new SimplePidFilter("distance");
        pid.setTunings(KP, KI, KD);
        return pid;
    }

    @Bean(name = "pidOrientation")
    public PidFilter pidOrientation() {
        log.info("Configuration PID Orientation");
        SimplePidFilter pid = new SimplePidFilter("orientation");
        pid.setTunings(KP, KI, KD);
        return pid;
    }

    @Bean(name = "pidMoteurDroit")
    public PidFilter pidMoteurDroit() {
        log.info("Configuration PID moteur droit");
        SimplePidFilter pid = new SimplePidFilter("pid_mot_droit");
        pid.setTunings(KP, KI, KD);
        return pid;
    }

    @Bean(name = "pidMoteurGauche")
    public PidFilter pidMoteurGauche() {
        log.info("Configuration PID moteur gauche");
        SimplePidFilter pid = new SimplePidFilter("pid_mot_gauche");
        pid.setTunings(KP, KI, KD);
        return pid;
    }

    @Bean(name = "rampDistance")
    public TrapezoidalRampFilter rampDistance() {
        log.info("Configuration TrapezoidalRampFilter Distance");
        return new TrapezoidalRampFilter("distance", SAMPLE_TIME_MS, 50, 50);
    }

    @Bean(name = "rampOrientation")
    public TrapezoidalRampFilter rampOrientation() {
        log.info("Configuration TrapezoidalRampFilter Orientation");
        return new TrapezoidalRampFilter("orientation", SAMPLE_TIME_MS, 50, 50);
    }

    @Bean
    public MonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }
}
