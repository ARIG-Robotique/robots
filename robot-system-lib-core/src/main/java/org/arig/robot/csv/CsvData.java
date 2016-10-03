package org.arig.robot.csv;

import lombok.Data;

/**
 * Created by gdepuille on 08/03/15.
 */
@Data
public class CsvData {

    // Position
    private double x;
    private double y;
    private double angle;

    // Codeurs infos
    private double codeurGauche;
    private double codeurDroit;
    private double codeurDistance;
    private double codeurOrient;

    // Mode asservissement
    private String modeAsserv;
    private String typeOdometrie;

    // Consigne
    private long vitesseDistance;
    private long vitesseOrient;
    private long consigneDistance;
    private long consigneOrient;

    // PIDs information
    private double setPointDistance;
    private double inputDistance;
    private double erreurDistance;
    private double sumErreurDistance;
    private double outputPidDistance;

    private double setPointOrient;
    private double inputOrient;
    private double erreurOrient;
    private double sumErreurOrient;
    private double outputPidOrient;

    // Commande moteurs
    private int cmdMoteurGauche;
    private int cmdMoteurDroit;

    // Position
    private boolean trajetAtteint;
    private boolean trajetEnApproche;
}
