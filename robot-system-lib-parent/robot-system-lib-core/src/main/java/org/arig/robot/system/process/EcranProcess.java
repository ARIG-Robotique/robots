package org.arig.robot.system.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EcranProcess implements InitializingBean, DisposableBean {

    public static final String socketPath = "/tmp/ecran.sock";

    private Process p;
    private final String executablePath;
    private final boolean debug;

    public EcranProcess(String executablePath) {
        this(executablePath, false);
    }

    public EcranProcess(String executablePath, boolean debug) {
        this.executablePath = executablePath;
        this.debug = debug;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final File execDir = new File("/tmp/ecran");
        if (!execDir.exists()) {
            log.info("Création du répertoire d'execution pour le program de l'ecran {} : {}", execDir.getAbsolutePath(), execDir.mkdirs());
        }

        List<String> args = new ArrayList<>();
        args.add(executablePath);
        args.add("unix");
        args.add(socketPath);

        // TODO : External config
        args.add("sauron");
        args.add("9042");
        if (debug) {
            args.add("debug");
        }

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
