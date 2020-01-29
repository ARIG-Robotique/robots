package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectionResult implements Serializable {

    private DirectionGirouette direction;

}
