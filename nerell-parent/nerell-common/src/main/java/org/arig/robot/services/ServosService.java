package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.constants.IConstantesUtiles;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServosService {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IIOService ioService;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void cyclePreparation() {
        log.info("Servos en position initiale");
        servos.printVersion();

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        homes();
    }

    public void homes() {
        servos.setPositionAndSpeed(IConstantesServos.MOUSTACHE_DROITE, IConstantesServos.POS_MOUSTACHE_DROITE_FERME, IConstantesServos.SPEED_MOUSTACHE_DROITE);
        servos.setPositionAndSpeed(IConstantesServos.MOUSTACHE_GAUCHE, IConstantesServos.POS_MOUSTACHE_GAUCHE_FERME, IConstantesServos.SPEED_MOUSTACHE_GAUCHE);
        servos.setPositionAndSpeed(IConstantesServos.BRAS_DROIT, IConstantesServos.POS_BRAS_DROIT_FERME, IConstantesServos.SPEED_BRAS_DROIT);
        servos.setPositionAndSpeed(IConstantesServos.BRAS_GAUCHE, IConstantesServos.POS_BRAS_GAUCHE_FERME, IConstantesServos.SPEED_BRAS_GAUCHE);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR_AVANT, IConstantesServos.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE, IConstantesServos.SPEED_ASCENSEUR_AVANT);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_AVANT_1, IConstantesServos.POS_PINCE_AVANT_1_FERME, IConstantesServos.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_AVANT_2, IConstantesServos.POS_PINCE_AVANT_2_FERME, IConstantesServos.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_AVANT_3, IConstantesServos.POS_PINCE_AVANT_3_FERME, IConstantesServos.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_AVANT_4, IConstantesServos.POS_PINCE_AVANT_4_FERME, IConstantesServos.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR_ARRIERE, IConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT, IConstantesServos.SPEED_ASCENSEUR_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_ARRIERE_1, IConstantesServos.POS_PINCE_ARRIERE_1_FERME, IConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_ARRIERE_2, IConstantesServos.POS_PINCE_ARRIERE_2_FERME, IConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_ARRIERE_3, IConstantesServos.POS_PINCE_ARRIERE_3_FERME, IConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_ARRIERE_4, IConstantesServos.POS_PINCE_ARRIERE_4_FERME, IConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_ARRIERE_5, IConstantesServos.POS_PINCE_ARRIERE_5_FERME, IConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServos.PIVOT_ARRIERE, IConstantesServos.POS_PIVOT_ARRIERE_FERME, IConstantesServos.SPEED_PIVOT_ARRIERE);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isMoustachesOuvert() {
        return servos.getPosition(IConstantesServos.MOUSTACHE_DROITE) == IConstantesServos.POS_MOUSTACHE_DROITE_OUVERT;
    }

    public boolean isMoustachesFerme() {
        return servos.getPosition(IConstantesServos.MOUSTACHE_DROITE) == IConstantesServos.POS_MOUSTACHE_DROITE_FERME;
    }

    public boolean isAscenseurAvantHaut() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_AVANT) == IConstantesServos.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE;
    }

    public boolean isAscenseurAvantBas() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_AVANT) == IConstantesServos.POS_ASCENSEUR_AVANT_BAS;
    }

    public boolean isPincesAvantOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_AVANT_1) == IConstantesServos.POS_PINCE_AVANT_1_OUVERT;
    }

    public boolean isPincesAvantFerme() {
        return servos.getPosition(IConstantesServos.PINCE_AVANT_1) == IConstantesServos.POS_PINCE_AVANT_1_FERME;
    }

    public boolean isAscenseurArriereHaut() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_ARRIERE) == IConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT;
    }

    public boolean isAscenseurArriereBas() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_ARRIERE) == IConstantesServos.POS_ASCENSEUR_ARRIERE_ECCUEIL;
    }

    public boolean isPivotArriereFerme() {
        return servos.getPosition(IConstantesServos.PIVOT_ARRIERE) == IConstantesServos.POS_PIVOT_ARRIERE_FERME;
    }

    public boolean isPivotArriereOuvert() {
        return servos.getPosition(IConstantesServos.PIVOT_ARRIERE) == IConstantesServos.POS_PIVOT_ARRIERE_OUVERT;
    }

    public boolean isPincesArriereOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_ARRIERE_1) == IConstantesServos.POS_PINCE_ARRIERE_1_OUVERT;
    }

    public boolean isPincesArriereFerme() {
        return servos.getPosition(IConstantesServos.PINCE_ARRIERE_1) == IConstantesServos.POS_PINCE_ARRIERE_1_FERME;
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    private void setPosition(byte servo, int position, boolean wait) {
        if (!wait) {
            servos.setPosition(servo, position);
        } else {
            int currentPosition = servos.getPosition(servo);
            if (currentPosition != position) {
                servos.setPosition(servo, position);
                ThreadUtils.sleep(computeWaitTime(servo, currentPosition, position));
            }
        }
    }

    private void setPositionBatch(int[][] servosPos, boolean wait) {
        int currentPosition = servos.getPosition((byte) servosPos[0][0]);

        for (int[] servoPos : servosPos) {
            servos.setPosition((byte) servoPos[0], servoPos[1]);
        }

        if (wait && currentPosition != servosPos[0][1]) {
            ThreadUtils.sleep(computeWaitTime((byte) servosPos[0][0], currentPosition, servosPos[0][1]));
        }
    }

    private int computeWaitTime(byte servo, int currentPosition, int position) {
        Triple<Integer, Integer, Integer> config = IConstantesServos.MIN_TIME_MAX.get(servo);

        int min = config.getLeft();
        int time = config.getMiddle();
        int max = config.getRight();

        // au cas ou la constante n'a pas été triée
        if (max < min) {
            min = config.getRight();
            max = config.getLeft();
        }

        double wait = time * Math.abs(position - currentPosition) / (max * 1. - min);
        return (int) Math.round(wait);
    }

    public void moustachesOuvert(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_MOUSTACHES).get(IConstantesServos.POS_BATCH_MOUSTACHES_OUVERT), wait);
    }

    public void moustachesFerme(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_MOUSTACHES).get(IConstantesServos.POS_BATCH_MOUSTACHES_FERME), wait);
    }

    // TODO gestion de la vitesse
    public void moustachesPoussette(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_MOUSTACHES).get(IConstantesServos.POS_BATCH_MOUSTACHES_POUSETTE), wait);
    }

    public void brasGaucheMancheAAir(boolean wait) {
        setPosition(IConstantesServos.BRAS_GAUCHE, IConstantesServos.POS_BRAS_GAUCHE_MANCHE_AIR, wait);
    }

    public void brasGauchePhare(boolean wait) {
        setPosition(IConstantesServos.BRAS_GAUCHE, IConstantesServos.POS_BRAS_GAUCHE_PHARE, wait);
    }

    public void brasGaucheFerme(boolean wait) {
        setPosition(IConstantesServos.BRAS_GAUCHE, IConstantesServos.POS_BRAS_GAUCHE_FERME, wait);
    }

    public void brasDroitMancheAAir(boolean wait) {
        setPosition(IConstantesServos.BRAS_DROIT, IConstantesServos.POS_BRAS_DROIT_MANCHE_AIR, wait);
    }

    public void brasDroitPhare(boolean wait) {
        setPosition(IConstantesServos.BRAS_DROIT, IConstantesServos.POS_BRAS_DROIT_PHARE, wait);
    }

    public void brasDroitFerme(boolean wait) {
        setPosition(IConstantesServos.BRAS_DROIT, IConstantesServos.POS_BRAS_DROIT_FERME, wait);
    }

    public void ascenseurAvantOuvertureMoustache(boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_AVANT, IConstantesServos.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE, wait);
    }

    public void ascenseurAvantRoulage(boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_AVANT, IConstantesServos.POS_ASCENSEUR_AVANT_ROULAGE, wait);
    }

    public void ascenseurAvantBas(boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_AVANT, IConstantesServos.POS_ASCENSEUR_AVANT_BAS, wait);
    }

    public void ascenseurArriereHaut(boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_ARRIERE, IConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT, wait);
    }

    public void ascenseurArriereEccueil(boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_ARRIERE, IConstantesServos.POS_ASCENSEUR_ARRIERE_ECCUEIL, wait);
    }

    public void ascenseurArriereTable(boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_ARRIERE, IConstantesServos.POS_ASCENSEUR_ARRIERE_TABLE, wait);
    }

    public void pivotArriereFerme(boolean wait) {
        setPosition(IConstantesServos.PIVOT_ARRIERE, IConstantesServos.POS_PIVOT_ARRIERE_FERME, wait);
    }

    public void pivotArriereOuvert(boolean wait) {
        setPosition(IConstantesServos.PIVOT_ARRIERE, IConstantesServos.POS_PIVOT_ARRIERE_OUVERT, wait);
    }

    public void pincesAvantPrise(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_AVANT).get(IConstantesServos.POS_BATCH_PINCES_AVANT_PRISE), wait);
    }

    public void pinceAvantPrise(int index, boolean wait) {
        int[] config = IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_AVANT).get(IConstantesServos.POS_BATCH_PINCES_AVANT_PRISE)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesAvantFerme(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_AVANT).get(IConstantesServos.POS_BATCH_PINCES_AVANT_FERME), wait);
    }

    public void pinceAvantFerme(int index, boolean wait) {
        int[] config = IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_AVANT).get(IConstantesServos.POS_BATCH_PINCES_AVANT_FERME)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesAvantOuvert(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_AVANT).get(IConstantesServos.POS_BATCH_PINCES_AVANT_OUVERT), wait);
    }

    public void pinceAvantOuvert(int index, boolean wait) {
        int[] config = IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_AVANT).get(IConstantesServos.POS_BATCH_PINCES_AVANT_OUVERT)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesArriereFerme(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_ARRIERE).get(IConstantesServos.POS_BATCH_PINCES_ARRIERE_FERME), wait);
    }

    public void pinceArriereFerme(int index, boolean wait) {
        int[] config = IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_ARRIERE).get(IConstantesServos.POS_BATCH_PINCES_ARRIERE_FERME)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesArriereOuvert(boolean wait) {
        setPositionBatch(IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_ARRIERE).get(IConstantesServos.POS_BATCH_PINCES_ARRIERE_OUVERT), wait);
    }

    public void pinceArriereOuvert(int index, boolean wait) {
        int[] config = IConstantesServos.BATCH_CONFIG.get(IConstantesServos.BATCH_PINCES_ARRIERE).get(IConstantesServos.POS_BATCH_PINCES_ARRIERE_OUVERT)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    //*******************************************//
    //* Autres                                  *//
    //*******************************************//

    public void controlBatteryVolts() {
        final double tension = getTension();
        if (tension < IConstantesUtiles.SEUIL_BATTERY_VOLTS && tension > 0) {
            log.warn("La tension de la carte sd21 a dépassé le seuil avec une valeur {}", tension);
            ioService.disableAlim12VPuissance();
            ioService.disableAlim5VPuissance();
        }
    }

    public double getTension() {
        return servos.getTension();
    }

}
