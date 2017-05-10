package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.config.spring.NerellUtilsCheckIOsContext;
import org.arig.robot.services.IOService;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author gdepuille on 11/04/17.
 */
@Slf4j
public class CheckIOs {

    @SneakyThrows
    public static void main(String ... args) {
        log.info("Demarrage de Nerell en mode contrôle des IOs ...");

        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.register(NerellUtilsCheckIOsContext.class);
        rootContext.refresh();

        IOService ioService = rootContext.getBean(IOService.class);
        ioService.clearColorLedRGB();

        TCS34725ColorSensor colorSensor = rootContext.getBean(TCS34725ColorSensor.class);

        // Contrôle couleur
        for (int i = 0 ; i < 60 ; i++) {
            TCS34725ColorSensor.ColorData color = ioService.frontColor();
            log.info("Couleur R: {} ; G: {} ; B: {} : C: {} ; HEX: {}", color.r(), color.g(), color.b(), color.c(), color.hexColor());
            log.info("Couleur temp: {} K", colorSensor.calculateColorTemperature(color));
            log.info("Luminosité: {} lux", colorSensor.calculateLux(color));

            Thread.sleep(1000);
        }

        // TODO : Check PCF8574

        rootContext.close();
    }
}
