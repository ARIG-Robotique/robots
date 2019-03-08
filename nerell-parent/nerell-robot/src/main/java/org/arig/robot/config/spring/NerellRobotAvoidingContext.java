package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesI2CAdc;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.services.avoiding.BasicAvoidingService;
import org.arig.robot.services.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.*;
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
public class NerellRobotAvoidingContext {

    @Autowired
    private Environment env;

    @Bean
    public RPLidarBridgeProcess rplidarBridgeProcess() throws IOException {
        return new RPLidarBridgeProcess("/opt/rplidar_bridge");
    }

    @Bean
    @DependsOn("rplidarBridgeProcess")
    public ILidarTelemeter rplidar() throws Exception {
        final File socketFile = new File(RPLidarBridgeProcess.socketPath);
        return new RPLidarA2OverSocketTelemeter(socketFile);
    }

    @Bean
    public IVisionBalise visionBalise() throws Exception {
        final String host = env.getRequiredProperty("balise.socket.host");
        final Integer port = env.getRequiredProperty("balise.socket.port", Integer.class);
        return new VisionBaliseOverSocket(host, port);
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

    @Bean(name = "gp2dGauche")
    public GP2D12 gp2dGauche() {
        return new GP2D12(IConstantesI2CAdc.GP2D_AVANT_GAUCHE, "GP2D Gauche");
    }

    @Bean(name = "gp2dCentre")
    public GP2D12 gp2dCentre() {
        return new GP2D12(IConstantesI2CAdc.GP2D_AVANT_CENTRE, "GP2D Centre");
    }

    @Bean(name = "gp2dDroit")
    public GP2D12 gp2dDroit() {
        return new GP2D12(IConstantesI2CAdc.GP2D_AVANT_DROIT, "GP2D Droit");
    }

    @Bean(name = "usLatGauche")
    public SRF02Sonar usLatGauche() {
        return new SRF02Sonar(IConstantesI2C.US_LAT_GAUCHE_NAME);
    }

    @Bean(name = "usGauche")
    public SRF02Sonar usGauche() {
        return new SRF02Sonar(IConstantesI2C.US_GAUCHE_NAME);
    }

    /*@Bean(name = "usDroit")
    public SRF02Sonar usDroit() {
        return new SRF02Sonar(IConstantesI2C.US_DROIT_NAME);
    }*/

    @Bean(name = "usLatDroit")
    public SRF02Sonar usLatDroit() {
        return new SRF02Sonar(IConstantesI2C.US_LAT_DROIT_NAME);
    }
}
