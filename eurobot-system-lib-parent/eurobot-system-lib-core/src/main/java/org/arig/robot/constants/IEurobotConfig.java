package org.arig.robot.constants;

public interface IEurobotConfig {

    // Durée du match //
    int matchTimeMs = 100000;

    // Temps écoulé pour la balise
    int baliseElapsedTimeMs = 50000;

    // Temps restant pour le déclenchement du pavillon
    int pavillonRemainingTimeMs = 5000;

    // Temps pour rendre le port prioritaire
    int invalidPriseRemainingTime = 18000;

    // Temps pour rendre valide le retour au port
    int validRetourPortRemainingTime = 10000;

    // Valeurs min / max en mm des axes du repères
    int tableWidth = 3000;
    int tableHeight = 2000;
    int tableBorder = 50;

    int pathFindingTailleBouee = 420;
    int pathFindingTailleBoueePort = 480;

}
