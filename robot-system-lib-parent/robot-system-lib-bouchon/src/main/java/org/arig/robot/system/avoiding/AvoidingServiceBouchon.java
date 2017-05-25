package org.arig.robot.system.avoiding;

import org.arig.robot.model.Point;
import org.arig.robot.model.Shape;

import java.util.Collections;
import java.util.List;

/**
 * @author gdepuille on 22/05/17.
 */
public class AvoidingServiceBouchon implements IAvoidingService {

    @Override
    public void process() {

    }

    @Override
    public List<Point> getDetectedPointsMmCapteurs() {
        return Collections.emptyList();
    }

    @Override
    public List<Point> getDetectedPointsMmLidar() {
        return Collections.emptyList();
    }

    @Override
    public List<Shape> getCollisionsShape() {
        return Collections.emptyList();
    }
}
