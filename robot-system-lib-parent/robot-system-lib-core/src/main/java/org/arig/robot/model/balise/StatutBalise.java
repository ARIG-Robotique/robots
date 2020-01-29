package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatutBalise implements Serializable {

    DetectionResult detection;

    public boolean detectionOk() {
        return detection != null;
    }

}
