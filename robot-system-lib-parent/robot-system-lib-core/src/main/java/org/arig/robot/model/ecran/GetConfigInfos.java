package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetConfigInfos implements Serializable {
    boolean exit;
    boolean twoRobots;
    int team;
    int strategy;
    boolean doubleDepose;
    boolean deposePartielle;
    boolean safeAvoidance;
    boolean startCalibration;
    boolean modeManuel;
    boolean skipCalageBordure;
    boolean updatePhoto;
    boolean etalonnageBalise;
    boolean etalonnageOk;
    int[][] posEcueil;
    int[][] posBouees;
}
