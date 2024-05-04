package org.arig.robot.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class StocksPots implements Iterable<StockPots> {

    public final List<StockPots> data = Arrays.asList(
            new StockPots(StockPots.ID.BLEU_NORD, 35, 1390, 0),
            new StockPots(StockPots.ID.BLEU_MILIEU, 35, 610, 0),
            new StockPots(StockPots.ID.BLEU_SUD, 1000, 35, 90),
            new StockPots(StockPots.ID.JAUNE_NORD, 3000 - 35, 1390, 180),
            new StockPots(StockPots.ID.JAUNE_MILIEU, 3000 - 35, 610, 180),
            new StockPots(StockPots.ID.JAUNE_SUD, 2000, 35, 90)
    );

    @Override
    public Iterator<StockPots> iterator() {
        return data.iterator();
    }

    public Stream<StockPots> stocksPresents() {
        return data.stream()
                .filter(s -> s.isPresent() && !s.isBloque());
    }

    public StockPots getClosest(Point point) {
        return stocksPresents()
                .min(Comparator.comparing(stock -> stock.distance(point)))
                .orElse(null);
    }

    public StockPots get(StockPots.ID id) {
        return data.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .get();
    }
}
