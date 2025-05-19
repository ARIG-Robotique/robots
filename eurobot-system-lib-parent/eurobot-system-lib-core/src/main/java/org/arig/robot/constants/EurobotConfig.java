package org.arig.robot.constants;

public interface EurobotConfig {

    // Durée du match
    int matchTimeMs = 100000;

    // Temps écoulés
    int pamiStartRemainingTimeMs = 15300;

    // Temps restants
    int validRetourBackstageRemainingTimeNerell = 10000;
    int validPriseAdverseRemainingTimeNerell = 30000;

    // Durée de validités
    int validTimeEchappement = 20000;
    int validTimeConstruction = 20000;
    int validTimePrise = 25000;

    // Valeurs min / max en mm des axes du repères
    int tableWidth = 3000;
    int tableHeight = 2000;
    int tableBorder = 50;

    int offsetPriseGradin = 270;
    int offsetDeposeGradin = 300;

    int rang1Coord = 100;
    int rang2Coord = 250;
    int rang3Coord = 400;

    // Noms des actions
    String ACTION_RETOUR_BACKSTAGE = "Retour backstage";

    // Actions d'échappement
    String ACTION_DEPOSE_GRADIN_PREFIX = "Depose ";
    String ACTION_PRISE_GRADIN_BRUT_PREFIX = "Prise ";
    String ACTION_ECHAPPEMENT_ARUCO_PREFIX = "Echap. Aruco ";

    int PATHFINDER_COLONE_SIZE = 500;
}
