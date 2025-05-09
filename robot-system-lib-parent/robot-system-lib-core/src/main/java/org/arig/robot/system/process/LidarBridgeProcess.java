package org.arig.robot.system.process;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper pour lancer le process externe lidar_bridge
 *
 * @author gdepuille on 11/04/17.
 */
@Slf4j
public class LidarBridgeProcess implements InitializingBean, DisposableBean {

    public static final String socketPath = "/tmp/lidar.sock";

    private Process p;
    private final String executablePath;
    private final boolean debug;

    public LidarBridgeProcess(String executablePath) {
        this(executablePath, false);
    }

    public LidarBridgeProcess(String executablePath, boolean debug) {
        this.executablePath = executablePath;
        this.debug = debug;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final File execDir = new File("/tmp/lidar_bridge");
        if (!execDir.exists()) {
            log.info("Création du répertoire d'execution pour RPLidar Bridge {} : {}", execDir.getAbsolutePath(), execDir.mkdirs());
        }

        List<String> args = new ArrayList<>();
        args.add(executablePath);
        args.add("unix");
        args.add(socketPath);
        args.add("rplidar"); // Driver

        File devDir = new File("/dev");
        File[] ttyUSBFiles = devDir.listFiles((dir, name) -> name.startsWith("ttyUSB"));
        if (ttyUSBFiles != null && ttyUSBFiles.length > 0) {
            log.info("Liste des descripteur de périphérique USB :");
            for (File file : ttyUSBFiles) {
                log.info(" - {}", file.getAbsolutePath());
            }
            args.add(ttyUSBFiles[0].getAbsolutePath());
        } else {
            throw new IllegalStateException("Aucun périphérique USB trouvé dans /dev");
        }

        log.info("Lancement du process Lidar Bridge avec les paramètres : {}", StringUtils.join(args, " "));
        ProcessBuilder pb = new ProcessBuilder(args.toArray(new String[args.size()]));
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
