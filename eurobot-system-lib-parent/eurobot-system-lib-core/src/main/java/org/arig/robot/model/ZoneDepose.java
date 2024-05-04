package org.arig.robot.model;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZoneDepose {

    final List<Plante> data = new ArrayList<>();

    public void add(Plante[] bras) {
        for (int i = 0; i < bras.length; i++) {
            data.add(bras[i].clone());
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int score() {
        return score(false);
    }

    protected int score(boolean jardiniere) {
        return data.stream()
                .map(plante -> {
                    int points = 0;
                    if (plante.getType() != TypePlante.AUCUNE) {
                        if (plante.isDansPot()) {
                            points += 4;
                        } else if (jardiniere || plante.getType() == TypePlante.RESISTANTE) {
                            points += 3;
                        } else if (plante.getType() == TypePlante.INCONNU) {
                            points += 1; // 1 chance sur 3 que ça rapporte 3 points
                        }
                        if (jardiniere) {
                            points += 1;
                        }
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
