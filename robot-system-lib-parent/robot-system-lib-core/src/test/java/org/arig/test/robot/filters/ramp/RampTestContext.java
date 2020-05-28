package org.arig.test.robot.filters.ramp;

import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
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
    public TrapezoidalRampFilter trapezoidalFilter1() {
        TrapezoidalRampFilter f = new TrapezoidalRampFilter("trapezoidal_ramp_test1");
        f.setRamps(1000, 1000);
        return f;
    }

    @Bean
    public TrapezoidalRampFilter trapezoidalFilter2() {
        TrapezoidalRampFilter f = new TrapezoidalRampFilter("trapezoidal_ramp_test2");
        f.setRamps(1000, 1000);
        return f;
    }
}
