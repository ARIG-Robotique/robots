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

    // Options de match
    String STATUETTE_PRESENTE = "Statuette présente";
    String VITRINE_PRESENTE = "Vitrine présente";

    // Noms des actions
    String ACTION_DECOUVERTE_CARRE_FOUILLE = "Découverte carré de fouille";
    String ACTION_DEPOSE_REPLIQUE = "Dépose replique";
    String ACTION_RECUPERATION_STATUETTE = "Recupération statuette";
    String ACTION_DEPOSE_STATUETTE = "Dépose statuette";
    String ACTION_RETOUR_EN_ZONE = "Retour en zone";

    // Zones d'exclusion d'action
}
