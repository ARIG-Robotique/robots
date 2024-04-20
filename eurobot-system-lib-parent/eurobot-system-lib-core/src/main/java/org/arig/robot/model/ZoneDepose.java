package org.arig.robot.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ZoneDepose {

    public final boolean jardiniere;

    final Plante[] data = {null, null, null, null, null, null};

    public void addRang1(final int position, final Plante plante) {
        data[position] = plante;
    }

    public void addRang2(final int position, final Plante plante) {
        data[position + 3] = plante;
    }

    public int score() {
        int points = 0;

        for (Plante plante : data) {
            if (plante != null) {
                if (plante.isDansPot()) {
                    points += 4;
                }
                else if (jardiniere || plante.getType() == TypePlante.RESISTANTE) {
                    points += 3;
                }
                if (jardiniere) {
                    points += 1;
                }
            }
        }

        return points;
    }

    @Override
    public String toString() {
        Function<Plante, String> mapper = p -> p != null ? p.getType().name() + (p.isDansPot() ? " (dans pot)" : "") : "EMPTY";
        return Arrays.stream(data).map(mapper).collect(Collectors.joining(","));
    }
}
