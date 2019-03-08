package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.Point;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectionResult implements Serializable {

    List<Point> foundGreen;
    List<Point> foundRed;
    List<Point> foundBlue;

    List<Point> verifiedGreen;
    List<Point> verifiedRed;
    List<Point> verifiedBlue;

}
