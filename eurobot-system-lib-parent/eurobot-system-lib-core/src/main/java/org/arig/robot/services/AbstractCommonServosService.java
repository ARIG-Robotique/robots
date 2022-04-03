package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCommonServosService extends AbstractServosService {

    protected static final String BRAS_BAS_EPAULE = "Bras bas épaule";
    protected static final String BRAS_BAS_COUDE = "Bras bas coude";
    protected static final String BRAS_BAS_POIGNET = "Bras bas poignet";

    protected static final String BRAS_HAUT_EPAULE = "Bras haut épaule";
    protected static final String BRAS_HAUT_COUDE = "Bras haut coude";
    protected static final String BRAS_HAUT_POIGNET = "Bras haut poignet";

    protected static final String CARRE_FOUILLE_OHMMETRE = "Ohmmetre";
    protected static final String CARRE_FOUILLE_POUSSOIR = "Poussoir carré fouille";

    protected static final String FOURCHE_STATUETTE = "Fourche statuette";

    protected static final String LANGUE = "Langue";
    protected static final String MOUSTACHE_GAUCHE = "Moustache gauche";
    protected static final String MOUSTACHE_DROITE = "Moustache droite";

    protected static final String POS_REPOS = "Repos";
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

    protected static final byte GROUP_BRAS_BAS_ID = 1;
    protected static final byte GROUP_BRAS_HAUT_ID = 2;
    protected static final byte GROUP_BRAS_MESURE_ID = 3;
    protected static final byte GROUP_STATUETTE_ID = 4;
    protected static final byte GROUP_ARRIERE_ID = 5;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        setPosition(BRAS_BAS_POIGNET, POS_REPOS, false);
        setPosition(BRAS_BAS_COUDE, POS_REPOS, false);
        setPosition(BRAS_BAS_EPAULE, POS_REPOS, false);
        setPosition(BRAS_HAUT_POIGNET, POS_REPOS, false);
        setPosition(BRAS_HAUT_COUDE, POS_REPOS, false);
        setPosition(BRAS_HAUT_EPAULE, POS_REPOS, false);
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

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void fourcheStatuetteFerme(boolean wait) {
        setPosition(FOURCHE_STATUETTE, POS_FERME, wait);
    }

    public void fourcheStatuettePriseDepose(boolean wait) {
        setPosition(FOURCHE_STATUETTE, POS_PRISE_DEPOSE, wait);
    }

    public void carreFouilleOhmmetreFerme(boolean wait) {
        setPosition(CARRE_FOUILLE_OHMMETRE, POS_FERME, wait);
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
}
