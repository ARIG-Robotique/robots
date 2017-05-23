package org.arig.robot.system.avoiding;

import org.arig.robot.model.Point;
import org.arig.robot.model.Rectangle;

import java.util.List;

/**
 * @author gdepuille on 13/05/15.
 */
public interface IAvoidingService {

    /**
     * Execution du système d'évittement
     */
    void process();

    List<Point> getDetectedPointsMmCapteurs();
    List<Point> getDetectedPointsMmLidar();

    List<Rectangle> getCollisionsShape();
}
