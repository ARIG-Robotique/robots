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

    protected static final String BRAS_MESURE_CARRE_FOUILLE = "Bras mesure carré fouille";
    protected static final String BRAS_POUSSE_CARRE_FOUILLE = "Bras pousse carré fouille";

    protected static final String PINCE_STATUETTE = "Pince statuette";
    protected static final String POUSSE_REPLIQUE = "Pousse replique";

    protected static final String TRAPPE_ARRIERE_GAUCHE = "Trappe arrière gauche";
    protected static final String TRAPPE_ARRIERE_CENTRE = "Trappe arrière centre";
    protected static final String TRAPPE_ARRIERE_DROITE = "Trappe arrière droite";

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
    protected static final String GROUP_TRAPPES_LATERAL_ARRIERE = "Trappes lateral arrière";

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        // TODO: Bras haut et bas @home
        brasMesureCarreFouilleFerme(false);
        brasPousseCarreFouilleFerme(false);
        pinceStatuetteFerme(false);
        pousseRepliqueFerme(false);
        trappeArriereCentreFerme(false);
        groupTrappesLateralArriereFerme(false);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isBrasMesureCarreFouilleFerme() {
        return isInPosition(BRAS_MESURE_CARRE_FOUILLE, POS_FERME);
    }

    public boolean isBrasMesureCarreFouilleMesure() {
        return isInPosition(BRAS_MESURE_CARRE_FOUILLE, POS_MESURE);
    }

    public boolean isBrasPousseCarreFouilleFerme() {
        return isInPosition(BRAS_POUSSE_CARRE_FOUILLE, POS_FERME);
    }

    public boolean isBrasPousseCarreFouillePoussette() {
        return isInPosition(BRAS_POUSSE_CARRE_FOUILLE, POS_POUSSETTE);
    }

    public boolean isPinceStatuetteFerme() {
        return isInPosition(PINCE_STATUETTE, POS_FERME);
    }

    public boolean isPinceStatuettePriseDepose() {
        return isInPosition(PINCE_STATUETTE, POS_PRISE_DEPOSE);
    }

    public boolean isTrappeArriereCentreFerme(){
        return isInPosition(TRAPPE_ARRIERE_CENTRE, POS_FERME);
    }

    public boolean isTrappeArriereCentreOuvert() {
        return isInPosition(TRAPPE_ARRIERE_CENTRE, POS_OUVERT);
    }

    public boolean isTrappeArriereGaucheFerme(){
        return isInPosition(TRAPPE_ARRIERE_GAUCHE, POS_FERME);
    }

    public boolean isTrappeArriereGaucheOuvert(){
        return isInPosition(TRAPPE_ARRIERE_GAUCHE, POS_OUVERT);
    }

    public boolean isTrappeArriereDroiteFerme(){
        return isInPosition(TRAPPE_ARRIERE_DROITE, POS_FERME);
    }

    public boolean isTrappeArriereDroiteOuvert(){
        return isInPosition(TRAPPE_ARRIERE_DROITE, POS_OUVERT);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//

    public void groupTrappesLateralArriereOuvert(boolean wait) {
        setPositionBatch(GROUP_TRAPPES_LATERAL_ARRIERE, POS_OUVERT, wait);
    }

    public void groupTrappesLateralArriereFerme(boolean wait) {
        setPositionBatch(GROUP_TRAPPES_LATERAL_ARRIERE, POS_FERME, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void pinceStatuetteFerme(boolean wait) {
        setPosition(PINCE_STATUETTE, POS_FERME, wait);
    }

    public void pinceStatuettePriseDepose(boolean wait) {
        setPosition(PINCE_STATUETTE, POS_PRISE_DEPOSE, wait);
    }

    public void pousseRepliqueFerme(boolean wait) {
        setPosition(POUSSE_REPLIQUE, POS_FERME, wait);
    }

    public void pousseRepliquePoussette(boolean wait) {
        setPosition(POUSSE_REPLIQUE, POS_POUSSETTE, wait);
    }

    public void brasMesureCarreFouilleFerme(boolean wait) {
        setPosition(BRAS_MESURE_CARRE_FOUILLE, POS_FERME, wait);
    }

    public void brasMesureCarreFouilleMesure(boolean wait) {
        setPosition(BRAS_MESURE_CARRE_FOUILLE, POS_MESURE, wait);
    }

    public void brasPousseCarreFouilleFerme(boolean wait) {
        setPosition(BRAS_POUSSE_CARRE_FOUILLE, POS_FERME, wait);
    }

    public void brasPousseCarreFouillePoussette(boolean wait) {
        setPosition(BRAS_POUSSE_CARRE_FOUILLE, POS_POUSSETTE, wait);
    }

    public void trappeArriereCentreFerme(boolean wait) {
        setPosition(TRAPPE_ARRIERE_CENTRE, POS_FERME, wait);
    }

    public void trappeArriereCentreOuvert(boolean wait) {
        setPosition(TRAPPE_ARRIERE_CENTRE, POS_OUVERT, wait);
    }
}
