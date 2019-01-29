package org.arig.robot;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.arig.robot.config.utils.spring.NerellUtilsCaptureCodeursContext;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 20/12/13.
 */
@Slf4j
public class CaptureCodeursValues {

    private Abstract2WheelsEncoders encoders;
    private AbstractPropulsionsMotors motors;
    private List<InfoCapture> infos = new ArrayList<>();

    public static void boot(final String [] args) throws Exception {
        log.info("Demarrage de Nerell en mode capture valeur codeurs ...");

        final AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(NerellUtilsCaptureCodeursContext.class);
        rootContext.refresh();

        CaptureCodeursValues rccv = new CaptureCodeursValues();
        rccv.encoders = rootContext.getBean(Abstract2WheelsEncoders.class);
        rccv.motors = rootContext.getBean(AbstractPropulsionsMotors.class);
        rccv.execute();

        rootContext.close();
        System.exit(0);
    }

    @SneakyThrows
    private void execute() {
        // Vitesse positive
        log.info("Reset codeurs");
        encoders.reset();
        for (int vitesse = motors.getStopSpeed() ; vitesse <= motors.getMaxSpeed() ; vitesse++) {
            captureForVitesse(vitesse);
        }

        motors.stopAll();
        Thread.sleep(5000);

        // Vitesse négative
        log.info("Reset codeurs");
        encoders.reset();
        for (int vitesse = motors.getStopSpeed() - 1 ; vitesse >= motors.getMinSpeed() ; vitesse--) {
            captureForVitesse(vitesse);
        }

        motors.stopAll();

        // Ecriture en CSV
        List<String> lines = infos.parallelStream()
                .map(i -> String.format("%s;%s;%s", i.getVitesse(), i.getGauche(), i.getDroit()))
                .collect(Collectors.toList());
        IOUtils.writeLines(lines, "\n", new FileOutputStream("capture.csv"), Charset.defaultCharset());
    }

    @SneakyThrows
    private void captureForVitesse(int vitesse) {
        log.info("Vitesse moteur {}", vitesse);
        motors.generateMouvement(vitesse, vitesse);
        for(int mesure = 0 ; mesure < 10 ; mesure++) {
            Thread.sleep(10);

            encoders.lectureValeurs();
            infos.add(new InfoCapture(vitesse, encoders.getGauche(), encoders.getDroit()));
        }
    }

    @Data
    public class InfoCapture {
        private final int vitesse;
        private final double gauche, droit;
    }
}
