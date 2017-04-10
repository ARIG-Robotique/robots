package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.RPLidarA2OverSocketTelemeter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.UUID;

/**
 * @author gdepuille on 09/04/17.
 */
@Slf4j
@Configuration
public class NerellUtilsCheckLidarContext {

    @Bean(destroyMethod = "destroyForcibly")
    public Process rplidarBridgeProcess() throws IOException {
        File logDir = new File("/var/log/rplidar_bridge");
        if (!logDir.exists()) {
            log.info("Création du répertoire de log pour RPLidar Bridge {} : {}", logDir.getAbsolutePath(), logDir.mkdirs());
        }
        String fileName = UUID.randomUUID().toString();
        File logFile = new File(logDir, fileName + ".log");
        File logErrorFile = new File(logDir, fileName + "-error.log");

        ProcessBuilder pb = new ProcessBuilder("/opt/rplidar_bridge", "unix");
        pb.directory(new File("/tmp/rplidar_bridge"));
        pb.redirectError(logErrorFile);
        pb.redirectOutput(logFile);

        return pb.start();
    }

    @Bean
    @DependsOn("rplidarBridgeProcess")
    public RPLidarA2OverSocketTelemeter rplidar() {
        final File socketFile = new File("/tmp/lidar.sock");
        return new RPLidarA2OverSocketTelemeter(socketFile);
    }
}
