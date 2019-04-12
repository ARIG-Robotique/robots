package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.model.Point;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectionResult implements Serializable {

    List<Point> foundGreen = Collections.emptyList();
    List<Point> foundRed = Collections.emptyList();
    List<Point> foundBlue = Collections.emptyList();

    List<Point> verifiedGreen = Collections.emptyList();
    List<Point> verifiedRed = Collections.emptyList();
    List<Point> verifiedBlue = Collections.emptyList();

}
