package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.CompletePidFilter;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringInfluxDBWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author gdepuille on 30/10/16.
 */
@Configuration
@Import({ I2CContext.class })
public class ReglagePIDContext {

    private double kpMotG = 1.0;
    private double kiMotG = 0.0;
    private double kdMotG = 0.0;

    private double kpMotD = 0.1;
    private double kiMotD = 0.0;
    private double kdMotD = 0.0;

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
    public IPidFilter pidMoteurGauche() {
        CompletePidFilter pid = new CompletePidFilter("pid_mot_g");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(kpMotG, kiMotG, kdMotG);
        return pid;
    }

    @Bean(name = "pidMoteurDroit")
    public IPidFilter pidMoteurDroit() {
        CompletePidFilter pid = new CompletePidFilter("pid_mot_d");
        pid.setSampleTime((int) IConstantesNerellConfig.asservTimeMs);
        pid.setMode(IPidFilter.PidMode.AUTOMATIC);

        pid.setTunings(kpMotD, kiMotD, kdMotD);
        return pid;
    }
}
