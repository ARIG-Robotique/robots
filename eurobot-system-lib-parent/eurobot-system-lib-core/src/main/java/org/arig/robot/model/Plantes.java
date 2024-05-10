package org.arig.robot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Plantes implements Iterable<Plante> {

    // delta en mm pour considérer que deux  sont les mêmes
    private static final int DELTA = 50;

    @Getter
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

    @Setter
    @Accessors(fluent = true, chain = true)
    private Team team;

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

//    public void priseStock(Plante.ID id) {
//        priseStock(id, StockPlantes.Status.EMPTY);
//    }

    public void priseStock(Plante.ID id, StockPlantes.Status status) {
        assert status != StockPlantes.Status.FULL;
        stocks.stream().filter(s -> s.getId() == id).findFirst().ifPresent(stock -> {
            if (stock.getStatus() == StockPlantes.Status.PARTIAL) {
                log.info("[rs] Stock plantes {} {}", id, StockPlantes.Status.EMPTY);
                stock.setStatus(StockPlantes.Status.EMPTY);
            } else {
                log.info("[rs] Stock plantes {} {}", id, status);
                stock.setStatus(status);
            }
        });
        plantes.removeIf(plante -> plante.getId() == id);
    }

    public StockPlantes stock(Plante.ID id) {
        return stocks.stream().filter(s -> s.getId() == id).findFirst().get();
    }

    public Stream<StockPlantes> stocksPresents(StockPlantes.Status status) {
        assert status != StockPlantes.Status.EMPTY;
        return stocks.stream()
                .filter(s -> {
                    if (status == StockPlantes.Status.FULL) {
                        return s.isFull();
                    } else {
                        return !s.isEmpty();
                    }
                })
                .filter(s -> s.getTimevalid() < System.currentTimeMillis() - 2000);
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

    public StockPlantes getClosestStock(Point point, StockPlantes.Status status) {
        assert status != StockPlantes.Status.EMPTY;
        int malus = 500;

        return stocksPresents(status)
                .min(Comparator.comparing(s -> {
                    double dst = s.distance(point);
                    if (team == Team.JAUNE) {
                        if (s.getId() == Plante.ID.STOCK_NORD_OUEST || s.getId() == Plante.ID.STOCK_SUD_OUEST) {
                            dst += malus;
                        }
                    } else {
                        if (s.getId() == Plante.ID.STOCK_NORD_EST || s.getId() == Plante.ID.STOCK_SUD_EST) {
                            dst += malus;
                        }
                    }
                    return dst;
                }))
                .orElse(null);
    }
}
