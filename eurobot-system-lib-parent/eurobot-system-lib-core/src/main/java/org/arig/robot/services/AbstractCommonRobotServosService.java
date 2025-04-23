package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.AbstractServos;

import java.util.Map;

@Slf4j
public abstract class AbstractCommonRobotServosService extends AbstractServosService {

    public static final String TIROIR_AVANT = "Tiroir avant";
    public static final String BEC_AVANT = "Bec avant";
    public static final String ASCENSEUR_AVANT = "Ascenseur avant";
    public static final String PINCE_AVANT_GAUCHE = "Pince avant gauche";
    public static final String DOIGT_AVANT_GAUCHE = "Doigt avant gauche";
    public static final String PINCE_AVANT_DROIT = "Pince avant droit";
    public static final String DOIGT_AVANT_DROIT = "Doigt avant droit";
    public static final String BLOCK_COLONNE_AVANT_GAUCHE = "Block colonne avant gauche";
    public static final String BLOCK_COLONNE_AVANT_DROIT = "Block colonne avant droit";

    public static final String TIRROIR_ARRIERE = "Tiroir arrière";
    public static final String BEC_ARRIERE = "Bec arrière";
    public static final String ASCENSEUR_ARRIERE = "Ascenseur arrière";
    public static final String PINCE_ARRIERE_GAUCHE = "Pince arrière gauche";
    public static final String DOIGT_ARRIERE_GAUCHE = "Doigt arrière gauche";
    public static final String PINCE_ARRIERE_DROIT = "Pince arrière droit";
    public static final String DOIGT_ARRIERE_DROIT = "Doigt arrière droit";
    public static final String BLOCK_COLONNE_ARRIERE_GAUCHE = "Block colonne arrière gauche";
    public static final String BLOCK_COLONNE_ARRIERE_DROIT = "Block colonne arrière droit";

    protected static final String POS_INIT = "Init";
    protected static final String POS_FERME = "Fermé";
    protected static final String POS_OUVERT = "Ouvert";
    protected static final String POS_OUVERT_PRISE = "Ouvert prise";
    protected static final String POS_OUVERT_DEPOSE = "Ouvert dépose";
    protected static final String POS_STOCK = "Stock";

    protected static final String GROUP_PINCES_AVANT = "Pinces avant";
    protected static final String GROUP_PINCES_ARRIERE = "Pinces arrière";
    protected static final String GROUP_DOIGTS_AVANT = "Doigts avant";
    protected static final String GROUP_DOIGTS_ARRIERE = "Doigts arrière";
    protected static final String GROUP_BLOCK_COLONNE_AVANT = "Block colonne avant";
    protected static final String GROUP_BLOCK_COLONNE_ARRIERE = "Block colonne arrière";

    protected static final byte GROUP_PINCES_AVANT_ID = 1;
    protected static final byte GROUP_PINCES_ARRIERE_ID = 2;
    protected static final byte GROUP_DOIGTS_AVANT_ID = 3;
    protected static final byte GROUP_DOIGTS_ARRIERE_ID = 4;
    protected static final byte GROUP_BLOCK_COLONNE_AVANT_ID = 5;
    protected static final byte GROUP_BLOCK_COLONNE_ARRIERE_ID = 6;

    protected AbstractCommonRobotServosService(AbstractServos servoDevice, AbstractServos... servoDevices) {
        super(servoDevice, servoDevices);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        // Batch
        setPositionBatch(GROUP_PINCES_AVANT, POS_INIT, false);
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_INIT, false);

        groupeDoigtsAvantFerme(false);
        groupeDoigtsArriereFerme(false);
        groupeBlockColonneAvantFerme(false);
        groupeBlockColonneArriereFerme(false);

        // Servo
        setPosition(TIROIR_AVANT, POS_INIT, false);
        setPosition(TIRROIR_ARRIERE, POS_INIT, false);
        setPosition(BEC_AVANT, POS_INIT, false);
        setPosition(BEC_ARRIERE, POS_INIT, false);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//
    public void groupePincesAvantPrise(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_OUVERT_PRISE, wait);
    }
    public void groupePincesAvantDepose(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_OUVERT_DEPOSE, wait);
    }
    public void groupePincesAvantFerme(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_FERME, wait);
    }

    public void groupePincesArrierePrise(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_OUVERT_PRISE, wait);
    }
    public void groupePincesArriereDepose(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_OUVERT_DEPOSE, wait);
    }
    public void groupePincesArriereFerme(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_FERME, wait);
    }

    public void groupeDoigtsAvantOuvert(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_AVANT, POS_OUVERT, wait);
    }
    public void groupeDoigtsAvantFerme(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_AVANT, POS_FERME, wait);
    }

    public void groupeDoigtsArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_ARRIERE, POS_OUVERT, wait);
    }
    public void groupeDoigtsArriereFerme(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_ARRIERE, POS_FERME, wait);
    }

    public void groupeBlockColonneAvantOuvert(boolean wait) {
        setPositionBatch(GROUP_BLOCK_COLONNE_AVANT, POS_OUVERT, wait);
    }
    public void groupeBlockColonneAvantFerme(boolean wait) {
        setPositionBatch(GROUP_BLOCK_COLONNE_AVANT, POS_FERME, wait);
    }

    public void groupeBlockColonneArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_BLOCK_COLONNE_ARRIERE, POS_OUVERT, wait);
    }
    public void groupeBlockColonneArriereFerme(boolean wait) {
        setPositionBatch(GROUP_BLOCK_COLONNE_ARRIERE, POS_FERME, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void tirroirAvantOuvert(boolean wait) {
        setPosition(TIROIR_AVANT, POS_OUVERT, wait);
    }
    public void tirroirAvantStock(boolean wait) {
        setPosition(TIROIR_AVANT, POS_STOCK, wait);
    }

    public void tirroirArriereOuvert(boolean wait) {
        setPosition(TIRROIR_ARRIERE, POS_OUVERT, wait);
    }
    public void tirroirArriereStock(boolean wait) {
        setPosition(TIRROIR_ARRIERE, POS_STOCK, wait);
    }

    public void becAvantOuvert(boolean wait) {
        setPosition(BEC_AVANT, POS_OUVERT, wait);
    }
    public void becAvantFerme(boolean wait) {
        setPosition(BEC_AVANT, POS_FERME, wait);
    }

    public void becArriereOuvert(boolean wait) {
        setPosition(BEC_AVANT, POS_OUVERT, wait);
    }
    public void becArriereFerme(boolean wait) {
        setPosition(BEC_AVANT, POS_FERME, wait);
    }
}
