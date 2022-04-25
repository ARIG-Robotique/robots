package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class AbstractCommonServosService extends AbstractServosService {

    public static final String BRAS_BAS_EPAULE = "Bras bas épaule";
    public static final String BRAS_BAS_COUDE = "Bras bas coude";
    public static final String BRAS_BAS_POIGNET = "Bras bas poignet";

    public static final String BRAS_HAUT_EPAULE = "Bras haut épaule";
    public static final String BRAS_HAUT_COUDE = "Bras haut coude";
    public static final String BRAS_HAUT_POIGNET = "Bras haut poignet";

    protected static final String CARRE_FOUILLE_OHMMETRE = "Ohmmetre";
    protected static final String CARRE_FOUILLE_POUSSOIR = "Poussoir carré fouille";

    protected static final String FOURCHE_STATUETTE = "Fourche statuette";
    protected static final String POUSSE_REPLIQUE = "Pousse replique";

    protected static final String LANGUE = "Langue";
    protected static final String MOUSTACHE_GAUCHE = "Moustache gauche";
    protected static final String MOUSTACHE_DROITE = "Moustache droite";

    protected static final String POS_INIT = "Init";
    protected static final String POS_FERME = "Fermé";
    protected static final String POS_OUVERT = "Ouvert";
    protected static final String POS_PRISE_DEPOSE = "Prise / dépose";
    protected static final String POS_POUSSETTE = "Poussette";
    protected static final String POS_MESURE = "Mesure";

    protected static final String GROUP_BRAS_BAS = "Bras bas";
    protected static final String GROUP_BRAS_HAUT = "Bras haut";
    protected static final String GROUP_BRAS_MESURE = "Bras mesure";
    protected static final String GROUP_STATUETTE = "Statuette / Replique";
    protected static final String GROUP_ARRIERE = "Arrière";
    protected static final String GROUP_MOUSTACHE = "Moustache";

    protected static final byte GROUP_BRAS_BAS_ID = 1;
    protected static final byte GROUP_BRAS_HAUT_ID = 2;
    protected static final byte GROUP_BRAS_MESURE_ID = 3;
    protected static final byte GROUP_STATUETTE_ID = 4;
    protected static final byte GROUP_ARRIERE_ID = 5;
    protected static final byte GROUP_MOUSTACHE_ID = 6;

    public boolean pousseReplique() {
        return false;
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        setPosition(BRAS_BAS_POIGNET, POS_INIT, false);
        setPosition(BRAS_BAS_COUDE, POS_INIT, false);
        setPosition(BRAS_BAS_EPAULE, POS_INIT, false);
        setPosition(BRAS_HAUT_POIGNET, POS_INIT, false);
        setPosition(BRAS_HAUT_COUDE, POS_INIT, false);
        setPosition(BRAS_HAUT_EPAULE, POS_INIT, false);
        carreFouilleOhmmetreFerme(false);
        carreFouillePoussoirFerme(false);
        fourcheStatuetteFerme(false);
        groupeArriereFerme(false);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//

    public void groupeArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_ARRIERE, POS_OUVERT, wait);
    }

    public void groupeArriereFerme(boolean wait) {
        setPositionBatch(GROUP_ARRIERE, POS_FERME, wait);
    }

    public void groupeMoustacheOuvert(boolean wait) {
        setPositionBatch(GROUP_MOUSTACHE, POS_OUVERT, wait);
    }
    public void groupeMoustacheFerme(boolean wait) {
        setPositionBatch(GROUP_MOUSTACHE, POS_FERME, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void fourcheStatuetteFerme(boolean wait) {
        setPosition(FOURCHE_STATUETTE, POS_FERME, wait);
    }

    public void fourcheStatuettePriseDepose(boolean wait) {
        setPosition(FOURCHE_STATUETTE, POS_PRISE_DEPOSE, wait);
    }

    public void pousseRepliqueFerme(boolean wait) {
        setPosition(POUSSE_REPLIQUE, POS_FERME, wait);
    }

    public void pousseRepliquePoussette(boolean wait) {
        setPosition(POUSSE_REPLIQUE, POS_POUSSETTE, wait);
    }

    public void carreFouilleOhmmetreFerme(boolean wait) {
        setPosition(CARRE_FOUILLE_OHMMETRE, POS_FERME, wait);
    }

    public void carreFouilleOhmmetreOuvert(boolean wait) {
        setPosition(CARRE_FOUILLE_OHMMETRE, POS_OUVERT, wait);
    }

    public void carreFouilleOhmmetreMesure(boolean wait) {
        setPosition(CARRE_FOUILLE_OHMMETRE, POS_MESURE, wait);
    }

    public void carreFouillePoussoirFerme(boolean wait) {
        setPosition(CARRE_FOUILLE_POUSSOIR, POS_FERME, wait);
    }

    public void carreFouillePoussoirPoussette(boolean wait) {
        setPosition(CARRE_FOUILLE_POUSSOIR, POS_POUSSETTE, wait);
    }

    public void langueFerme(boolean wait) {
        setPosition(LANGUE, POS_FERME, wait);
    }

    public void langueOuvert(boolean wait) {
        setPosition(LANGUE, POS_OUVERT, wait);
    }

    public void moustacheGaucheFerme(boolean wait) {
        setPosition(MOUSTACHE_GAUCHE, POS_FERME, wait);
    }

    public void moustacheGaucheOuvert(boolean wait) {
        setPosition(MOUSTACHE_GAUCHE, POS_OUVERT, wait);
    }

    public void moustacheDroiteFerme(boolean wait) {
        setPosition(MOUSTACHE_DROITE, POS_FERME, wait);
    }

    public void moustacheDroiteOuvert(boolean wait) {
        setPosition(MOUSTACHE_DROITE, POS_OUVERT, wait);
    }

    public void brasBas(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_BAS_EPAULE, a1, BRAS_BAS_COUDE, a2, BRAS_BAS_POIGNET, a3), speed);
    }

    public void brasHaut(int a1, int a2, int a3, int speed) {
        setAngles(Map.of(BRAS_HAUT_EPAULE, a1, BRAS_HAUT_COUDE, a2, BRAS_HAUT_POIGNET, a3), speed);
    }

}
