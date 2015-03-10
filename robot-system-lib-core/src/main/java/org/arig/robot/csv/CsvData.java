package org.arig.robot.csv;

import lombok.Data;

/**
 * Created by gdepuille on 08/03/15.
 */
@Data
public class CsvData {

    private double x;
    private double y;
    private double angle;

    private double codeurGauche;
    private double codeurDroit;

    private double codeurDistance;
    private double codeurOrient;

    private String modeAsserv;
    private String typeOdometrie;

    private long consigneDistance;
    private long consigneOrient;

    private double outputPidDistance;
    private double outputPidOrient;

    private long vitesseDistance;
    private long vitesseOrient;

    private double setPointDistance;
    private double setPointOrient;

    private int cmdMoteurGauche;
    private int cmdMoteurDroit;
}
