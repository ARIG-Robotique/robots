package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class PointBras implements Serializable {
    public int x;
    public int y;
    public int a;
    public Boolean invertA1;
}
