package org.arig.robot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Echantillon extends Point {
    public enum ID {
        SITE_ECHANTILLONS_JAUNE,
        SITE_ECHANTILLONS_VIOLET,
        SITE_FOUILLE_JAUNE,
        SITE_FOUILLE_VIOLET
    }

    private final ID id;
    private final CouleurEchantillon couleur;
    private boolean blocking; // génère un masque sur la pathfinder
    private Long existence;

    public Echantillon(ID id, CouleurEchantillon couleur, double x, double y, boolean blocking) {
        super(x, y);
        this.id = id;
        this.couleur = couleur;
        this.blocking = blocking;
    }

    public Echantillon(ID id, CouleurEchantillon couleur, double x, double y, Long existence) {
        super(x, y);
        this.id = id;
        this.couleur = couleur;
        this.blocking = false;
        this.existence = existence;
    }

    public void setPt(Point point) {
        setX(point.getX());
        setY(point.getY());
    }

    public boolean isReal() {
        return existence == null || (System.currentTimeMillis() - existence) >= 5000;
    }

    public Echantillon clone() {
        Echantillon newEchantillon = new Echantillon(id, couleur, getX(), getY(), blocking);
        newEchantillon.existence = this.existence;
        return newEchantillon;
    }

    @Override
    public String toString() {
        return "Echantillon{" + "x=" + getX() + ",y=" + getY() + ",couleur=" + couleur + "}";
    }
}
