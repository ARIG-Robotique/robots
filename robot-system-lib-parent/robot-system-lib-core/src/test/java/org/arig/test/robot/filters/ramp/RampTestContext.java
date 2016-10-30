package org.arig.test.robot.filters.ramp;

import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.RampFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.FileSystemUtils;

import java.io.File;

/**
 * @author gdepuille on 15/03/15.
 */
@Configuration
public class RampTestContext {

    @Autowired
    private Environment env;

    @Bean
    public ConvertionRobotUnit convertisseur() {
        return new ConvertionRobotUnit(10, 10);
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper() {
        String directory = String.format("%s%s%s", env.getRequiredProperty("java.io.tmpdir"), File.separator, "arig/robot/rampTest");
        return new MonitoringJsonWrapper(directory);
    }

    @Bean
    public IRampFilter filter() {
        RampFilter f = new RampFilter("ramp_test");
        f.setRampAcc(1000);
        f.setRampDec(1000);
        return f;
    }
}
