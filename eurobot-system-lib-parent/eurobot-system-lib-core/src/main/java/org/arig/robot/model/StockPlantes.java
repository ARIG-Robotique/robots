package org.arig.robot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class StockPlantes implements Iterable<Plante> {

    // delta en mm pour considérer que deux  sont les mêmes
    private static final int DELTA = 50;

    private Rectangle stockNordOuest = new Rectangle(850, 2150, 300, 300);
    private Rectangle stockNord = new Rectangle(1350, 2350, 300, 300);
    private Rectangle stockNordEst = new Rectangle(1850, 2150, 300, 300);

    private Rectangle stockSudOuest = new Rectangle(850, 550, 300, 300);
    private Rectangle stockSud = new Rectangle(1350, 1350, 300, 300);
    private Rectangle stockSudEst = new Rectangle(1850, 550, 300, 300);

    @Getter
    private List<Plante> plantes = new ArrayList<>();

    @Override
    public Iterator<Plante> iterator() {
        return plantes.iterator();
    }

    public void priseStockNordOuest() {
        plantes.removeIf(plante -> plante.getId() == Plante.ID.STOCK_NORD_OUEST);
    }

    public void priseStockNord() {
        plantes.removeIf(plante -> plante.getId() == Plante.ID.STOCK_NORD);
    }

    public void priseStockNordEst() {
        plantes.removeIf(plante -> plante.getId() == Plante.ID.STOCK_NORD_EST);
    }

    public void priseStockSudOuest() {
        plantes.removeIf(plante -> plante.getId() == Plante.ID.STOCK_SUD_OUEST);
    }

    public void priseStockSud() {
        plantes.removeIf(plante -> plante.getId() == Plante.ID.STOCK_SUD);
    }

    public void priseStockSudEst() {
        plantes.removeIf(plante -> plante.getId() == Plante.ID.STOCK_SUD_EST);
    }

    public boolean isInStockNordOuest(final Point pt) {
        return stockNordOuest.contains(pt);
    }

    public boolean isInStockNord(final Point pt) {
        return stockNord.contains(pt);
    }

    public boolean isInStockNordEst(final Point pt) {
        return stockNordEst.contains(pt);
    }

    public boolean isInStockSudOuest(final Point pt) {
        return stockSudOuest.contains(pt);
    }

    public boolean isInStockSud(final Point pt) {
        return stockSud.contains(pt);
    }

    public boolean isInStockSudEst(final Point pt) {
        return stockSudEst.contains(pt);
    }

    public Optional<Plante> findPlante(final Point pt, final TypePlante c) {
        return plantes.stream()
                .filter(e -> StockPlantes.match(e, pt, c))
                .findFirst();
    }

    public Plante findPlante(final Shape polygon) {
        return plantes.stream()
                .filter(e -> polygon.contains(e.getX(), e.getY()))
                .findFirst()
                .orElse(null);
    }

    public void addPlante(final Point point, final TypePlante c) {
        final Plante.ID id;
        if (isInStockNordOuest(point)) {
            id = Plante.ID.STOCK_NORD_OUEST;
        } else if (isInStockNord(point)) {
            id = Plante.ID.STOCK_NORD;
        } else if (isInStockNordEst(point)) {
            id = Plante.ID.STOCK_NORD_EST;
        } else if (isInStockSudOuest(point)) {
            id = Plante.ID.STOCK_SUD_OUEST;
        } else if (isInStockSud(point)) {
            id = Plante.ID.STOCK_SUD;
        } else if (isInStockSudEst(point)) {
            id = Plante.ID.STOCK_SUD_EST;
        } else {
            id = null;
        }
        Plante plante = new Plante(id, c, point.getX(), point.getY(), System.currentTimeMillis());
        plante.setBlocking(id != null);
        plantes.add(plante);
    }

    public static boolean match(final Plante e, final Point pt, final TypePlante c) {
        return Math.abs(e.getX() - pt.getX()) < DELTA && Math.abs(e.getY() - pt.getY()) < DELTA;
    }
}
