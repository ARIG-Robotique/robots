package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.AbstractServos;

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

    public static final String TIROIR_ARRIERE = "Tiroir arrière";
    public static final String BEC_ARRIERE = "Bec arrière";
    public static final String ASCENSEUR_ARRIERE = "Ascenseur arrière";
    public static final String PINCE_ARRIERE_GAUCHE = "Pince arrière gauche";
    public static final String DOIGT_ARRIERE_GAUCHE = "Doigt arrière gauche";
    public static final String PINCE_ARRIERE_DROIT = "Pince arrière droit";
    public static final String DOIGT_ARRIERE_DROIT = "Doigt arrière droit";
    public static final String BLOCK_COLONNE_ARRIERE_GAUCHE = "Block colonne arrière gauche";
    public static final String BLOCK_COLONNE_ARRIERE_DROIT = "Block colonne arrière droit";

    protected static final String POS_FERME = "Fermé";
    protected static final String POS_OUVERT = "Ouvert";
    protected static final String POS_SERRE = "Serré";
    protected static final String POS_LACHE = "Lache";
    protected static final String POS_PRISE = "Prise";
    protected static final String POS_DEPOSE = "Dépose";
    protected static final String POS_PRISE_SOL = "Prise sol";
    protected static final String POS_STOCK = "Stock";
    protected static final String POS_SPLIT = "Split";
    protected static final String POS_HAUT = "Haut";
    protected static final String POS_BAS = "Bas";
    protected static final String POS_ETAGE_2 = "Etage 2";
    protected static final String POS_REPOS = "Repos";

    protected static final int FULL_SPEED = 0;
    protected static final int HALF_SPEED = 30;

    protected static final String GROUP_INDIVIDUAL_AVANT = "Individuel avant";
    protected static final String GROUP_INDIVIDUAL_ARRIERE = "Individuel arrière";
    protected static final String GROUP_PINCES_AVANT = "Pinces avant";
    protected static final String GROUP_PINCES_ARRIERE = "Pinces arrière";
    protected static final String GROUP_DOIGTS_AVANT = "Doigts avant";
    protected static final String GROUP_DOIGTS_ARRIERE = "Doigts arrière";
    protected static final String GROUP_BLOCK_COLONNE_AVANT = "Block colonne avant";
    protected static final String GROUP_BLOCK_COLONNE_ARRIERE = "Block colonne arrière";

    protected static final byte GROUP_INDIVIDUAL_AVANT_ID = 1;
    protected static final byte GROUP_INDIVIDUAL_ARRIERE_ID = 2;
    protected static final byte GROUP_PINCES_AVANT_ID = 3;
    protected static final byte GROUP_PINCES_ARRIERE_ID = 4;
    protected static final byte GROUP_DOIGTS_AVANT_ID = 5;
    protected static final byte GROUP_DOIGTS_ARRIERE_ID = 6;
    protected static final byte GROUP_BLOCK_COLONNE_AVANT_ID = 7;
    protected static final byte GROUP_BLOCK_COLONNE_ARRIERE_ID = 8;

    protected AbstractCommonRobotServosService(AbstractServos servoDevice, AbstractServos... servoDevices) {
        super(servoDevice, servoDevices);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes(boolean endMatch) {
        // Batch
        groupePincesAvantRepos(false);
        groupePincesArriereRepos(false);
        groupeDoigtsAvantFerme(false);
        groupeDoigtsArriereFerme(false);
        groupeBlockColonneAvantOuvert(false);
        groupeBlockColonneArriereOuvert(false);

        // Servo individuels
        tiroirAvantStock(false);
        tiroirArriereStock(false);
        if (!endMatch) {
        ascenseurAvantRepos(false);
        ascenseurArriereRepos(false);
        } else {
            ascenseurAvantStock(false);
            ascenseurArriereStock(false);
        }
        becAvantRepos(false);
        becArriereRepos(false);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//
    public void groupePincesAvantOuvert(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_OUVERT, wait);
    }
    public void groupePincesAvantPrise(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_PRISE, wait);
    }
    public void groupePincesAvantPriseSol(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_PRISE_SOL, wait);
    }
    public void groupePincesAvantStock(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_STOCK, wait);
    }
    public void groupePincesAvantRepos(boolean wait) {
        setPositionBatch(GROUP_PINCES_AVANT, POS_REPOS, wait);
    }

    public void groupePincesArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_OUVERT, wait);
    }
    public void groupePincesArrierePrise(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_PRISE, wait);
    }
    public void groupePincesArrierePriseSol(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_PRISE_SOL, wait);
    }
    public void groupePincesArriereStock(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_STOCK, wait);
    }
    public void groupePincesArriereRepos(boolean wait) {
        setPositionBatch(GROUP_PINCES_ARRIERE, POS_REPOS, wait);
    }

    public void groupeDoigtsAvantOuvert(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_AVANT, POS_OUVERT, wait);
    }
    public void groupeDoigtsAvantLache(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_AVANT, POS_LACHE, wait);
    }
    public void groupeDoigtsAvantSerre(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_AVANT, POS_SERRE, wait);
    }
    public void groupeDoigtsAvantFerme(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_AVANT, POS_FERME, wait);
    }

    public void groupeDoigtsArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_ARRIERE, POS_OUVERT, wait);
    }
    public void groupeDoigtsArriereLache(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_ARRIERE, POS_LACHE, wait);
    }
    public void groupeDoigtsArriereSerre(boolean wait) {
        setPositionBatch(GROUP_DOIGTS_ARRIERE, POS_SERRE, wait);
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

    public void tiroirAvantDepose(boolean wait) {
        tiroirAvantDepose(wait, false);
    }

    public void tiroirAvantDepose(boolean wait, boolean halfSpeed) {
        setPositionAndSpeed(TIROIR_AVANT, POS_DEPOSE, halfSpeed ? HALF_SPEED : FULL_SPEED, wait);
    }
    public void tiroirAvantPrise(boolean wait) {
        tiroirAvantPrise(wait, false);
    }
    public void tiroirAvantPrise(boolean wait, boolean halfSpeed) {
        setPositionAndSpeed(TIROIR_AVANT, POS_PRISE, halfSpeed ? HALF_SPEED : FULL_SPEED, wait);
    }
    public void tiroirAvantStock(boolean wait) {
        tiroirAvantStock(wait, false);
    }
    public void tiroirAvantStock(boolean wait, boolean halfSpeed) {
        setPositionAndSpeed(TIROIR_AVANT, POS_STOCK, halfSpeed ? HALF_SPEED : FULL_SPEED, wait);
    }

    public void tiroirArriereDepose(boolean wait) {
        tiroirArriereDepose(wait, false);
    }
    public void tiroirArriereDepose(boolean wait, boolean halfSpeed) {
        setPositionAndSpeed(TIROIR_ARRIERE, POS_DEPOSE, halfSpeed ? HALF_SPEED : FULL_SPEED, wait);
    }
    public void tiroirArrierePrise(boolean wait) {
        tiroirArrierePrise(wait, false);
    }
    public void tiroirArrierePrise(boolean wait, boolean halfSpeed) {
        setPositionAndSpeed(TIROIR_ARRIERE, POS_PRISE, halfSpeed ? HALF_SPEED : FULL_SPEED, wait);
    }
    public void tiroirArriereStock(boolean wait) {
        tiroirArriereStock(wait, false);
    }
    public void tiroirArriereStock(boolean wait, boolean halfSpeed) {
        setPositionAndSpeed(TIROIR_ARRIERE, POS_STOCK, halfSpeed ? HALF_SPEED : FULL_SPEED, wait);
    }

    public void becAvantOuvert(boolean wait) {
        setPosition(BEC_AVANT, POS_OUVERT, wait);
    }
    public void becAvantFerme(boolean wait) {
        setPosition(BEC_AVANT, POS_FERME, wait);
    }
    public void becAvantRepos(boolean wait) {
        setPosition(BEC_AVANT, POS_REPOS, wait);
    }

    public void becArriereOuvert(boolean wait) {
        setPosition(BEC_ARRIERE, POS_OUVERT, wait);
    }
    public void becArriereFerme(boolean wait) {
        setPosition(BEC_ARRIERE, POS_FERME, wait);
    }
    public void becArriereRepos(boolean wait) {
        setPosition(BEC_ARRIERE, POS_REPOS, wait);
    }

    public void ascenseurAvantHaut(boolean wait) {
        setPosition(ASCENSEUR_AVANT, POS_HAUT, wait);
    }
    public void ascenseurAvantSplit(boolean wait) {
        setPosition(ASCENSEUR_AVANT, POS_SPLIT, wait);
    }
    public void ascenseurAvantStock(boolean wait) {
        setPosition(ASCENSEUR_AVANT, POS_STOCK, wait);
    }
    public void ascenseurAvantBas(boolean wait) {
        setPosition(ASCENSEUR_AVANT, POS_BAS, wait);
    }
    public void ascenseurAvantRepos(boolean wait) {
        setPosition(ASCENSEUR_AVANT, POS_REPOS, wait);
    }
    public void ascenseurAvantEtage2(boolean wait) {
        setPosition(ASCENSEUR_AVANT, POS_ETAGE_2, wait);
    }

    public void ascenseurArriereHaut(boolean wait) {
        setPosition(ASCENSEUR_ARRIERE, POS_HAUT, wait);
    }
    public void ascenseurArriereSplit(boolean wait) {
        setPosition(ASCENSEUR_ARRIERE, POS_SPLIT, wait);
    }
    public void ascenseurArriereStock(boolean wait) {
        setPosition(ASCENSEUR_ARRIERE, POS_STOCK, wait);
    }
    public void ascenseurArriereBas(boolean wait) {
        setPosition(ASCENSEUR_ARRIERE, POS_BAS, wait);
    }
    public void ascenseurArriereRepos(boolean wait) {
        setPosition(ASCENSEUR_ARRIERE, POS_REPOS, wait);
    }
    public void ascenseurArriereEtage2(boolean wait) {
        setPosition(ASCENSEUR_ARRIERE, POS_ETAGE_2, wait);
    }
}
