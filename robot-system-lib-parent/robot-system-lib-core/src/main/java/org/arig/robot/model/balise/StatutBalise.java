package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.communication.balise.enums.CouleurDetectee;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatutBalise implements Serializable {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectionResult implements Serializable {
        private DirectionGirouette direction;
        private CouleurDetectee[] ecueil;
        private CouleurDetectee[] bouees;
    }

    boolean cameraReady;

    DetectionResult detection;

    public boolean detectionOk() {
        return detection != null;
    }

}
