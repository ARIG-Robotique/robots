package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.ServosService;
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
        rootContext.scan("org.arig.robot.config.spring");
        rootContext.refresh();

        IOService ioService = rootContext.getBean(IOService.class);
        EjectionModuleService ejectionModuleService = rootContext.getBean(EjectionModuleService.class);
        ServosService servosService = rootContext.getBean(ServosService.class);
        TCS34725ColorSensor colorSensor = rootContext.getBean(TCS34725ColorSensor.class);

        ioService.clearColorLedRGB();

        // Contrôle couleur
        /*
        for (int i = 0 ; i < 60 ; i++) {
            TCS34725ColorSensor.ColorData color = ioService.frontColor();
            log.info("Couleur R: {} ; G: {} ; B: {} : C: {} ; HEX: {}", color.r(), color.g(), color.b(), color.c(), color.hexColor());
            log.info("Couleur temp: {} K", colorSensor.calculateColorTemperature(color));
            log.info("Luminosité: {} lux", colorSensor.calculateLux(color));

            Thread.sleep(1000);
        }
        */

        // Check init et ejection module lunaire
        ioService.enableAlim12VPuissance();
        ioService.enableAlim5VPuissance();
        servosService.cyclePreparation();
        ejectionModuleService.init();
        ejectionModuleService.ejectionAvantRetourStand();


        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        rootContext.close();
    }
}
