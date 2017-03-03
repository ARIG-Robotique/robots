package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringInfluxDBWrapper;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
@Import({ NerellUtilsI2CContext.class })
public class NerellUtilsReglagePIDContext {

    private double kpMotG = 0.08;
    private double kiMotG = 30.0;
    private double kdMotG = 0.008;

    private double kpMotD = 0.08;
    private double kiMotD = 30.0;
    private double kdMotD = 0.008;

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        MonitoringInfluxDBWrapper w = new MonitoringInfluxDBWrapper();
        w.setUrl("http://sglk-dxf5xy1-lnx:8086");
        w.setUsername("root");
        w.setPassword("root");
        w.setDbName("nerell_utils");
        w.setRetentionPolicy("autogen");

        return w;
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
