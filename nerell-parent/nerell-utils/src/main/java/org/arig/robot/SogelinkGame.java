package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
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
public class SogelinkGame {

    @SneakyThrows
    public static void main(String... args) {
        log.info("Demarrage de Nerell en mode Sogelink Game ...");

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
        servosService.homes();
        servosService.aspirationFerme();

        ejectionModuleService.ejectionAvantRetourStand();

        log.info("Activation des tâches planifé");
        rs.enablePinces();
        rs.enableMatch();

        boolean finish;
        do {
            if (!rs.hasModuleLunaireExpected()) {
                // Le jeux est valable pour trois polychrome
                rs.addModuleLunaireExpected(ModuleLunaire.polychrome());
                rs.addModuleLunaireExpected(ModuleLunaire.polychrome());
                rs.addModuleLunaireExpected(ModuleLunaire.polychrome());
            }

            finish = ioService.presenceBaseLunaireDroite() && ioService.presenceBaseLunaireGauche()
                        && ioService.bordureArriereDroite() && ioService.bordureArriereGauche();
            brasService.sleep(200);

            if (finish) {
                log.info("Fin du programme ;-)");
            }
        } while (!finish);

        ejectionModuleService.ejectionAvantRetourStand();

        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        rootContext.close();
    }
}
