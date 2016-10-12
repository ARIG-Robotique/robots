package org.arig.test.robot.filters.ramp;

import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.RampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.InfluxDbWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gdepuille on 15/03/15.
 */
@Configuration
public class RampTestContext {

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(10, 10);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        InfluxDbWrapper w = new InfluxDbWrapper();
        w.setUrl("http://localhost:8086");
        w.setUsername("xx");
        w.setPassword("xx");
        w.setDbName("tua");
        w.setRetentionPolicy("autogen");

        return w;
    }

    @Bean
    public IRampFilter filter() {
        RampFilter f = new RampFilter("ramp_test");
        f.setRampAcc(1000);
        f.setRampDec(1000);
        return f;
    }
}
