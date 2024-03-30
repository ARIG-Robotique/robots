package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.AbstractServos;

import java.util.Map;

@Slf4j
public abstract class AbstractCommonRobotServosService extends AbstractServosService {

    public static final String BRAS_AVANT_GAUCHE_EPAULE = "Bras avant gauche épaule";
    public static final String BRAS_AVANT_GAUCHE_COUDE = "Bras avant gauche coude";
    public static final String BRAS_AVANT_GAUCHE_POIGNET = "Bras avant gauche poignet";
    public static final String BRAS_AVANT_GAUCHE_PINCE = "Bras avant gauche pince";

    public static final String BRAS_AVANT_CENTRE_EPAULE = "Bras avant centre épaule";
    public static final String BRAS_AVANT_CENTRE_COUDE = "Bras avant centre coude";
    public static final String BRAS_AVANT_CENTRE_POIGNET = "Bras avant centre poignet";
    public static final String BRAS_AVANT_CENTRE_PINCE = "Bras avant centre pince";

    public static final String BRAS_AVANT_DROIT_EPAULE = "Bras avant droit épaule";
    public static final String BRAS_AVANT_DROIT_COUDE = "Bras avant droit coude";
    public static final String BRAS_AVANT_DROIT_POIGNET = "Bras avant droit poignet";
    public static final String BRAS_AVANT_DROIT_PINCE = "Bras avant droit pince";

    public static final String BRAS_ARRIERE_GAUCHE_EPAULE = "Bras arrière gauche épaule";
    public static final String BRAS_ARRIERE_GAUCHE_COUDE = "Bras arrière gauche coude";
    public static final String BRAS_ARRIERE_GAUCHE_POIGNET = "Bras arrière gauche poignet";
    public static final String BRAS_ARRIERE_GAUCHE_PINCE = "Bras arrière gauche pince";

    public static final String BRAS_ARRIERE_CENTRE_EPAULE = "Bras arrière centre épaule";
    public static final String BRAS_ARRIERE_CENTRE_COUDE = "Bras arrière centre coude";
    public static final String BRAS_ARRIERE_CENTRE_POIGNET = "Bras arrière centre poignet";
    public static final String BRAS_ARRIERE_CENTRE_PINCE = "Bras arrière centre pince";

    public static final String BRAS_ARRIERE_DROIT_EPAULE = "Bras arrière droit épaule";
    public static final String BRAS_ARRIERE_DROIT_COUDE = "Bras arrière droit coude";
    public static final String BRAS_ARRIERE_DROIT_POIGNET = "Bras arrière droit poignet";
    public static final String BRAS_ARRIERE_DROIT_PINCE = "Bras arrière droit pince";

    protected static final String BLOQUE_PLANTE_AVANT_GAUCHE = "Bloque plante avant gauche";
    protected static final String BLOQUE_PLANTE_AVANT_CENTRE = "Bloque plante avant centre";
    protected static final String BLOQUE_PLANTE_AVANT_DROIT = "Bloque plante avant droit";

    protected static final String PANNEAU_SOLAIRE_ROUE = "Panneau solaire roue";
    protected static final String PANNEAU_SOLAIRE_SKI = "Panneau solaire ski";

    protected static final String PORTE_POT = "Porte pot";
    protected static final String PORTE_POT_GLISSIERE = "Porte pot glissière";

    protected static final String POS_INIT = "Init";
    protected static final String POS_FERME = "Fermé";
    protected static final String POS_OUVERT = "Ouvert";
    protected static final String POS_PRISE = "Prise";
    protected static final String POS_BAS = "Bas";
    protected static final String POS_HAUT = "Haut";
    protected static final String POS_RENTRE = "Rentré";
    protected static final String POS_SORTI = "Sorti";

    protected static final String GROUP_BRAS_AVANT_GAUCHE = "Bras avant gauche";
    protected static final String GROUP_BRAS_AVANT_CENTRE = "Bras avant centre haut";
    protected static final String GROUP_BRAS_AVANT_DROIT = "Bras avant droit";
    protected static final String GROUP_BRAS_ARRIERE_GAUCHE = "Bras arrière gauche";
    protected static final String GROUP_BRAS_ARRIERE_CENTRE = "Bras arrière centre";
    protected static final String GROUP_BRAS_ARRIERE_DROIT = "Bras arrière droit";
    protected static final String GROUP_PINCE_AVANT = "Pince avant";
    protected static final String GROUP_PINCE_ARRIERE = "Pince arrière";
    protected static final String GROUP_PANNEAU_SOLAIRE = "Panneau solaire";
    protected static final String GROUP_BLOQUE_PLANTE_AVANT = "Bloque plantes avant";
    protected static final String GROUP_PORTE_POT = "Porte Pot magnetique";

    protected static final byte GROUP_BRAS_AVANT_GAUCHE_ID = 1;
    protected static final byte GROUP_BRAS_AVANT_CENTRE_ID = 2;
    protected static final byte GROUP_BRAS_AVANT_DROIT_ID = 3;
    protected static final byte GROUP_BRAS_ARRIERE_GAUCHE_ID = 4;
    protected static final byte GROUP_BRAS_ARRIERE_CENTRE_ID = 5;
    protected static final byte GROUP_BRAS_ARRIERE_DROIT_ID = 6;
    protected static final byte GROUP_PINCE_AVANT_ID = 7;
    protected static final byte GROUP_PINCE_ARRIERE_ID = 8;
    protected static final byte GROUP_PANNEAU_SOLAIRE_ID = 9;
    protected static final byte GROUP_BLOQUE_PLANTE_AVANT_ID = 10;
    protected static final byte GROUP_PORTE_POT_ID = 11;

    protected AbstractCommonRobotServosService(AbstractServos servoDevice, AbstractServos... servoDevices) {
        super(servoDevice, servoDevices);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        setPosition(BRAS_AVANT_GAUCHE_POIGNET, POS_INIT, false);
        setPosition(BRAS_AVANT_GAUCHE_COUDE, POS_INIT, false);
        setPosition(BRAS_AVANT_GAUCHE_EPAULE, POS_INIT, false);
        //setPosition(BRAS_AVANT_GAUCHE_PINCE, POS_INIT, false);

        setPosition(BRAS_AVANT_CENTRE_POIGNET, POS_INIT, false);
        setPosition(BRAS_AVANT_CENTRE_COUDE, POS_INIT, false);
        setPosition(BRAS_AVANT_CENTRE_EPAULE, POS_INIT, false);
        //setPosition(BRAS_AVANT_CENTRE_PINCE, POS_INIT, false);

        setPosition(BRAS_AVANT_DROIT_POIGNET, POS_INIT, false);
        setPosition(BRAS_AVANT_DROIT_COUDE, POS_INIT, false);
        setPosition(BRAS_AVANT_DROIT_EPAULE, POS_INIT, false);
        //setPosition(BRAS_AVANT_DROIT_PINCE, POS_INIT, false);

        setPosition(BRAS_ARRIERE_GAUCHE_POIGNET, POS_INIT, false);
        setPosition(BRAS_ARRIERE_GAUCHE_COUDE, POS_INIT, false);
        setPosition(BRAS_ARRIERE_GAUCHE_EPAULE, POS_INIT, false);
        //setPosition(BRAS_ARRIERE_GAUCHE_PINCE, POS_INIT, false);

        setPosition(BRAS_ARRIERE_CENTRE_POIGNET, POS_INIT, false);
        setPosition(BRAS_ARRIERE_CENTRE_COUDE, POS_INIT, false);
        setPosition(BRAS_ARRIERE_CENTRE_EPAULE, POS_INIT, false);
        //setPosition(BRAS_ARRIERE_CENTRE_PINCE, POS_INIT, false);

        setPosition(BRAS_ARRIERE_DROIT_POIGNET, POS_INIT, false);
        setPosition(BRAS_ARRIERE_DROIT_COUDE, POS_INIT, false);
        setPosition(BRAS_ARRIERE_DROIT_EPAULE, POS_INIT, false);
        //setPosition(BRAS_ARRIERE_DROIT_PINCE, POS_INIT, false);

        setPosition(PORTE_POT, POS_BAS, false);
        setPosition(PORTE_POT_GLISSIERE, POS_RENTRE, false);

        groupePanneauFerme(false);
        groupePinceArriereFerme(false);
        groupePinceAvantFerme(false);
        groupeBloquePlanteFerme(false);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//

    public void groupePinceAvantOuvert(boolean wait) {
        setPositionBatch(GROUP_PINCE_AVANT, POS_OUVERT, wait);
    }
    public void groupePinceAvantFerme(boolean wait) {
        setPositionBatch(GROUP_PINCE_AVANT, POS_FERME, wait);
    }

    public void groupePinceArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_PINCE_ARRIERE, POS_OUVERT, wait);
    }
    public void groupePinceArriereFerme(boolean wait) {
        setPositionBatch(GROUP_PINCE_ARRIERE, POS_FERME, wait);
    }

    public void groupePanneauOuvert(boolean wait) {
        setPositionBatch(GROUP_PANNEAU_SOLAIRE, POS_OUVERT, wait);
    }
    public void groupePanneauFerme(boolean wait) {
        setPositionBatch(GROUP_PANNEAU_SOLAIRE, POS_FERME, wait);
    }

    public void groupeBloquePlanteOuvert(boolean wait) {
        setPositionBatch(GROUP_BLOQUE_PLANTE_AVANT, POS_OUVERT, wait);
    }
    public void groupeBloquePlanteFerme(boolean wait) {
        setPositionBatch(GROUP_BLOQUE_PLANTE_AVANT, POS_FERME, wait);
    }
    public void groupeBloquePlantePrise(boolean wait) {
        setPositionBatch(GROUP_BLOQUE_PLANTE_AVANT, POS_PRISE, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void setPanneauSolaireRoueOuvert(boolean wait) {
        setPosition(PANNEAU_SOLAIRE_ROUE, POS_OUVERT, wait);
    }

    public void setPanneauSolaireRoueFerme(boolean wait) {
        setPosition(PANNEAU_SOLAIRE_ROUE, POS_FERME, wait);
    }

    public void setPanneauSolaireSkiOuvert(boolean wait) {
        setPosition(PANNEAU_SOLAIRE_SKI, POS_OUVERT, wait);
    }

    public void setPanneauSolaireSkiFerme(boolean wait) {
        setPosition(PANNEAU_SOLAIRE_SKI, POS_FERME, wait);
    }

    public void setPortePotOuvert(boolean wait) {
        setPosition(PORTE_POT, POS_OUVERT, wait);
    }

    public void setPortePotFerme(boolean wait) {
        setPosition(PORTE_POT, POS_FERME, wait);
    }

    public void setPortePotGlissiereOuvert(boolean wait) {
        setPosition(PORTE_POT_GLISSIERE, POS_OUVERT, wait);
    }

    public void setPortePotGlissiereFerme(boolean wait) {
        setPosition(PORTE_POT_GLISSIERE, POS_FERME, wait);
    }

    public void brasAvantGauchePinceOuvert(boolean wait) {
        setPosition(BRAS_AVANT_GAUCHE_PINCE, POS_OUVERT, wait);
    }

    public void brasAvantGauchePinceFerme(boolean wait) {
        setPosition(BRAS_AVANT_GAUCHE_PINCE, POS_FERME, wait);
    }

    public void brasAvantCentrePinceOuvert(boolean wait) {
        setPosition(BRAS_AVANT_CENTRE_PINCE, POS_OUVERT, wait);
    }

    public void brasAvantCentrePinceFerme(boolean wait) {
        setPosition(BRAS_AVANT_CENTRE_PINCE, POS_FERME, wait);
    }

    public void brasAvantDroitPinceOuvert(boolean wait) {
        setPosition(BRAS_AVANT_DROIT_PINCE, POS_OUVERT, wait);
    }

    public void brasAvantDroitPinceFerme(boolean wait) {
        setPosition(BRAS_AVANT_DROIT_PINCE, POS_FERME, wait);
    }

    public void brasArriereGauchePinceOuvert(boolean wait) {
        setPosition(BRAS_ARRIERE_GAUCHE_PINCE, POS_OUVERT, wait);
    }

    public void brasArriereGauchePinceFerme(boolean wait) {
        setPosition(BRAS_ARRIERE_GAUCHE_PINCE, POS_FERME, wait);
    }

    public void brasArriereCentrePinceOuvert(boolean wait) {
        setPosition(BRAS_ARRIERE_CENTRE_PINCE, POS_OUVERT, wait);
    }

    public void brasArriereCentrePinceFerme(boolean wait) {
        setPosition(BRAS_ARRIERE_CENTRE_PINCE, POS_FERME, wait);
    }

    public void brasArriereDroitPinceOuvert(boolean wait) {
        setPosition(BRAS_ARRIERE_DROIT_PINCE, POS_OUVERT, wait);
    }

    public void brasAvantGauche(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_AVANT_GAUCHE_EPAULE, a1, BRAS_AVANT_GAUCHE_COUDE, a2, BRAS_AVANT_GAUCHE_POIGNET, a3), speed);
    }

    public void brasAvantCentre(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_AVANT_CENTRE_EPAULE, a1, BRAS_AVANT_CENTRE_COUDE, a2, BRAS_AVANT_CENTRE_POIGNET, a3), speed);
    }

    public void brasAvantDroit(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_AVANT_DROIT_EPAULE, a1, BRAS_AVANT_DROIT_COUDE, a2, BRAS_AVANT_DROIT_POIGNET, a3), speed);
    }

    public void brasArriereGauche(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_ARRIERE_GAUCHE_EPAULE, a1, BRAS_ARRIERE_GAUCHE_COUDE, a2, BRAS_ARRIERE_GAUCHE_POIGNET, a3), speed);
    }

    public void brasArriereCentre(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_ARRIERE_CENTRE_EPAULE, a1, BRAS_ARRIERE_CENTRE_COUDE, a2, BRAS_ARRIERE_CENTRE_POIGNET, a3), speed);
    }

    public void brasArriereDroit(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_ARRIERE_DROIT_EPAULE, a1, BRAS_ARRIERE_DROIT_COUDE, a2, BRAS_ARRIERE_DROIT_POIGNET, a3), speed);
    }

}
