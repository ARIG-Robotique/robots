package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellServosService extends AbstractServosService {

    private static final String BRAS_BAS_EPAULE = "Bras bas épaule";
    private static final byte BRAS_BAS_EPAULE_ID = 1;
    private static final String BRAS_BAS_COUDE = "Bras bas coude";
    private static final byte BRAS_BAS_COUDE_ID = 2;
    private static final String BRAS_BAS_POIGNET = "Bras bas poignet";
    private static final byte BRAS_BAS_POIGNET_ID = 3;

    private static final String BRAS_HAUT_EPAULE = "Bras haut épaule";
    private static final byte BRAS_HAUT_EPAULE_ID = 4;
    private static final String BRAS_HAUT_COUDE = "Bras haut coude";
    private static final byte BRAS_HAUT_COUDE_ID = 5;
    private static final String BRAS_HAUT_POIGNET = "Bras haut poignet";
    private static final byte BRAS_HAUT_POIGNET_ID = 6;

    private static final String BRAS_MESURE_CARRE_FOUILLE = "Bras mesure carré fouille";
    private static final byte BRAS_MESURE_CARRE_FOUILLE_ID = 7;
    private static final String BRAS_POUSSE_CARRE_FOUILLE = "Bras pousse carré fouille";
    private static final byte BRAS_POUSSE_CARRE_FOUILLE_ID = 8;

    private static final String PINCE_STATUETTE = "Pince statuette";
    private static final byte PINCE_STATUETTE_ID = 9;
    private static final String POUSSE_REPLIQUE = "Pousse replique";
    private static final byte POUSSE_REPLIQUE_ID = 10;

    private static final String TRAPPE_ARRIERE_GAUCHE = "Trappe arrière gauche";
    private static final byte TRAPPE_ARRIERE_GAUCHE_ID = 11;
    private static final String TRAPPE_ARRIERE_CENTRE = "Trappe arrière centre";
    private static final byte TRAPPE_ARRIERE_CENTRE_ID = 12;
    private static final String TRAPPE_ARRIERE_DROITE = "Trappe arrière droite";
    private static final byte TRAPPE_ARRIERE_DROITE_ID = 13;

    private static final String POS_REPOS = "Repos";
    private static final String POS_FERME = "Fermé";
    private static final String POS_OUVERT = "Ouvert";
    private static final String POS_PRISE_DEPOSE = "Prise / dépose";
    private static final String POS_POUSSETTE = "Poussette";
    private static final String POS_MESURE = "Mesure";

    private static final String GROUP_BRAS_BAS = "Bras bas";
    private static final byte GROUP_BRAS_BAS_ID = 1;
    private static final String GROUP_BRAS_HAUT = "Bras haut";
    private static final byte GROUP_BRAS_HAUT_ID = 2;
    private static final String GROUP_BRAS_MESURE = "Bras mesure";
    private static final byte GROUP_BRAS_MESURE_ID = 3;
    private static final String GROUP_STATUETTE = "Statuette / Replique";
    private static final byte GROUP_STATUETTE_ID = 4;
    private static final String GROUP_TRAPPES_LATERAL_ARRIERE = "Trappes lateral arrière";
    private static final byte GROUP_TRAPPES_ARRIERE_ID = 5;

    public NerellServosService() {
        super();

        Servo brasBasEpaule = servo(BRAS_BAS_EPAULE_ID, BRAS_BAS_EPAULE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasBasCoude = servo(BRAS_BAS_COUDE_ID, BRAS_BAS_COUDE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasBasPoignet = servo(BRAS_BAS_POIGNET_ID, BRAS_BAS_POIGNET)
                .time(500)
                .position(POS_REPOS, 1500);
        group(GROUP_BRAS_BAS_ID, GROUP_BRAS_BAS)
                .addServo(brasBasEpaule)
                .addServo(brasBasCoude)
                .addServo(brasBasPoignet);

        Servo brasHautEpaule = servo(BRAS_HAUT_EPAULE_ID, BRAS_HAUT_EPAULE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasHautCoude = servo(BRAS_HAUT_COUDE_ID, BRAS_HAUT_COUDE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasHautPoignet = servo(BRAS_HAUT_POIGNET_ID, BRAS_HAUT_POIGNET)
                .time(500)
                .position(POS_REPOS, 1500);
        group(GROUP_BRAS_HAUT_ID, GROUP_BRAS_HAUT)
                .addServo(brasHautEpaule)
                .addServo(brasHautCoude)
                .addServo(brasHautPoignet);

        Servo brasMesureCarreFouille = servo(BRAS_MESURE_CARRE_FOUILLE_ID, BRAS_MESURE_CARRE_FOUILLE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_MESURE, 1500);
        Servo brasPousseCarreFouille = servo(BRAS_POUSSE_CARRE_FOUILLE_ID, BRAS_POUSSE_CARRE_FOUILLE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_POUSSETTE, 1500);
        group(GROUP_BRAS_MESURE_ID, GROUP_BRAS_MESURE)
                .addServo(brasMesureCarreFouille)
                .addServo(brasPousseCarreFouille);

        Servo pinceStatuette = servo(PINCE_STATUETTE_ID, PINCE_STATUETTE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_PRISE_DEPOSE, 1500);
        Servo pousseReplique = servo(POUSSE_REPLIQUE_ID, POUSSE_REPLIQUE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_POUSSETTE, 1500);
        group(GROUP_STATUETTE_ID, GROUP_STATUETTE)
                .addServo(pinceStatuette)
                .addServo(pousseReplique);

        Servo trappeArriereGauche = servo(TRAPPE_ARRIERE_GAUCHE_ID, TRAPPE_ARRIERE_GAUCHE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);
        Servo trappeArriereCentre = servo(TRAPPE_ARRIERE_CENTRE_ID, TRAPPE_ARRIERE_CENTRE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);
        Servo trappeArriereDroite = servo(TRAPPE_ARRIERE_DROITE_ID, TRAPPE_ARRIERE_DROITE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);

        group(GROUP_TRAPPES_ARRIERE_ID, GROUP_TRAPPES_LATERAL_ARRIERE)
                .addServo(trappeArriereGauche)
                .addServo(trappeArriereCentre)
                .addServo(trappeArriereDroite)
                .batch(POS_OUVERT)
                .batch(POS_FERME);
    }

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
