package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetConfigInfos implements Serializable {
    boolean exit;
    int team;
    int strategy;
    boolean doubleDepose;
    boolean safeAvoidance;
    boolean startCalibration;
    boolean modeManuel;
    boolean skipCalageBordure;
    boolean updatePhoto;
    boolean etalonnageBalise;
    int[][] posEcueil;
    int[][] posBouees;
}
