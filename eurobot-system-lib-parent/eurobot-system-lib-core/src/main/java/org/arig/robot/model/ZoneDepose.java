package org.arig.robot.model;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZoneDepose {

    public final boolean jardiniere;

    final List<Plante> data = new ArrayList<>();

    public void addFromBras(BrasListe.Contenu[] bras) {
        for (int i = 0; i < bras.length; i++) {
            switch (bras[i]) {
                case PLANTE_FRAGILE:
                case PLANTE_INCONNU:
                case PLANTE_RESISTANTE:
                case PLANTE_DANS_POT:
                    data.add(new Plante(bras[i].getTypePlante(), bras[i] == BrasListe.Contenu.PLANTE_DANS_POT));
                    break;
            }
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int score() {
        return data.stream()
                .map(plante -> {
                    int points = 0;
                    if (plante.isDansPot()) {
                        points += 4;
                    } else if (jardiniere || plante.getType() == TypePlante.RESISTANTE) {
                        points += 3;
                    }
                    if (jardiniere) {
                        points += 1;
                    }
                    return points;
                })
                .sorted(Comparator.reverseOrder())
                .limit(6)
                .mapToInt(s -> s)
                .sum();
    }

    @Override
    public String toString() {
        Function<Plante, String> mapper = p -> p != null ? p.getType().name() + (p.isDansPot() ? " (dans pot)" : "") : "EMPTY";
        return data.stream().map(mapper).collect(Collectors.joining(","));
    }
}
