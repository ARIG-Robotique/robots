package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class TransitionBras implements Serializable {

    public static final int MAX_SPEED = 70;

    public static final TransitionBras DEFAULT = new TransitionBras(MAX_SPEED, new PointBras[0]);

    private int speed;

    private PointBras[] points;

    public static TransitionBras withPoints(PointBras... points) {
        return new TransitionBras(MAX_SPEED, points);
    }

}
