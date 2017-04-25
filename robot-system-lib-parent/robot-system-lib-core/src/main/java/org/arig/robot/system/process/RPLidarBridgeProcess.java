package org.arig.robot.system.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;

/**
 * Wrapper pour lancer le process externe rplidar_bridge
 *
 * @author gdepuille on 11/04/17.
 */
@Slf4j
public class RPLidarBridgeProcess implements InitializingBean, DisposableBean {

    public static final String socketPath = "/tmp/lidar.sock";

    private Process p;
    private final String executablePath;

    public RPLidarBridgeProcess(String executablePath) {
        this.executablePath = executablePath;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final File execDir = new File("/tmp/rplidar_bridge");
        if (!execDir.exists()) {
            log.info("Création du répertoire d'execution pour RPLidar Bridge {} : {}", execDir.getAbsolutePath(), execDir.mkdirs());
        }

        ProcessBuilder pb = new ProcessBuilder(executablePath, "unix", socketPath);
        pb.directory(execDir);

        p = pb.start();

        StreamGobbler out = new StreamGobbler(p.getInputStream(), log::info);
        StreamGobbler err = new StreamGobbler(p.getErrorStream(), log::error);
        new Thread(out).start();
        new Thread(err).start();
    }

    @Override
    public void destroy() throws Exception {
        p.destroyForcibly();
    }
}