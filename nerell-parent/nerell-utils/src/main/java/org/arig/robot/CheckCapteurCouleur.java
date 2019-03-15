package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.ServosService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author gdepuille on 11/04/17.
 */
@Slf4j
public class CheckCapteurCouleur {

    @SneakyThrows
    public static void boot(String... args) {
        log.info("Demarrage de Nerell en mode contrôle du capteur couleur ...");

        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.scan("org.arig.robot.config.spring");
        rootContext.refresh();

        IOService ioService = rootContext.getBean(IOService.class);
        EjectionModuleService ejectionModuleService = rootContext.getBean(EjectionModuleService.class);
        ServosService servosService = rootContext.getBean(ServosService.class);
        RobotStatus robotStatus = rootContext.getBean(RobotStatus.class);

        robotStatus.setTeam(Team.VIOLET);

        // Check init et ejection module lunaire
        ioService.enableAlim12VPuissance();
        ioService.enableAlim5VPuissance();
        servosService.cyclePreparation();
        ejectionModuleService.init();

        robotStatus.addModuleDansMagasin(ModuleLunaire.polychrome());
        robotStatus.addModuleDansMagasin(ModuleLunaire.polychrome());
        robotStatus.addModuleDansMagasin(ModuleLunaire.polychrome());
        robotStatus.addModuleDansMagasin(ModuleLunaire.polychrome());
        robotStatus.addModuleDansMagasin(ModuleLunaire.polychrome());

        while (robotStatus.hasModuleDansMagasin()) {
            ejectionModuleService.ejectionModule();
        }

        //ioService.disableLedCapteurCouleur();

        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        rootContext.close();
    }
}
