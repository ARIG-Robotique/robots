package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.LidarTelemeterBouchon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
public class NerellSimulatorAvoidingContext {

    @Bean
    public ILidarTelemeter rplidar() {
        return new LidarTelemeterBouchon();
    }
}
