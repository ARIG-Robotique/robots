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

    // Zones d'exclusion d'action
}
