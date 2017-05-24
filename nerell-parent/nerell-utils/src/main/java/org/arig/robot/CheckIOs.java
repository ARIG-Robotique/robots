package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.ServosService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author gdepuille on 11/04/17.
 */
@Slf4j
public class CheckIOs {

    @SneakyThrows
    public static void main(String... args) {
        log.info("Demarrage de Nerell en mode contrôle des IOs ...");

        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.scan("org.arig.robot.config.spring");
        rootContext.refresh();

        IOService ioService = rootContext.getBean(IOService.class);
        RobotStatus rs = rootContext.getBean(RobotStatus.class);
        EjectionModuleService ejectionModuleService = rootContext.getBean(EjectionModuleService.class);
        BrasService brasService = rootContext.getBean(BrasService.class);
        ServosService servosService = rootContext.getBean(ServosService.class);

        ioService.clearColorLedRGB();

        // Check init et ejection module lunaire
        ioService.enableAlim12VPuissance();
        ioService.enableAlim5VPuissance();
        servosService.cyclePreparation();
        ejectionModuleService.init();
        servosService.homes();

        do {
            brasService.stockerModuleRobot();
            brasService.sleep(2000);
        } while (rs.canAddModuleMagasin());

        ejectionModuleService.ejectionAvantRetourStand();

        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        rootContext.close();
    }
}
