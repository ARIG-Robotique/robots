package org.arig.robot.config.utils.spring;

import org.arig.robot.config.spring.NerellI2CContext;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
@Import({ NerellI2CContext.class })
public class NerellUtilsReglagePIDContext {

    private double kpMotG = 0.9;
    private double kiMotG = 12.0;
    private double kdMotG = 0.0009;

    private double kpMotD = 0.9;
    private double kiMotD = 12.0;
    private double kdMotD = 0.0009;

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(IConstantesNerellConfig.countPerMm, IConstantesNerellConfig.countPerDeg);
    }

    @Bean(name = "pidMoteurGauche")
    public IPidFilter pidMoteurGauche(AbstractPropulsionsMotors motors) {
        CompletePidFilter pid = new CompletePidFilter("pid_mot_g");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setOutputLimits(motors.getMinSpeed(), motors.getMaxSpeed());

        pid.setTunings(kpMotG, kiMotG, kdMotG);
        return pid;
    }

    @Bean(name = "pidMoteurDroit")
    public IPidFilter pidMoteurDroit(AbstractPropulsionsMotors motors) {
        CompletePidFilter pid = new CompletePidFilter("pid_mot_d");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);
        pid.setOutputLimits(motors.getMinSpeed(), motors.getMaxSpeed());

        pid.setTunings(kpMotD, kiMotD, kdMotD);
        return pid;
    }
}