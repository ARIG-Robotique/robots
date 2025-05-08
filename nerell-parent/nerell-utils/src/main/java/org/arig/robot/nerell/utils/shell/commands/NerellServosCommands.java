package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.AbstractNerellFaceService;
import org.arig.robot.services.NerellFaceWrapper;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@AllArgsConstructor
@ShellCommandGroup("Servos")
public class NerellServosCommands {

    private final NerellRobotServosService servosService;
    private final NerellIOService ioService;
    private final AbstractEnergyService energyService;
    private final NerellFaceWrapper faceWrapper;

    private final int nbLoop = 5;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos()
                ? Availability.available() : Availability.unavailable("Alimentation servos KO");
    }

    @ShellMethod("Cycle de préparation des servos")
    public void preparation() {
        servosService.cyclePreparation();
        ThreadUtils.sleep(800);
    }

    @ShellMethod("Identification des servos")
    public void identificationServos(byte id, int delta, byte speed, int nbCycle) {
        for (int i = 0; i < nbCycle; i++) {
            servosService.setPositionById(id, 1500 + delta, speed);
            ThreadUtils.sleep(1000);
            servosService.setPositionById(id, 1500 - delta, speed);
            ThreadUtils.sleep(1000);
        }
        servosService.setPositionById(id, 1500, speed);
    }

    @ShellMethod("Configuration attente pince avant")
    public void configWaitPinceAvant(int wait) {
        servosService.groupeDoigtsAvantFerme(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupePincesAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupePincesAvantRepos(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente pince arriere")
    public void configWaitPinceArriere(int wait) {
        servosService.groupeDoigtsArriereFerme(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupePincesArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupePincesArriereRepos(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente doigts avant")
    public void configWaitDoigtsAvant(int wait) {
        servosService.groupePincesAvantOuvert(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeDoigtsAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeDoigtsAvantFerme(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente doigts arriere")
    public void configWaitDoigtsArriere(int wait) {
        servosService.groupePincesArriereOuvert(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeDoigtsArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeDoigtsArriereFerme(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente block colonne avant")
    public void configWaitBlockColonneAvant(int wait) {
        servosService.groupePincesAvantOuvert(false);
        servosService.ascenseurAvantHaut(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeBlockColonneAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeBlockColonneAvantFerme(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente block colonne arriere")
    public void configWaitBlockColonneArriere(int wait) {
        servosService.groupePincesArriereOuvert(false);
        servosService.ascenseurArriereHaut(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.groupeBlockColonneArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.groupeBlockColonneArriereFerme(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente ascenseur avant")
    public void configWaitAscenseurAvant(int wait) {
        servosService.groupePincesAvantOuvert(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.ascenseurAvantHaut(false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurAvantBas(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente ascenseur arriere")
    public void configWaitAscenseurArriere(int wait) {
        servosService.groupePincesArriereOuvert(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.ascenseurArriereHaut(false);
            ThreadUtils.sleep(wait);
            servosService.ascenseurArriereBas(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente tiroir avant")
    public void configWaitTiroirAvant(int wait) {
        servosService.becAvantFerme(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.tiroirAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.tiroirAvantStock(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente tiroir arriere")
    public void configWaitTiroirArriere(int wait) {
        servosService.becArriereFerme(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.tiroirArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.tiroirArriereStock(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente bec avant")
    public void configWaitBecAvant(int wait) {
        servosService.tiroirAvantOuvert(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.becAvantOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.becAvantFerme(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Configuration attente bec arriere")
    public void configWaitBecArriere(int wait) {
        servosService.tiroirArriereOuvert(true);
        for (int i = 0; i < nbLoop; i++) {
            servosService.becArriereOuvert(false);
            ThreadUtils.sleep(wait);
            servosService.becArriereFerme(false);
            ThreadUtils.sleep(wait);
        }

        preparation();
    }

    @ShellMethod("Chargement face avant")
    public void testChargementFaceAvant() {
        testChargementFace(NerellFaceWrapper.Face.AVANT);
    }

    @ShellMethod("Chargement face arriere")
    public void testChargementFaceArriere() {
        testChargementFace(NerellFaceWrapper.Face.ARRIERE);
    }

    private void testChargementFace(NerellFaceWrapper.Face face) {
        AbstractNerellFaceService faceService = faceWrapper.getFaceService(face);

        try {
            GradinBrut gradin = new GradinBrut(GradinBrut.ID.BLEU_BAS_CENTRE, 0, 0, false, GradinBrut.Orientation.HORIZONTAL);
            faceService.preparePriseGradinBrut(gradin);
            boolean tirette = ThreadUtils.waitUntil(ioService::tirette, 1000, 60000);
            if (!tirette) {
                log.error("Tirette non détectée");
                return;
            }
            AbstractNerellFaceService.PriseGradinState priseGradinState = faceService.prendreGradinBrutStockTiroir();
            if (priseGradinState == AbstractNerellFaceService.PriseGradinState.ERREUR_COLONNES) {
                log.info("Enleve tirrette pour stock colonnes");
                tirette = ThreadUtils.waitUntil(() -> !ioService.tirette(), 1000, 60000);
                if (tirette) {
                    log.error("Tirette non enlevée");
                    return;
                }
                servosService.groupeBlockColonneAvantFerme(true);
                priseGradinState = AbstractNerellFaceService.PriseGradinState.OK;
            }
            log.info("Résultat chargement face {} : {}", face, priseGradinState);
        } catch (Exception e) {
            log.error("Erreur lors du chargement de la face {}", face, e);
        }
    }

    @ShellMethod("Test construction etage 1 avant")
    public void testConstructionEtage1Avant() {
        testConstructionEtage1(NerellFaceWrapper.Face.AVANT);
    }

    @ShellMethod("Test construction etage 1 arriere")
    public void testConstructionEtage1Arriere() {
        testConstructionEtage1(NerellFaceWrapper.Face.ARRIERE);
    }

    private void testConstructionEtage1(NerellFaceWrapper.Face face) {
        log.info("Début test construction etage 1 sur la face {}. Start avec tirette", face);
        boolean tirette = ThreadUtils.waitUntil(ioService::tirette, 1000, 60000);
        if (!tirette) {
            log.error("Tirette non détectée");
            return;
        }

        log.info("Pince  : G {} - D {}", ioService.pinceAvantGauche(true), ioService.pinceAvantDroite(true));
        log.info("Sol    : G {} - D {}", ioService.solAvantGauche(true), ioService.solAvantDroite(true));
        log.info("Tiroir : B {} - H {}", ioService.tiroirAvantBas(true), ioService.tiroirAvantHaut(true));

        if (ioService.pinceAvantGauche(true) && ioService.pinceAvantDroite(true) &&
            ioService.tiroirAvantHaut(true) && ioService.tiroirAvantBas(true)
        ) {
            log.info("Construction 1 etage avec le stock pince et planche haut");
            servosService.groupePincesAvantPrise(true);
            servosService.ascenseurAvantHaut(true);
            servosService.tiroirAvantOuvert(true);
            servosService.becAvantOuvert(true);
            servosService.ascenseurAvantSplit(true);
            servosService.becAvantFerme(true);
            servosService.tiroirAvantStock(true);
            servosService.ascenseurAvantBas(true);
            servosService.groupeDoigtsAvantLache(true);

            log.info("Enleve etage et tirette");
            while(ioService.tirette() || ioService.pinceAvantGauche(false) || ioService.pinceAvantDroite(false)) {
                ThreadUtils.sleep(100);
            }

            servosService.groupeDoigtsAvantFerme(false);
            if (ioService.solAvantDroite(true) || ioService.solAvantGauche(true)) {
                servosService.ascenseurAvantStock(false);
            } else {
                servosService.ascenseurAvantRepos(false);
            }
            servosService.groupePincesAvantRepos(false);

        } else if (ioService.solAvantDroite(true) && ioService.solAvantGauche(true) &&
                   !ioService.tiroirAvantHaut(true) && ioService.tiroirAvantBas(true)
        ) {
            servosService.groupeBlockColonneAvantOuvert(true);
            // ????

        } else {
            log.error("Pas de construction possible étage 1 sur la face {}", face);
        }
    }

    @ShellMethod("Test construction etage 2 avant")
    public void testConstructionEtage2Avant() {
        testConstructionEtage1(NerellFaceWrapper.Face.AVANT);
    }

    @ShellMethod("Test construction etage 2 arriere")
    public void testConstructionEtage2Arriere() {
        testConstructionEtage1(NerellFaceWrapper.Face.ARRIERE);
    }

    private void testConstructionEtage2(NerellFaceWrapper.Face face) {

    }
}
