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
  private final String driver;
  private final String comFile;

  public LidarBridgeProcess(String executablePath, String driver) {
    this(executablePath, driver, null);
  }

  public LidarBridgeProcess(String executablePath, String driver, String comFile) {
    this.executablePath = executablePath;
    this.driver = driver;
    if (StringUtils.isBlank(comFile)) {
      this.comFile = "/dev/" + driver;
    } else {
      this.comFile = comFile;
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    final File execDir = new File("/tmp/lidar_bridge");
    if (!execDir.exists()) {
      log.info("Création du répertoire d'execution pour Lidar Bridge {} : {}", execDir.getAbsolutePath(), execDir.mkdirs());
    }

    List<String> args = new ArrayList<>();
    args.add(executablePath);
    args.add("unix");
    args.add(socketPath);
    args.add(driver); // Driver
    args.add(comFile); // Port

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
