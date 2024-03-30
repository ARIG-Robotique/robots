package org.arig.robot.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AireDeDepose {

    public static final int MAX_DEPOSE = 6;

    final Plante[] rang1 = {null, null, null};
    final Plante[] rang2 = {null, null, null};

    public void addRang1(final int position, final Plante plante) {
        rang1[position] = plante;
    }

    public void addRang2(final int position, final Plante plante) {
        rang2[position] = plante;
    }

    public int score() {
        AtomicInteger points = new AtomicInteger(0);

        Consumer<Plante> consumer = plante -> {
            if (plante == null) {
                return;
            }
            if (plante.isDansPot()) {
                points.addAndGet(4);
            } else if (plante.getType() == TypePlante.RESISTANTE) {
                points.addAndGet(3);
            }
        };

        Stream.of(rang1).forEach(consumer);
        Stream.of(rang2).forEach(consumer);
        return points.get();
    }

    public AireDeDepose clone() {
        AireDeDepose c = new AireDeDepose();
        for (int i = 0; i < MAX_DEPOSE / 2; i++) {
            c.rang1[i] = rang1[i] == null ? null : rang1[i].clone();
            c.rang2[i] = rang2[i] == null ? null : rang2[i].clone();
        }
        return c;
     }
}
