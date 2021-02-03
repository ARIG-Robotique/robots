package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.communication.balise.enums.EPresenceBouee;
import org.arig.robot.model.communication.balise.enums.ECouleurDetectee;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatutBalise implements Serializable {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoueeDetectee {
        private ECouleurDetectee col;
        private int[] pos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectionResult implements Serializable {
        private EDirectionGirouette girouette;
        private ECouleurDetectee[] ecueilEquipe;
        private ECouleurDetectee[] ecueilAdverse;
        private EPresenceBouee[] bouees;
        private BoueeDetectee[] hautFond;
    }

    boolean cameraReady;

    DetectionResult detection;

    public boolean detectionOk() {
        return detection != null;
    }

}
