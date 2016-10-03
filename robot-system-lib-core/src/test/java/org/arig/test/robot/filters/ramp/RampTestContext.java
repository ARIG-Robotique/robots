package org.arig.test.robot.filters.ramp;

import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
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
    public IRampFilter filter() {
        Ramp f = new Ramp();
        f.setRampAcc(1000);
        f.setRampDec(1000);
        return f;
    }
}
