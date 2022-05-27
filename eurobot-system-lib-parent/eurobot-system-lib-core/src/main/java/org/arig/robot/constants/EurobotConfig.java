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
    int validRetourSiteDeFouilleRemainingTimeNerell = 10000;
    int validRetourSiteDeFouilleRemainingTimeOdin = 8000;

    int validTimeEchappement = 20000;

    int validDeposeIfElementInStockRemainingTime = matchTimeMs / 2; // Activation de la dépose si au moins un élément en stock

    // Options de match
    String REVERSE_CARRE_FOUILLE = "Force reverse carres f.";
    String DOUBLE_DEPOSE_GALERIE = "Force dbl. dans galerie";
    String PRISE_UNITAIRE = "Prise unitaire";
    String SITE_DE_FOUILLE = "Site de fouille";

    // Noms des actions
    String ACTION_DECOUVERTE_CARRE_FOUILLE = "Découverte carré de fouille";
    String ACTION_ABRI_CHANTIER = "Abri de chantier";
    String ACTION_DEPOSE_STATUETTE = "Dépose statuette / vitrine";
    String ACTION_DEPOSE_GALERIE = "Dépose galerie";
    String ACTION_RETOUR_SITE_DE_FOUILLE_PREFIX = "Retour site de fouille ";
    String ACTION_DEPOSE_CAMPEMENT_PREFIX = "Dépose campement ";
    String ACTION_RETOUR_CAMPEMENT_PREFIX = "Retour campement ";
    String ACTION_PRISE_DISTRIB_EQUIPE = "Prise distrib. équipe";
    String ACTION_PRISE_DISTRIB_COMMUN_EQUIPE = "Prise distrib. commun équipe";
    String ACTION_PRISE_DISTRIB_COMMUN_ADVERSE = "Prise distrib. commun adverse";
    String ACTION_PRISE_SITE_FOUILLE_EQUIPE = "Prise site de fouille équipe";

    String ACTION_PRISE_ECHANTILLON_UNITAIRE = "Prise echantillon";

    String ACTION_PRISE_ECHANTILLONS_SITE_EQUIPE = "Prise échantillons site équipe";

    String ACTION_PRISE_ECHANTILLON_DISTRIBUTEUR_CAMPEMENT = "Prise echantillon campement";

    // Actions d'échappement
    String ACTION_ECHAPPEMENT_ARUCO_PREFIX = "Echappement Aruco ";
    String ACTION_ECHAPPEMENT_ABRI_CHANTIER = "Echappement abri chantier";
    String ACTION_ECHAPPEMENT_VITRINE = "Echappement vitrine";


    // Zones d'exclusion d'action

    // nombre de points de déposes comptés lors d'une prise
    int PTS_DEPOSE_PRISE = 2;

    // Taille d'un echantillon
    int ECHANTILLON_SIZE = 150;

    int PATHFINDER_ECHANTILLON_SIZE = 500;
    int PATHFINDER_SITE_FOUILLE_SIZE = 700;

    boolean ECHANGE_PRISE = true;

}
