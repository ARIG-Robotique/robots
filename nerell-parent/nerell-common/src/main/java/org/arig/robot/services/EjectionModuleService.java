package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 22/05/17.
 */
@Slf4j
@Service
public class EjectionModuleService {

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    public void init() {
        log.info("Initialisation de l'ejection des modules");
        if (ioService.glissiereFerme()) {
            servosService.ouvreGlissiere();
            while(!ioService.finCourseGlissiereDroite());
            servosService.stopGlissiere();
        }

        // Dans une position ouverte quelque part
        servosService.fermeGlissiere();
        while(ioService.finCourseGlissiereDroite());
        servosService.stopGlissiere();
    }

    public void ejectionAvantRetourStand() {
        if (ioService.presenceRouleaux()) {
            ejectionModule(ModuleLunaire.monochrome());
        }

        while(ioService.presenceDevidoir()) {

        }
    }

    public void ejectionModule(ModuleLunaire module) {
        log.info("Ejection module {}", module.type().name());

        servosService.devidoirChargement();
        if (module.isPolychrome()) {
            // TODO
        }
        servosService.ouvreGlissiere();
        while(ioService.finCourseGlissiereGauche());
        servosService.fermeGlissiere();
        while(ioService.finCourseGlissiereDroite());
        servosService.stopGlissiere();
    }

    private void waitTimeMs(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log.error("Interruption du Thread", e);
        }
    }
}
