package org.arig.robot.system.process;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EcranProcess implements InitializingBean, DisposableBean {

  private Process p;
  private final String executablePath;
  @Getter
  private final String socketPath;
  private final int port;
  private final boolean debug;

  public EcranProcess(String executablePath, String socketPath) {
    this(executablePath, socketPath, false);
  }

  public EcranProcess(String executablePath, String socketPath, boolean debug) {
    this.executablePath = executablePath;
    this.socketPath = socketPath;
    this.debug = debug;

    this.port = -1;
  }

  public EcranProcess(String executablePath, int port) {
    this(executablePath, port, false);
  }

  public EcranProcess(String executablePath, int port, boolean debug) {
    this.executablePath = executablePath;
    this.port = port;
    this.debug = debug;

    this.socketPath = null;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (System.getProperty(ConstantesConfig.disableEcran) != null) {
      log.info("Le démarrage de l'écran est désactivé");
      return;
    }

    final File execDir = new File("/tmp/ecran");
    if (!execDir.exists()) {
      log.info("Création du répertoire d'execution pour le program de l'ecran {} : {}", execDir.getAbsolutePath(), execDir.mkdirs());
    }

    List<String> args = new ArrayList<>();
    args.add(executablePath);
    if (StringUtils.isNotBlank(socketPath)) {
      args.add("unix");
      args.add(socketPath);
    } else {
      args.add("inet");
      args.add(String.valueOf(port));
    }

    if (debug) {
      args.add("debug");
    }

    log.info("Lancement du process Ecran avec les paramètres : {}", StringUtils.join(args, " "));
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
    if (p != null) {
      p.destroyForcibly();
    }
  }
}
