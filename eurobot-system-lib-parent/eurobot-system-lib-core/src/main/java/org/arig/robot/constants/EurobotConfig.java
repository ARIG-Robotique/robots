package org.arig.robot.constants;

public interface EurobotConfig {

  // Durée du match
  int matchTimeMs = 100000;

  // Temps écoulés
  int pamiStartRemainingTimeMs = 15300;

  // Temps restants
  int validRetourBackstageRemainingTime = 10000;
  int validPriseRemainingTime = 20000;
  int validPriseAdverseRemainingTime = 30000;
  int validPriseDeuxFacesPleineRemainingTime = 30000;
  int validDeposeDeuxFacesNonPleineRemainingTime = 30000;
  int validDeposeRemainingTime = 15000;
  int validEchappementRemainingTime = 10000;

  // Valeurs min / max en mm des axes du repères
  int tableWidth = 3000;
  int tableHeight = 2000;
  int tableBorder = 50;

  int offsetPriseGradin = 270;
  int offsetDeposeGradin = 300;

  int rang1Coord = 120;
  int rang2Coord = 270;
  int rang3Coord = 420;
  int rang4Coord = 570;

  // Noms des actions
  String ACTION_RETOUR_BACKSTAGE = "Retour backstage";
  String ACTION_DEPOSE_BANDEROLLE = "Dépose banderole";

  // Actions d'échappement
  String ACTION_DEPOSE_GRADIN_PREFIX = "Depose ";
  String ACTION_PRISE_GRADIN_BRUT_PREFIX = "Prise ";
  String ACTION_ECHAPPEMENT_ARUCO_PREFIX = "Echap. Aruco ";

  int PATHFINDER_COLONE_SIZE = 500;
}
