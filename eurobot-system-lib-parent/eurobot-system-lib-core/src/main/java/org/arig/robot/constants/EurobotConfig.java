package org.arig.robot.constants;

public interface EurobotConfig {

    // Durée du match //
    int matchTimeMs = 100000;

    // Temps écoulé pour la balise
    int baliseElapsedTimeMs = 50000;

    // Valeurs min / max en mm des axes du repères
    int tableWidth = 3000;
    int tableHeight = 2000;
    int tableBorder = 50;

    // Config retour site de fouille
    int validRetourSiteDeFouilleRemainingTimeNerell = 15000;
    int validRetourSiteDeFouilleRemainingTimeOdin = 12000;

    // Config activation vitrine sans statuette
    int validActivationVitrineRemainingTime = 30000;

    // Options de match
    String TROIS_DANS_ABRI = "3 dans abri";

    // Noms des actions
    String ACTION_DECOUVERTE_CARRE_FOUILLE = "Découverte carré de fouille";
    String ACTION_STATUETTE_REPLIQUE = "Statuette / Replique";
    String ACTION_DEPOSE_STATUETTE = "Dépose statuette";
    String ACTION_RETOUR_SITE_DE_FOUILLE_PREFIX = "Retour site de fouille ";

    // Actions d'échappement
    String ACTION_ECHAPPEMENT_COIN_TABLE_PREFIX = "Echappement coin de table ";

    // Actions temporaire sans bras
    String TMP_ACTION_POUSSETTE_SITE_ECHANTILLONS_EQUIPE = "Poussette site echantillons équipe";

    // Zones d'exclusion d'action
}
