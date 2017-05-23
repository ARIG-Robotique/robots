package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author gdepuille on 11/04/17.
 */
@Slf4j
public class CheckCapteurCouleur {

    @SneakyThrows
    public static void main(String... args) {
        log.info("Demarrage de Nerell en mode contrôle du capteur couleur ...");

        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.scan("org.arig.robot.config.spring");
        rootContext.refresh();

        IOService ioService = rootContext.getBean(IOService.class);
        EjectionModuleService ejectionModuleService = rootContext.getBean(EjectionModuleService.class);
        ServosService servosService = rootContext.getBean(ServosService.class);
        RobotStatus robotStatus = rootContext.getBean(RobotStatus.class);

        robotStatus.setTeam(Team.BLEU);

        // Check init et ejection module lunaire
        ioService.enableAlim12VPuissance();
        ioService.enableAlim5VPuissance();
        servosService.cyclePreparation();
        ejectionModuleService.init();

        Deque<ModuleLunaire> modules = new LinkedList<>();
        modules.add(ModuleLunaire.polychrome());
        modules.add(ModuleLunaire.polychrome());
        modules.add(ModuleLunaire.polychrome());
        modules.add(ModuleLunaire.polychrome());
        modules.add(ModuleLunaire.polychrome());

        while (!modules.isEmpty()) {
            ejectionModuleService.ejectionModule(modules.poll());
        }

        //ioService.disableLedCapteurCouleur();

        ioService.disableAlim12VPuissance();
        ioService.disableAlim5VPuissance();

        rootContext.close();
    }
}
