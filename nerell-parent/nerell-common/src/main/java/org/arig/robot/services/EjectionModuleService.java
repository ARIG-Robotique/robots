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

    private static final int TEMPS_MAX_PRESENCE_ROULEAUX = 1000;
    private static final int TEMPS_MAX_TROUVER_COULEUR = 3000;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus robotStatus;

    public void init() {
        if (!robotStatus.isSimulateur()) {
            log.info("Initialisation de l'ejection des modules");

            if (ioService.glissiereFerme()) {
                servosService.ouvreGlissiere();
                while (!ioService.finCourseGlissiereDroite()) {
                    waitTimeMs(1);
                }
                servosService.stopGlissiere();
            }

            // Dans une position ouverte quelque part
            servosService.fermeGlissiere();
            while (ioService.finCourseGlissiereDroite()) {
                waitTimeMs(1);
            }
            servosService.stopGlissiere();

            log.info("Fin d'initialisation de l'ejection des modules");
        }
    }

    public void ejectionAvantRetourStand() {
        if (ioService.presenceRouleaux()) {
            ejectionModule(ModuleLunaire.monochrome());
        }

        while (ioService.presenceDevidoir()) {
            servosService.devidoirDechargement();

            int remaining = TEMPS_MAX_PRESENCE_ROULEAUX;
            while (!ioService.presenceRouleaux() && remaining > 0) {
                remaining -= 10;
                waitTimeMs(10);
            }

            if (ioService.presenceRouleaux()) {
                ejectionModule(ModuleLunaire.monochrome());
            } else {
                log.warn("Le dévidoir à dit qu'il était plein mais il a rien rendu !");
                servosService.devidoirChargement();
                servosService.waitDevidoire();
            }
        }
    }

    public void ejectionModule(ModuleLunaire module) {
        log.info("Ejection module {}", module.type().name());

        if (module.isPolychrome()) {
            servosService.devidoirLectureCouleur();
            servosService.waitDevidoire();

            // TODO - Ya des grandes chances que ca marche pas !
            ioService.enableLedCapteurCouleur();
            servosService.tourneModuleRouleauxFF();

            int remaining = TEMPS_MAX_TROUVER_COULEUR;
            while (ioService.getTeamColorFromSensor() != robotStatus.getTeam() && remaining > 0) {
                remaining -= 10;
                waitTimeMs(10);
            }

            if (ioService.getTeamColorFromSensor() != robotStatus.getTeam()) {
                log.warn("Imposible de trouver la couleur du module");
            }

            servosService.tourneModuleRouleauxStop();
            ioService.disableLedCapteurCouleur();
        }

        servosService.devidoirChargement();

        servosService.ouvreGlissiere();
        while (ioService.finCourseGlissiereGauche()) {
            waitTimeMs(1);
        }
        servosService.fermeGlissiere();
        while (ioService.finCourseGlissiereDroite()) {
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
