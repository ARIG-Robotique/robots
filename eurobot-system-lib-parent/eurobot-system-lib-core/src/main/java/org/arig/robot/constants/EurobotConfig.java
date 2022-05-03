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

    int validActivationVitrineRemainingTime = 30000; // Activation vitrine sans statuette

    int invalidPriseEchantillonRemainingTime = 30000;

    // Options de match
    String TROIS_DANS_ABRI = "3 dans abri";
    String STOCKAGE_ABRI = "Stockage abri";

    // Noms des actions
    String ACTION_DECOUVERTE_CARRE_FOUILLE = "Découverte carré de fouille";
    String ACTION_ABRI_CHANTIER = "Abri de chantier";
    String ACTION_DEPOSE_STATUETTE = "Dépose statuette";
    String ACTION_RETOUR_SITE_DE_FOUILLE_PREFIX = "Retour site de fouille ";
    String ACTION_PRISE_DISTRIB_EQUIPE = "Prise distrib. équipe";
    String ACTION_PRISE_DISTRIB_COMMUN_EQUIPE = "Prise distrib. commun équipe";
    String ACTION_PRISE_DISTRIB_COMMUN_ADVERSE = "Prise distrib. commun adverse";

    // Actions d'échappement
    String ACTION_ECHAPPEMENT_COIN_TABLE_PREFIX = "Echappement coin de table ";

    // Actions temporaire sans bras
    String TMP_ACTION_POUSSETTE_SITE_ECHANTILLONS_EQUIPE = "Poussette site echantillons équipe";

    // Zones d'exclusion d'action

    // nombre de points de déposes comptés lors d'une prise
    int PTS_DEPOSE_PRISE = 2;

    // Taille d'un echantillon
    int ECHANTILLON_SIZE = 150;
}
