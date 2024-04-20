package org.arig.robot.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StocksPots implements Iterable<StockPots> {

    private final List<StockPots> data = Arrays.asList(
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

    public StockPots getClosest(Point point) {
        return data.stream()
                .filter(s -> s.isPresent() && !s.isBloque())
                .min(Comparator.comparing(stock -> stock.distance(point)))
                .orElse(null);
    }

    public Map<StockPots.ID, Boolean> gameStatus() {
        return data.stream()
                .collect(Collectors.toMap(StockPots::getId, StockPots::isPresent));
    }

    public StockPots get(StockPots.ID id) {
        return data.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .get();
    }
}
