package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.constants.IConstantesServosNerell;
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
        servos.setPositionAndSpeed(IConstantesServosNerell.MOUSTACHE_DROITE, IConstantesServosNerell.POS_MOUSTACHE_DROITE_FERME, IConstantesServosNerell.SPEED_MOUSTACHE);
        servos.setPositionAndSpeed(IConstantesServosNerell.MOUSTACHE_GAUCHE, IConstantesServosNerell.POS_MOUSTACHE_GAUCHE_FERME, IConstantesServosNerell.SPEED_MOUSTACHE);
        servos.setPositionAndSpeed(IConstantesServosNerell.BRAS_DROIT, IConstantesServosNerell.POS_BRAS_DROIT_FERME, IConstantesServosNerell.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServosNerell.BRAS_GAUCHE, IConstantesServosNerell.POS_BRAS_GAUCHE_FERME, IConstantesServosNerell.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServosNerell.ASCENSEUR_AVANT, IConstantesServosNerell.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE, IConstantesServosNerell.SPEED_ASCENSEUR_AVANT);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_AVANT_1, IConstantesServosNerell.POS_PINCE_AVANT_1_FERME, IConstantesServosNerell.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_AVANT_2, IConstantesServosNerell.POS_PINCE_AVANT_2_FERME, IConstantesServosNerell.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_AVANT_3, IConstantesServosNerell.POS_PINCE_AVANT_3_FERME, IConstantesServosNerell.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_AVANT_4, IConstantesServosNerell.POS_PINCE_AVANT_4_FERME, IConstantesServosNerell.SPEED_PINCE_AVANT);
        servos.setPositionAndSpeed(IConstantesServosNerell.ASCENSEUR_ARRIERE, IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_HAUT, IConstantesServosNerell.SPEED_ASCENSEUR_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_ARRIERE_1, IConstantesServosNerell.POS_PINCE_ARRIERE_1_FERME, IConstantesServosNerell.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_ARRIERE_2, IConstantesServosNerell.POS_PINCE_ARRIERE_2_FERME, IConstantesServosNerell.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_ARRIERE_3, IConstantesServosNerell.POS_PINCE_ARRIERE_3_FERME, IConstantesServosNerell.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_ARRIERE_4, IConstantesServosNerell.POS_PINCE_ARRIERE_4_FERME, IConstantesServosNerell.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServosNerell.PINCE_ARRIERE_5, IConstantesServosNerell.POS_PINCE_ARRIERE_5_FERME, IConstantesServosNerell.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(IConstantesServosNerell.PIVOT_ARRIERE, IConstantesServosNerell.POS_PIVOT_ARRIERE_FERME, IConstantesServosNerell.SPEED_PIVOT_ARRIERE);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isMoustachesOuvert() {
        return servos.getPosition(IConstantesServosNerell.MOUSTACHE_DROITE) == IConstantesServosNerell.POS_MOUSTACHE_DROITE_OUVERT;
    }

    public boolean isMoustachesFerme() {
        return servos.getPosition(IConstantesServosNerell.MOUSTACHE_DROITE) == IConstantesServosNerell.POS_MOUSTACHE_DROITE_FERME;
    }

    public boolean isAscenseurAvantHaut() {
        return servos.getPosition(IConstantesServosNerell.ASCENSEUR_AVANT) == IConstantesServosNerell.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE;
    }

    public boolean isAscenseurAvantBas() {
        return servos.getPosition(IConstantesServosNerell.ASCENSEUR_AVANT) == IConstantesServosNerell.POS_ASCENSEUR_AVANT_BAS;
    }

    public boolean isPincesAvantOuvert() {
        return servos.getPosition(IConstantesServosNerell.PINCE_AVANT_1) == IConstantesServosNerell.POS_PINCE_AVANT_1_OUVERT;
    }

    public boolean isPincesAvantFerme() {
        return servos.getPosition(IConstantesServosNerell.PINCE_AVANT_1) == IConstantesServosNerell.POS_PINCE_AVANT_1_FERME;
    }

    public boolean isAscenseurArriereHaut() {
        return servos.getPosition(IConstantesServosNerell.ASCENSEUR_ARRIERE) == IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_HAUT;
    }

    public boolean isAscenseurArriereBas() {
        return servos.getPosition(IConstantesServosNerell.ASCENSEUR_ARRIERE) == IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_ECUEIL;
    }

    public boolean isPivotArriereFerme() {
        return servos.getPosition(IConstantesServosNerell.PIVOT_ARRIERE) == IConstantesServosNerell.POS_PIVOT_ARRIERE_FERME;
    }

    public boolean isPivotArriereOuvert() {
        return servos.getPosition(IConstantesServosNerell.PIVOT_ARRIERE) == IConstantesServosNerell.POS_PIVOT_ARRIERE_OUVERT;
    }

    public boolean isPincesArriereOuvert() {
        return servos.getPosition(IConstantesServosNerell.PINCE_ARRIERE_1) == IConstantesServosNerell.POS_PINCE_ARRIERE_1_OUVERT;
    }

    public boolean isPincesArriereFerme() {
        return servos.getPosition(IConstantesServosNerell.PINCE_ARRIERE_1) == IConstantesServosNerell.POS_PINCE_ARRIERE_1_FERME;
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

    private void setPositionBatchAndSpeed(int[][] servosPos, byte speed, boolean wait) {
        int currentPosition = servos.getPosition((byte) servosPos[0][0]);

        for (int[] servoPos : servosPos) {
            servos.setPositionAndSpeed((byte) servoPos[0], servoPos[1], speed);
        }

        if (wait && currentPosition != servosPos[0][1]) {
            ThreadUtils.sleep(computeWaitTime((byte) servosPos[0][0], currentPosition, servosPos[0][1]));
        }
    }

    private int computeWaitTime(byte servo, int currentPosition, int position) {
        Triple<Integer, Integer, Integer> config = IConstantesServosNerell.MIN_TIME_MAX.get(servo);

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
        setPositionBatchAndSpeed(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_MOUSTACHES).get(IConstantesServosNerell.POS_BATCH_MOUSTACHES_OUVERT), IConstantesServosNerell.SPEED_MOUSTACHE, wait);
    }

    public void moustachesFerme(boolean wait) {
        setPositionBatchAndSpeed(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_MOUSTACHES).get(IConstantesServosNerell.POS_BATCH_MOUSTACHES_FERME), IConstantesServosNerell.SPEED_MOUSTACHE, wait);
    }

    public void moustachesPoussette(boolean wait) {
        setPositionBatchAndSpeed(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_MOUSTACHES).get(IConstantesServosNerell.POS_BATCH_MOUSTACHES_POUSETTE), IConstantesServosNerell.SPEED_MOUSTACHE_POUSSETTE, false);

        if (wait) {
            // Position poussette est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosNerell.WAIT_MOUSTACHE_POUSSETTE);
        }
    }

    public void brasGaucheMancheAAir(boolean wait) {
        setPosition(IConstantesServosNerell.BRAS_GAUCHE, IConstantesServosNerell.POS_BRAS_GAUCHE_MANCHE_AIR, wait);
    }

    public void brasGauchePhare(boolean wait) {
        setPosition(IConstantesServosNerell.BRAS_GAUCHE, IConstantesServosNerell.POS_BRAS_GAUCHE_PHARE, wait);
    }

    public void brasGaucheFerme(boolean wait) {
        setPosition(IConstantesServosNerell.BRAS_GAUCHE, IConstantesServosNerell.POS_BRAS_GAUCHE_FERME, wait);
    }

    public void brasDroitMancheAAir(boolean wait) {
        setPosition(IConstantesServosNerell.BRAS_DROIT, IConstantesServosNerell.POS_BRAS_DROIT_MANCHE_AIR, wait);
    }

    public void brasDroitPhare(boolean wait) {
        setPosition(IConstantesServosNerell.BRAS_DROIT, IConstantesServosNerell.POS_BRAS_DROIT_PHARE, wait);
    }

    public void brasDroitFerme(boolean wait) {
        setPosition(IConstantesServosNerell.BRAS_DROIT, IConstantesServosNerell.POS_BRAS_DROIT_FERME, wait);
    }

    public void ascenseurAvantOuvertureMoustache(boolean wait) {
        setPosition(IConstantesServosNerell.ASCENSEUR_AVANT, IConstantesServosNerell.POS_ASCENSEUR_AVANT_OUVERTURE_MOUSTACHE, wait);
    }

    public void ascenseurAvantRoulage(boolean wait) {
        setPosition(IConstantesServosNerell.ASCENSEUR_AVANT, IConstantesServosNerell.POS_ASCENSEUR_AVANT_ROULAGE, wait);
    }

    public void ascenseurAvantBas(boolean wait) {
        setPosition(IConstantesServosNerell.ASCENSEUR_AVANT, IConstantesServosNerell.POS_ASCENSEUR_AVANT_BAS, wait);
    }

    public void ascenseurArriereHaut(boolean wait) {
        setPosition(IConstantesServosNerell.ASCENSEUR_ARRIERE, IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_HAUT, wait);
    }

    public void ascenseurArriereEcueil(boolean wait) {
        setPosition(IConstantesServosNerell.ASCENSEUR_ARRIERE, IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_ECUEIL, wait);
    }

    public void ascenseurArriereTable(boolean wait) {
        setPosition(IConstantesServosNerell.ASCENSEUR_ARRIERE, IConstantesServosNerell.POS_ASCENSEUR_ARRIERE_TABLE, wait);
    }

    public void pivotArriereFerme(boolean wait) {
        setPosition(IConstantesServosNerell.PIVOT_ARRIERE, IConstantesServosNerell.POS_PIVOT_ARRIERE_FERME, wait);
    }

    public void pivotArriereOuvert(boolean wait) {
        setPosition(IConstantesServosNerell.PIVOT_ARRIERE, IConstantesServosNerell.POS_PIVOT_ARRIERE_OUVERT, wait);
    }

    public void pincesAvantPrise(boolean wait) {
        setPositionBatch(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_AVANT).get(IConstantesServosNerell.POS_BATCH_PINCES_AVANT_PRISE), wait);
    }

    public void pinceAvantPrise(int index, boolean wait) {
        int[] config = IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_AVANT).get(IConstantesServosNerell.POS_BATCH_PINCES_AVANT_PRISE)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesAvantFerme(boolean wait) {
        setPositionBatch(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_AVANT).get(IConstantesServosNerell.POS_BATCH_PINCES_AVANT_FERME), wait);
    }

    public void pinceAvantFerme(int index, boolean wait) {
        int[] config = IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_AVANT).get(IConstantesServosNerell.POS_BATCH_PINCES_AVANT_FERME)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesAvantOuvert(boolean wait) {
        setPositionBatch(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_AVANT).get(IConstantesServosNerell.POS_BATCH_PINCES_AVANT_OUVERT), wait);
    }

    public void pinceAvantOuvert(int index, boolean wait) {
        int[] config = IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_AVANT).get(IConstantesServosNerell.POS_BATCH_PINCES_AVANT_OUVERT)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesArriereFerme(boolean wait) {
        setPositionBatch(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_ARRIERE).get(IConstantesServosNerell.POS_BATCH_PINCES_ARRIERE_FERME), wait);
    }

    public void pinceArriereFerme(int index, boolean wait) {
        int[] config = IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_ARRIERE).get(IConstantesServosNerell.POS_BATCH_PINCES_ARRIERE_FERME)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    public void pincesArriereOuvert(boolean wait) {
        setPositionBatch(IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_ARRIERE).get(IConstantesServosNerell.POS_BATCH_PINCES_ARRIERE_OUVERT), wait);
    }

    public void pinceArriereOuvert(int index, boolean wait) {
        int[] config = IConstantesServosNerell.BATCH_CONFIG.get(IConstantesServosNerell.BATCH_PINCES_ARRIERE).get(IConstantesServosNerell.POS_BATCH_PINCES_ARRIERE_OUVERT)[index];
        setPosition((byte) config[0], config[1], wait);
    }

    //*******************************************//
    //* Autres                                  *//
    //*******************************************//

    public void controlBatteryVolts() {
        final double tension = getTension();
        if (tension < IConstantesUtiles.SEUIL_BATTERY_VOLTS && tension > 0) {
            log.warn("La tension de la carte sd21 est en dessous du seuil : {} < {}", tension, IConstantesUtiles.SEUIL_BATTERY_VOLTS);
            ioService.disableAlim12VPuissance();
            ioService.disableAlim5VPuissance();
        }
    }

    public double getTension() {
        return servos.getTension();
    }

}
