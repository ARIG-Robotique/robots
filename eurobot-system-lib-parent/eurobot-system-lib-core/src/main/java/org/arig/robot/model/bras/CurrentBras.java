package org.arig.robot.model.bras;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class CurrentBras implements Serializable {
    public final PositionBras state;
    public final int a1;
    public final int a2;
    public final int a3;
    public final int x;
    public final int y;
    public final int a;

    public CurrentBras(PositionBras state, AnglesBras angles, PointBras point) {
        this(state, angles.a1, angles.a2, angles.a3, point.x, point.y, point.a);
    }
}
