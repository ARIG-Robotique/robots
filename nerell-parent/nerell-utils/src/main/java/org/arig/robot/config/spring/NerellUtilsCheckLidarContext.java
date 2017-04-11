package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.RPLidarA2OverSocketTelemeter;
import org.arig.robot.system.process.RPLidarBridgeProcess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.File;
import java.io.IOException;

/**
 * @author gdepuille on 09/04/17.
 */
@Slf4j
@Configuration
public class NerellUtilsCheckLidarContext {

    @Bean
    public RPLidarBridgeProcess rplidarBridgeProcess() throws IOException {
        return new RPLidarBridgeProcess("/opt/rplidar_bridge");
    }

    @Bean
    @DependsOn("rplidarBridgeProcess")
    public RPLidarA2OverSocketTelemeter rplidar() {
        final File socketFile = new File(RPLidarBridgeProcess.socketPath);
        return new RPLidarA2OverSocketTelemeter(socketFile);
    }
}
