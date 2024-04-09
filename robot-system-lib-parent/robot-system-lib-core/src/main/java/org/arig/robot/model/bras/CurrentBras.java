package org.arig.robot.model.bras;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class CurrentBras implements Serializable {
    public final double a1;
    public final double a2;
    public final double a3;
    public final int x;
    public final int y;
    public final int a;
    public final boolean invertA1;

    public CurrentBras(AnglesBras angles, PointBras point) {
        this(angles.a1, angles.a2, angles.a3, point.x, point.y, point.a, point.invertA1);
    }
}
