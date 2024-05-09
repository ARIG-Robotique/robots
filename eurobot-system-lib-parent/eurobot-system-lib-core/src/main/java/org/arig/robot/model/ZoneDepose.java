package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class ZoneDepose {

    protected final List<Plante> data = new ArrayList<>();
    private final String name;

    public void add(Plante[] plantes) {
        for (int i = 0; i < plantes.length; i++) {
            if (name != null) {
                log.info("[RS] Ajout dans {}: {}", name, plantes[i].getType());
            }
            data.add(plantes[i].clone());
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
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
