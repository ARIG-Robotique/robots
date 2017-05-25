package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrasService {

    private static final int NB_TENTATIVES_ASPIRATION = 2;
    private static final int TEMPS_TENTATIVE_ASPIRATION = 1000;
    private static final int TEMPS_ROULAGE_MODULE = 500;

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
        if (ioService.presencePinceDroite() && !ioService.presencePinceCentre()) {
            if (!robotStatus.canAddModuleMagasin()) {
                log.warn("Pas de place dans la magasin");
                return false;
            }

            if (!servosService.isPinceCentreFerme()) {
                if (servosService.isBrasPriseRobot() || servosService.isBrasAttente()) {
                    servosService.brasVertical();
                    servosService.waitBras();
                }
                servosService.pinceCentreFerme();
                servosService.waitPince();
            }

            servosService.pinceDroiteVentouse();
            servosService.brasPriseRobot();
            servosService.waitBras();

            if (!ioService.presencePinceCentre()) {
                log.warn("Tentative de stockage de module sans module !");
                servosService.pinceDroiteOuvert();
                servosService.brasAttentePriseRobot();
                return false;
            }

            ioService.enablePompeAVide();

            if (!tentativeAspirationRobot(NB_TENTATIVES_ASPIRATION)) {
                log.warn("Impossible d'aspirer le module !");
                ioService.disablePompeAVide();
                servosService.pinceDroiteOuvert();
                servosService.brasAttentePriseRobot();
                return false;
            }

            servosService.porteMagasinOuvert();
            servosService.pinceDroiteOuvert();
            servosService.waitPince();

        } else if (ioService.presencePinceCentre() && servosService.isPinceCentreFerme()
                && !ioService.presencePinceDroite() && (servosService.isPinceDroiteOuvert() || servosService.isPinceDroitePriseProduit())) {

            log.info("Module centre alors que pince centre ferme. La pince droite est vide, un petit mercato");
            servosService.brasVertical();
            servosService.pinceCentreOuvert();

        } else if (ioService.presencePinceCentre()) {
            if (!robotStatus.canAddModuleMagasin()) {
                log.warn("Pas de place dans la magasin");
                return false;
            }

            servosService.brasPriseRobot();
            servosService.waitBras();

            ioService.enablePompeAVide();

            if (!tentativeAspirationRobot(NB_TENTATIVES_ASPIRATION)) {
                log.warn("Impossible d'aspirer le module !");

                ioService.disablePompeAVide();
                servosService.pinceDroiteOuvert();
                servosService.brasAttentePriseRobot();

                return false;
            }

            if (ioService.presencePinceDroite()) {
                servosService.brasAttenteDepose();
                servosService.waitBras();
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
        servosService.waitBras();

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
        ioService.enableElectroVanne();

        servosService.brasAttentePriseRobot();

        servosService.entreeMagasinOuvert();
        sleep(TEMPS_ROULAGE_MODULE);
        robotStatus.addModuleDansMagasin(ModuleLunaire.monochrome());

        ioService.disableElectroVanne();

        servosService.entreeMagasinFerme();
        servosService.porteMagasinOuvert();

        return true;
    }

    public boolean stockerModuleFusee() {
        if (ioService.presenceFusee()) {
            if (!robotStatus.canAddModuleMagasin()) {
                log.warn("Pas de place dans la magasin");
                return false;
            }

            servosService.brasPriseFusee();
            servosService.waitBras();

            servosService.pinceCentreFerme();
            servosService.waitPince();

            ioService.enablePompeAVide();

            if (!tentativeAspirationFusee(2)) {
                log.warn("Impossible d'aspirer le module !");
                ioService.disablePompeAVide();
                servosService.brasAttentePriseFusee();
                return false;
            }

            servosService.brasDepose();
            servosService.waitBras();

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
            ioService.enableElectroVanne();

            servosService.brasAttentePriseFusee();

            servosService.entreeMagasinOuvert();
            sleep(500);

            ioService.disableElectroVanne();

            servosService.entreeMagasinFerme();
            servosService.porteMagasinOuvert();

            return true;

        } else {
            log.info("Aucun module à récupérer");
            return false;
        }
    }

    private boolean tentativeAspirationRobot(int nb) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;

        while (!ioService.presenceModuleDansBras() && remaining > 0) {
            remaining -= 100;
            sleep(100);
        }

        if (!ioService.presenceModuleDansBras()) {
            if (nb > 0) {
                servosService.brasAttentePriseRobot();
                servosService.waitBras();

                servosService.brasPriseRobot();
                servosService.waitBras();

                return tentativeAspirationRobot(nb - 1);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean tentativeAspirationFusee(int nb) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;

        while (!ioService.presenceModuleDansBras() && remaining > 0) {
            remaining -= 100;
            sleep(100);
        }

        if (!ioService.presenceModuleDansBras()) {
            if (nb > 0) {
                servosService.brasAttentePriseFusee();
                servosService.waitBras();

                servosService.brasPriseFusee();
                servosService.waitBras();

                return tentativeAspirationFusee(nb - 1);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
