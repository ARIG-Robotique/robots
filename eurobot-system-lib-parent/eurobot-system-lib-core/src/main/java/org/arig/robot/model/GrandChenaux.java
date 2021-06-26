package org.arig.robot.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Objects;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GrandChenaux extends Chenaux {

    public enum Line {
        A, B
    }

    // un chenal contient 9 emplacements sur deux lignes de 5 et 4, en quinconce
    // il est possible d'en mettre plus que 9, qui n'auront pas de position connue

    @Override
    protected Chenaux newInstance() {
        GrandChenaux chenaux = new GrandChenaux();
        for (int i = 0; i < 9; i++) {
            chenaux.chenalRouge.set(i, null);
            chenaux.chenalVert.set(i, null);
        }
        return chenaux;
    }

    @Override
    public boolean chenalVertEmpty() {
        return chenalVert.stream().allMatch(Objects::isNull);
    }

    @Override
    public boolean chenalRougeEmpty() {
        return chenalRouge.stream().allMatch(Objects::isNull);
    }

    public void addRouge(Line line, ECouleurBouee... bouees) {
        for (int i = 0; i < bouees.length; i++) {
            if (bouees[i] != null) {
                chenalRouge.set(i + (line == Line.A ? 0 : 5), bouees[i]);
            }
        }
    }

    public void addVert(Line line, ECouleurBouee... bouees) {
        for (int i = 0; i < bouees.length; i++) {
            if (bouees[i] != null) {
                chenalVert.set(i + (line == Line.A ? 0 : 5), bouees[i]);
            }
        }
    }

}
