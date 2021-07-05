package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetConfigInfos extends ReadWriteInfo implements Serializable {
    boolean exit;
    boolean twoRobots;
    boolean safeAvoidance;
    boolean startCalibration;
    boolean modeManuel;
    boolean skipCalageBordure;
    boolean updatePhoto;
    boolean etalonnageBalise;
    boolean etalonnageOk;
}
