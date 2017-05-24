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

    private static final int TEMPS_MAX_PRESENCE_ROULEAUX = 2000;
    private static final int TEMPS_MAX_TROUVER_COULEUR = 3000;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    public void init() {
        if (!rs.isSimulateur()) {
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
            doEject();
        }

        while (ioService.presenceDevidoir()) {
            servosService.devidoirDechargement();
            servosService.waitDevidoire();
            doEject();
        }
    }

    public void ejectionModule() {
        log.info("Ejection module");

        ModuleLunaire module = rs.nextModule();

        if (module == null) {
            log.info("Pas de module present dans le magasin");
            return;
        }

        int remaining = TEMPS_MAX_PRESENCE_ROULEAUX;
        while (!ioService.presenceRouleaux() && remaining > 0) {
            remaining -= 10;
            waitTimeMs(10);
        }

        if (!ioService.presenceRouleaux()) {
            log.warn("Le dévidoir à dit qu'il était plein mais il a rien rendu !");
            return;
        }

        rs.disableMagasin();

        if (module.isPolychrome()) {

            ioService.enableLedCapteurCouleur();
            servosService.devidoirLectureCouleur();
            servosService.waitDevidoire();

            if (ioService.getTeamColorFromSensor() == rs.getTeam()) {
                servosService.tourneModuleRouleauxRF();

                remaining = TEMPS_MAX_TROUVER_COULEUR;
                while (ioService.getTeamColorFromSensor() == rs.getTeam() && remaining > 0) {
                    remaining -= 10;
                    waitTimeMs(10);
                }
            }

            servosService.tourneModuleRouleauxFF();

            remaining = TEMPS_MAX_TROUVER_COULEUR;
            while (ioService.getTeamColorFromSensor() != rs.getTeam() && remaining > 0) {
                remaining -= 10;
                waitTimeMs(10);
            }

            if (ioService.getTeamColorFromSensor() != rs.getTeam()) {
                log.warn("Imposible de trouver la couleur du module");
            }

            servosService.tourneModuleRouleauxStop();
            ioService.disableLedCapteurCouleur();

        }

        doEject();

        rs.enableMagasin();
    }

    private void doEject() {
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
