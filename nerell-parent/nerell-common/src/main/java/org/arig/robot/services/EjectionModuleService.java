package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 22/05/17.
 */
@Slf4j
@Service
public class EjectionModuleService {

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    public void init() {
        log.info("Initialisation de l'ejection des modules");
        if (ioService.glissiereFerme()) {
            servosService.ouvreGlissiere();
            while(!ioService.finCourseGlissiereDroite()) {
                waitTimeMs(1);
            }
            servosService.stopGlissiere();
        }

        // Dans une position ouverte quelque part
        servosService.fermeGlissiere();
        while(ioService.finCourseGlissiereDroite()) {
            waitTimeMs(1);
        }
        servosService.stopGlissiere();
    }

    public void ejectionAvantRetourStand() {
        if (ioService.presenceRouleaux()) {
            ejectionModule(ModuleLunaire.monochrome());
        }

        while(ioService.presenceDevidoir()) {
            servosService.devidoirDechargement();
            while(!ioService.presenceRouleaux()) {
                waitTimeMs(10);
            }
            ejectionModule(ModuleLunaire.monochrome());
        }
    }

    public void ejectionModule(ModuleLunaire module) {
        log.info("Ejection module {}", module.type().name());
        if (module.isPolychrome()) {
            // TODO
        }

        servosService.devidoirChargement();
        servosService.ouvreGlissiere();
        while(ioService.finCourseGlissiereGauche()) {
            waitTimeMs(1);
        }
        servosService.fermeGlissiere();
        while(ioService.finCourseGlissiereDroite()) {
            waitTimeMs(1);
        }
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
