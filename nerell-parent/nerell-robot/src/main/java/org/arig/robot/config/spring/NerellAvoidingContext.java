package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.services.avoiding.BasicAvoidingService;
import org.arig.robot.services.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.RPLidarA2OverSocketTelemeter;
import org.arig.robot.system.process.RPLidarBridgeProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

/**
 * @author gdepuille on 23/12/14.
 */
@Slf4j
@Configuration
public class NerellAvoidingContext {

    @Autowired
    private Environment env;

    @Bean
    public RPLidarBridgeProcess rplidarBridgeProcess() throws IOException {
        return new RPLidarBridgeProcess("/opt/rplidar_bridge");
    }

    @Bean
    @DependsOn("rplidarBridgeProcess")
    public ILidarTelemeter rplidar() {
        final File socketFile = new File(RPLidarBridgeProcess.socketPath);
        return new RPLidarA2OverSocketTelemeter(socketFile);
    }

    @Bean
    public IAvoidingService avoidingService() {
        IConstantesNerellConfig.AvoidingSelection avoidingImplementation = env.getProperty("avoidance.service.implementation", IConstantesNerellConfig.AvoidingSelection.class);
        if (avoidingImplementation == IConstantesNerellConfig.AvoidingSelection.BASIC) {
            return new BasicAvoidingService();
        } else {
            return new CompleteAvoidingService();
        }
    }
}
