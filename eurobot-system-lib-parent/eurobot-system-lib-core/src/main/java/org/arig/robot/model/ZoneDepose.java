package org.arig.robot.model;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ZoneDepose {

    public final boolean jardiniere;

    final Plante[] data = {null, null, null, null, null, null};

    public void setFromBras(BrasListe.Contenu[] bras) {
        for (int i = 0; i < bras.length; i++) {
            Plante plante = null;
            switch (bras[i]) {
                case PLANTE_FRAGILE:
                case PLANTE_INCONNU:
                case PLANTE_RESISTANTE:
                case PLANTE_DANS_POT:
                    plante = new Plante(bras[i].getTypePlante(), bras[i] == BrasListe.Contenu.PLANTE_DANS_POT);
                    break;
            }
            data[i] = plante;
        }
    }

    public void addRang1(final int position, final Plante plante) {
        data[position] = plante;
    }

    public void addRang2(final int position, final Plante plante) {
        data[position + 3] = plante;
    }

    public boolean isEmpty() {
        return Stream.of(data).allMatch(Objects::isNull);
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
