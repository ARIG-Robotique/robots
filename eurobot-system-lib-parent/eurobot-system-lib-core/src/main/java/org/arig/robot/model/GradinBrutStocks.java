package org.arig.robot.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class GradinBrutStocks implements Iterable<GradinBrut> {

    public final List<GradinBrut> data = Arrays.asList(
        new GradinBrut(GradinBrut.ID.JAUNE_RESERVE, 825, 1725, true),
        new GradinBrut(GradinBrut.ID.JAUNE_HAUT_GAUCHE, 75, 1325, true),
        new GradinBrut(GradinBrut.ID.JAUNE_MILIEU_CENTRE, 1100, 950, false),
        new GradinBrut(GradinBrut.ID.JAUNE_BAS_GAUCHE, 75, 400, true),
        new GradinBrut(GradinBrut.ID.JAUNE_BAS_CENTRE, 775, 250, false),

        new GradinBrut(GradinBrut.ID.BLEU_RESERVE, 2175, 1725, true),
        new GradinBrut(GradinBrut.ID.BLEU_HAUT_DROITE, 2925, 1325, true),
        new GradinBrut(GradinBrut.ID.BLEU_MILIEU_CENTRE, 1900, 950, false),
        new GradinBrut(GradinBrut.ID.BLEU_BAS_DROITE, 2925, 400, true),
        new GradinBrut(GradinBrut.ID.BLEU_BAS_CENTRE, 2225, 250, false)
    );

    @Override
    public Iterator<GradinBrut> iterator() {
        return data.iterator();
    }

    public Stream<GradinBrut> stocksPresents() {
        return data.stream()
                .filter(s -> s.present() && !s.bloque());
    }

    public GradinBrut getClosest(Point point) {
        return stocksPresents()
                .min(Comparator.comparing(stock -> stock.distance(point)))
                .orElse(null);
    }

    public GradinBrut get(GradinBrut.ID id) {
        return data.stream()
                .filter(s -> s.id() == id)
                .findFirst()
                .get();
    }
}
