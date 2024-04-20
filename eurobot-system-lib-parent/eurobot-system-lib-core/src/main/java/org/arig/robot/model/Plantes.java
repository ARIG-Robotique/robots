package org.arig.robot.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Plantes implements Iterable<Plante> {

    // delta en mm pour considérer que deux  sont les mêmes
    private static final int DELTA = 50;

    private final List<StockPlantes> stocks = Arrays.asList(
            new StockPlantes(Plante.ID.STOCK_NORD_OUEST, 1000, 1300),
            new StockPlantes(Plante.ID.STOCK_NORD, 1500, 1500),
            new StockPlantes(Plante.ID.STOCK_NORD_EST, 2000, 1300),
            new StockPlantes(Plante.ID.STOCK_SUD_OUEST, 1000, 700),
            new StockPlantes(Plante.ID.STOCK_SUD, 1500, 500),
            new StockPlantes(Plante.ID.STOCK_SUD_EST, 2000, 700)
    );

    @Getter
    private List<Plante> plantes = new ArrayList<>();

    @Override
    public Iterator<Plante> iterator() {
        return plantes.iterator();
    }

    public Plantes() {
        // positions prédéfinies
        for (StockPlantes stock : stocks) {
            plantes.addAll(stock.getInit());
        }
    }

    public void priseStock(Plante.ID id) {
        stocks.stream().filter(s -> s.getId() == id).findFirst().get().setPresent(false);
        plantes.removeIf(plante -> plante.getId() == id);
    }

    // TODO automatiquement marquer comme pris les stocks qui n'ont plus assez de plantes

//    public Optional<Plante> findPlante(final Point pt, final TypePlante c) {
//        return plantes.stream()
//                .filter(e -> Plantes.match(e, pt, c))
//                .findFirst();
//    }
//
//    public Plante findPlante(final Shape polygon) {
//        return plantes.stream()
//                .filter(e -> polygon.contains(e.getX(), e.getY()))
//                .findFirst()
//                .orElse(null);
//    }
//
//    public void addPlante(final Point point, final TypePlante c) {
//        final Plante.ID id;
//        if (isInStockNordOuest(point)) {
//            id = Plante.ID.STOCK_NORD_OUEST;
//        } else if (isInStockNord(point)) {
//            id = Plante.ID.STOCK_NORD;
//        } else if (isInStockNordEst(point)) {
//            id = Plante.ID.STOCK_NORD_EST;
//        } else if (isInStockSudOuest(point)) {
//            id = Plante.ID.STOCK_SUD_OUEST;
//        } else if (isInStockSud(point)) {
//            id = Plante.ID.STOCK_SUD;
//        } else if (isInStockSudEst(point)) {
//            id = Plante.ID.STOCK_SUD_EST;
//        } else {
//            id = null;
//        }
//        Plante plante = new Plante(id, c, point.getX(), point.getY(), System.currentTimeMillis());
//        plante.setBlocking(id != null);
//        plantes.add(plante);
//    }

    public static boolean match(final Plante e, final Point pt, final TypePlante c) {
        return Math.abs(e.getX() - pt.getX()) < DELTA && Math.abs(e.getY() - pt.getY()) < DELTA;
    }

    public StockPlantes getClosest(Point point) {
        return stocks.stream()
                .filter(StockPlantes::isPresent)
                .min(Comparator.comparing(stock -> stock.distance(point)))
                .orElse(null);
    }
}
