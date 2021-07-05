package org.arig.robot.constants;

import java.awt.*;

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
    int validRetourPortRemainingTimeNerell = 15000;
    int validRetourPortRemainingTimeOdin = 10000;

    // Valeurs min / max en mm des axes du repères
    int tableWidth = 3000;
    int tableHeight = 2000;
    int tableBorder = 50;

    int pathFindingTailleBouee = 420;
    int pathFindingTailleBoueePort = 480;

    int deltaCapteurCouleur = 42;

    // Noms des actions
    String ACTION_PRISE_BOUEE_NORD = "Bouées nord";
    String ACTION_PRISE_BOUEE_SUD = "Bouées sud";
    String ACTION_PRISE_BOUEE_PREFIX = "Bouée ";
    String ACTION_PHARE = "Phare";
    String ACTION_MANCHE_A_AIR = "Manche à air";
    String ACTION_DEPOSE_PETIT_PORT = "Dépose petit port";
    String ACTION_DEPOSE_GRAND_PORT = "Dépose grand port";
    String ACTION_DEPOSE_GRAND_PORT_ROUGE = "Dépose grand port rouge";
    String ACTION_DEPOSE_GRAND_PORT_VERT = "Dépose grand port vert";
    String ACTION_RETOUR_AU_PORT_PREFIX = "Retour au port ";
    String ACTION_ATTENTE_GRAND_PORT_PREFIX = "Attente grand port ";
    String ACTION_ATTENTE_CENTRE_PREFIX = "Attente centre ";
    String ACTION_ANTIBLOCAGE_PORT = "Antiblocage port";
    String ACTION_ECUEIL_EQUIPE = "Ecueil equipe";
    String ACTION_NETTOYAGE_GRAND_PORT = "Netoyage grand port";
    String ACTION_NETTOYAGE_PETIT_PORT = "Netoyage petit port";

    Rectangle ZONE_GRAND_PORT_BLEU = new Rectangle(0, 850, 900, 700);
    Rectangle ZONE_GRAND_PORT_JAUNE = new Rectangle(2400, 850, 900, 700);
    Rectangle ZONE_ECUEIL_EQUIPE_BLEU = new Rectangle(0, 0, 800, 500);
    Rectangle ZONE_ECUEIL_EQUIPE_JAUNE = new Rectangle(2200, 0, 800, 500);
    Rectangle ZONE_ECUEIL_COMMUN_ADVERSE_BLEU = new Rectangle(1850, 1600, 600, 400);
    Rectangle ZONE_ECUEIL_COMMUN_ADVERSE_JAUNE = new Rectangle(550, 1600, 600, 400);
}
