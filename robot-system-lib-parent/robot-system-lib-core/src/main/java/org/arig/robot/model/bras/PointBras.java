package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class PointBras implements Serializable {
    public int x;
    public int y;
    public int a;
    public Boolean invertA1;

    /**
     * Translation du doigt par rapport à la position actuelle (l'angle est concervé)
     */
    public static PointBrasTranslated translated(int x, int y) {
        return new PointBrasTranslated(x, y);
    }

    /**
     * Rotation du poignet (le point cible n'est pas conervé)
     */
    public static PointBrasRotated rotated(int a) {
        return new PointBrasRotated(a);
    }

    public static class PointBrasTranslated extends PointBras {
        public PointBrasTranslated(int x, int y) {
            super(x, y, 0, null);
        }
    }

    public static class PointBrasRotated extends PointBras {
        public PointBrasRotated(int a) {
            super(0, 0, a, null);
        }
    }
}
