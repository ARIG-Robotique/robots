package org.arig.robot.system.motion;

import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.TypeOdometrie;

/**
 * The Interface IOdometrie.
 *
 * @author gdepuille
 */
public interface IOdometrie {

    TypeOdometrie getType();

    Position getCurrentPosition();

    void updatePosition(final Point pt);
    void updatePosition(final double x, final double y);
    void updateX(final double x);
    void updateY(final double y);
    void updateAngle(final double angle);

    void calculPosition();
}
