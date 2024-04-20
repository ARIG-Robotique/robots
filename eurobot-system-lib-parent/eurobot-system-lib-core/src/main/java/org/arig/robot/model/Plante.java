package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Plante extends Point {

    public enum ID {
        STOCK_NORD_OUEST,
        STOCK_NORD,
        STOCK_NORD_EST,

        STOCK_SUD_EST,
        STOCK_SUD,
        STOCK_SUD_OUEST
    }

    private final ID id;
    private final TypePlante type;
    private boolean dansPot; // la plante est dans un pot
    @JsonIgnore
    private boolean blocking; // génère un masque sur la pathfinder
    @JsonIgnore
    private Long existence;

    public Plante(TypePlante type) {
        this.id = null;
        this.type = type;
        this.blocking = false;
    }

    public Plante(TypePlante type, boolean dansPot) {
        this.id = null;
        this.type = type;
        this.dansPot = dansPot;
        this.blocking = false;
    }

    public Plante(ID id, TypePlante type, double x, double y, boolean blocking) {
        super(x, y);
        this.id = id;
        this.type = type;
        this.blocking = blocking;
    }

    public Plante(ID id, TypePlante type, double x, double y, Long existence) {
        super(x, y);
        this.id = id;
        this.type = type;
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

    public Plante clone() {
        Plante newPlante = new Plante(id, type, getX(), getY(), blocking);
        newPlante.dansPot = this.dansPot;
        newPlante.existence = this.existence;
        return newPlante;
    }

    @Override
    public String toString() {
        return "Plante{" + "x=" + getX() + ",y=" + getY() + ",type=" + type + "}";
    }
}
