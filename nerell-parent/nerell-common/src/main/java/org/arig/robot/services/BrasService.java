package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrasService {

    private static final int NB_TENTATIVES_ASPIRATION = 2;
    private static final int TEMPS_TENTATIVE_ASPIRATION = 1000;
    private static final int TEMPS_ROULAGE_MODULE = 1000;

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private ServosService servosService;

    @Autowired
    private IIOService ioService;

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public boolean stockerModuleRobot() {
        /*if (ioService.presencePinceDroite() && !ioService.presencePinceCentre()) {
            if (!robotStatus.canAddModuleMagasin()) {
                log.warn("Pas de place dans la magasin");
                return false;
            }

            if (!servosService.isPinceCentreFerme()) {
                if (servosService.isBrasPriseRobot() || servosService.isBrasAttente()) {
                    servosService.brasVertical();
                    servosService.waitBrasCourt();
                }
                servosService.pinceCentreFerme();
                servosService.waitPince();
            }

            servosService.brasPriseRobot();
            servosService.pinceDroiteVentouse();
            servosService.waitPince();

            if (!ioService.presencePinceCentre()) {
                log.warn("Tentative de stockage de module sans module !");
                servosService.pinceDroiteOuvert();
                servosService.brasVertical();
                servosService.waitBrasCourt();
                servosService.pinceCentreOuvert();
                servosService.waitPince();
                servosService.brasAttentePriseRobot();
                return false;
            }

            servosService.brasAttentePriseRobot();
            servosService.waitBrasCourt();
            ioService.enablePompeAVide();
            servosService.brasPriseRobot();
            servosService.waitBrasCourt();

            if (!tentativeAspirationRobot(NB_TENTATIVES_ASPIRATION)) {
                log.warn("Impossible d'aspirer le module !");
                ioService.disablePompeAVide();
                servosService.pinceDroiteOuvert();
                servosService.brasVertical();
                servosService.waitBrasCourt();
                servosService.pinceCentreOuvert();
                servosService.waitPince();
                servosService.brasAttentePriseRobot();
                return false;
            }

            servosService.porteMagasinOuvert();
            servosService.pinceDroiteOuvert();
            servosService.waitPince();

        } else if (ioService.presencePinceCentre()) {
            if (!robotStatus.canAddModuleMagasin()) {
                log.warn("Pas de place dans la magasin");
                return false;
            }

            ioService.enablePompeAVide();
            servosService.brasPriseRobot();
            servosService.waitBrasCourt();

            if (!tentativeAspirationRobot(NB_TENTATIVES_ASPIRATION)) {
                log.warn("Impossible d'aspirer le module !");

                ioService.disablePompeAVide();
                servosService.pinceDroiteOuvert();
                servosService.brasAttentePriseRobot();

                return false;
            }

            if (ioService.presencePinceDroite()) {
                servosService.brasAttenteDepose();
                servosService.waitBrasCourt();
            } else {
                servosService.pinceDroiteOuvert();
                servosService.pinceCentreOuvertDansDroit();
                servosService.waitPince();
            }

        } else {
            log.info("Aucun module à récupérer");
            return false;
        }

        servosService.brasDepose();
        servosService.waitBrasLong();

        servosService.pinceCentreOuvert();
        servosService.porteMagasinFerme();
        servosService.waitPorteMagasin();

        if (!ioService.presenceModuleDansBras()) {
            log.warn("Pas de module dans le bras, pas de chocolat :'(");

            ioService.disablePompeAVide();
            servosService.brasAttentePriseRobot();
            servosService.porteMagasinOuvert();

            return false;
        }

        ioService.disablePompeAVide();
        servosService.brasAttentePriseRobot();

        double remaining = TEMPS_ROULAGE_MODULE / 2;
        while (!ioService.presenceEntreeMagasin() && remaining > 0) {
            sleep(10);
            remaining -= 10;
        }

        remaining = TEMPS_ROULAGE_MODULE / 2;
        while (ioService.presenceEntreeMagasin() && remaining > 0) {
            sleep(10);
            remaining -= 10;
        }

        servosService.porteMagasinOuvert();
        */
        return true;
    }

    public boolean stockerModuleFusee() {
//        if (ioService.presenceFusee()) {

        /*
        if (!robotStatus.canAddModuleMagasin()) {
            log.warn("Pas de place dans la magasin");
            return false;
        }

        ioService.enablePompeAVide();

        servosService.brasPriseFusee();
        servosService.waitBrasCourt();

        if (!tentativeAspirationFusee(2)) {
            log.warn("Impossible d'aspirer le module !");
            ioService.disablePompeAVide();
            servosService.brasAttentePriseFusee();
            return false;
        }

        servosService.brasArracheFusee();
        servosService.waitBrasCourt();

        servosService.brasDeposeFromFusee();
        servosService.waitBrasLong();

        servosService.porteMagasinFerme();
        servosService.waitPorteMagasin();

        if (!ioService.presenceModuleDansBras()) {
            log.warn("Pas de module dans le bras, pas de chocolat :'(");

            ioService.disablePompeAVide();
            servosService.brasAttentePriseFusee();
            servosService.porteMagasinOuvert();

            return false;
        }

        ioService.disablePompeAVide();
        servosService.brasAttentePriseFusee();

        double remaining = TEMPS_ROULAGE_MODULE / 2;
        while (!ioService.presenceEntreeMagasin() && remaining > 0) {
            sleep(10);
            remaining -= 10;
        }

        remaining = TEMPS_ROULAGE_MODULE / 2;
        while (ioService.presenceEntreeMagasin() && remaining > 0) {
            sleep(10);
            remaining -= 10;
        }

        servosService.porteMagasinOuvert();
*/
        return true;
//        } else {
//            log.info("Aucun module à récupérer");
//            return false;
//        }
    }

    private boolean tentativeAspirationRobot(int nb) {
        /*
        long remaining = TEMPS_TENTATIVE_ASPIRATION;

        while (!ioService.presenceModuleDansBras() && remaining > 0) {
            remaining -= 100;
            sleep(100);
        }

        if (!ioService.presenceModuleDansBras()) {
            if (nb > 0) {
                servosService.brasAttentePriseRobot();
                servosService.waitBrasCourt();

                servosService.brasPriseRobot();
                servosService.waitBrasCourt();

                return tentativeAspirationRobot(nb - 1);
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
        return true;
    }

    private boolean tentativeAspirationFusee(int nb) {
        /*
        long remaining = TEMPS_TENTATIVE_ASPIRATION;

        while (!ioService.presenceModuleDansBras() && remaining > 0) {
            remaining -= 100;
            sleep(100);
        }

        if (!ioService.presenceModuleDansBras()) {
            if (nb > 0) {
                servosService.brasAttentePriseFusee();
                servosService.waitBrasCourt();

                servosService.brasPriseFusee();
                servosService.waitBrasCourt();

                return tentativeAspirationFusee(nb - 1);
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
        return true;
    }
}
