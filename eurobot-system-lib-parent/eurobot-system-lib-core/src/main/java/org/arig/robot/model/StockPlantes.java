package org.arig.robot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static org.arig.robot.utils.ArigUtils.lerp;

@Getter
@Setter
public class StockPlantes extends Point {

    @Override
    public String toString() {
        return "StockPlantes{" +
                "id=" + id +
                ", timevalid=" + timevalid +
                ", status=" + status +
                '}';
    }

    public enum Status {
        FULL,
        PARTIAL,
        EMPTY
    }

    private final Plante.ID id;
    private final Rectangle rect;
    // instant auquel le stock a été marqué inaccessible
    private long timevalid = 0;
    private Status status = Status.FULL;

    public StockPlantes(Plante.ID id, double x, double y) {
        super(x, y);
        this.id = id;
        this.rect = Rectangle.byCenter(x, y, 300, 300);
    }

    public boolean isFull() {
        return status == Status.FULL;
    }

    public boolean isEmpty() {
        return status == Status.EMPTY;
    }

    public List<Plante> getInit() {
        List<Plante> plantes = new ArrayList<>();
        plantes.add(new Plante(
                id,
                TypePlante.RESISTANTE,
                getX(),
                getY(),
                false
        ));

        for (int i = 1; i < 6; i++) {
            double a = lerp(i, 1, 6, 0, Math.PI * 2);
            plantes.add(new Plante(
                    id,
                    i == 1 ? TypePlante.RESISTANTE : TypePlante.FRAGILE,
                    getX() + 70 * Math.cos(a),
                    getY() + 70 * Math.sin(a),
                    false
            ));
        }

        return plantes;
    }

}
