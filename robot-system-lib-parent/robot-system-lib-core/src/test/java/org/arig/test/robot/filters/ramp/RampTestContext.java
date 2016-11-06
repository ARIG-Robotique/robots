package org.arig.test.robot.filters.ramp;

import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.RampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 15/03/15.
 */
@Configuration
public class RampTestContext {

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(10, 10);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        return new MonitoringJsonWrapper();
    }

    @Bean
    public IRampFilter filter1() {
        RampFilter f = new RampFilter("ramp_test1");
        f.setRampAcc(1000);
        f.setRampDec(1000);
        return f;
    }

    @Bean
    public IRampFilter filter2() {
        RampFilter f = new RampFilter("ramp_test2");
        f.setRampAcc(1000);
        f.setRampDec(1000);
        return f;
    }
}
