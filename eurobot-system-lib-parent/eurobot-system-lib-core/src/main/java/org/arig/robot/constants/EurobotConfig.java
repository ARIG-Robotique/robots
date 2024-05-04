package org.arig.robot.constants;

public interface EurobotConfig {

    // Durée du match
    int matchTimeMs = 100000;

    // Temps écoulés
    int baliseElapsedTimeMs = 50000;
    int pamiStartElapsedTimeMs = 90000;

    // Temps restants
    int validRetourSiteDeChargeRemainingTimeNerell = 10000;

    // Durée de validités
    int validTimeEchappement = 20000;
    int validTimePrisePots = 40000;
    int validTimePrisePlantes = 25000;

    // Valeurs min / max en mm des axes du repères
    int tableWidth = 3000;
    int tableHeight = 2000;
    int tableBorder = 50;

    // Options de match
    String PREFERE_PANNEAUX = "Prefere panneaux";
    String ACTIVE_VOL_AU_SOL = "Active vol au sol";
    String ACTIVE_VOL_JARDINIERES = "Active vol jardinières";

    // Noms des actions
    String ACTION_PRISE_SITE_DE_PLANTES = "Prise site de plantes";
    String ACTION_RETOUR_SITE_DE_CHARGE = "Retour site de charge";
    String ACTION_PANNEAU_SOLAIRE_EQUIPE = "Panneau solaire équipe";
    String ACTION_PANNEAU_SOLAIRE_COMMUN = "Panneau solaire commun";
    String ACTION_PRISE_STOCK_POTS = "Prise stock pots";

    // Actions d'échappement
    String ACTION_ECHAPPEMENT_ARUCO_PREFIX = "Echappement Aruco ";

    int PATHFINDER_FLEUR_SIZE = 500;
    int PATHFINDER_STOCK_POTS_SIZE = 600;
    int PATHFINDER_STOCK_PLANTES_SIZE = 580;
}
