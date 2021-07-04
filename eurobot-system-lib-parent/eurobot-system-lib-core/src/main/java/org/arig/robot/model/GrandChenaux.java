package org.arig.robot.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GrandChenaux extends Chenaux {

    public enum Line {
        A, // 5 emplacements à l'extérieur du port (index 2 à 6)
        B, // 4 emplacements à l'intérieur du port (index 7 à 10)
        C // 2 emplacements contre la bordure (index 0 et 1)
    }

    public GrandChenaux() {
        for (int i = 0; i < 11; i++) {
            chenalRouge.add(null);
            chenalVert.add(null);
        }
    }

    @Override
    protected Chenaux newInstance() {
        return new GrandChenaux();
    }

    public List<ECouleurBouee> getVert(Line line) {
        switch (line) {
            case A:
                return chenalVert.subList(2, 7);
            case B:
                return chenalVert.subList(7, 10);
            case C:
                return chenalVert.subList(0, 2);
            default:
                return Collections.emptyList();
        }
    }

    public List<ECouleurBouee> getRouge(Line line) {
        switch (line) {
            case A:
                return chenalRouge.subList(2, 7);
            case B:
                return chenalRouge.subList(7, 10);
            case C:
                return chenalRouge.subList(0, 2);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public boolean chenalVertEmpty() {
        return chenalVert.stream().skip(2).allMatch(Objects::isNull);
    }

    @Override
    public boolean chenalRougeEmpty() {
        return chenalRouge.stream().skip(2).allMatch(Objects::isNull);
    }

    public void addVert(Line line, int index, ECouleurBouee bouee) {
        switch (line) {
            case A:
                assert index >= 0 && index < 5;
                if (bouee != null) {
                    chenalVert.set(index + 2, bouee);
                }
                break;
            case B:
                assert index >= 0 && index < 4;
                if (bouee != null) {
                    chenalVert.set(index + 7, bouee);
                }
                break;
            case C:
                assert index >= 0 && index < 2;
                if (bouee != null) {
                    chenalVert.set(index, bouee);
                }
                break;
        }
    }

    public void addRouge(Line line, int index, ECouleurBouee bouee) {
        switch (line) {
            case A:
                assert index >= 0 && index < 5;
                if (bouee != null) {
                    chenalRouge.set(index + 2, bouee);
                }
                break;
            case B:
                assert index >= 0 && index < 4;
                if (bouee != null) {
                    chenalRouge.set(index + 7, bouee);
                }
                break;
            case C:
                assert index >= 0 && index < 2;
                if (bouee != null) {
                    chenalRouge.set(index, bouee);
                }
                break;
        }
    }

    public void addVert(Line line, ECouleurBouee... bouees) {
        switch (line) {
            case A:
                assert bouees.length == 5;
                for (int i = 0; i < 5; i++) {
                    if (bouees[i] != null) {
                        chenalVert.set(4 - i + 2, bouees[i]); // inversé pour dépose arrière Nerell
                    }
                }
                break;
            case B:
                assert bouees.length == 4;
                for (int i = 0; i < 4; i++) {
                    if (bouees[i] != null) {
                        chenalVert.set(i + 7, bouees[i]);
                    }
                }
                break;
            case C:
                assert bouees.length == 2;
                for (int i = 0; i < 2; i++) {
                    if (bouees[i] != null) {
                        chenalVert.set(i, bouees[i]);
                    }
                }
                break;
        }
    }

    public void addRouge(Line line, ECouleurBouee... bouees) {
        switch (line) {
            case A:
                assert bouees.length == 5;
                for (int i = 0; i < 5; i++) {
                    if (bouees[i] != null) {
                        chenalRouge.set(i + 2, bouees[i]);
                    }
                }
                break;
            case B:
                assert bouees.length == 4;
                for (int i = 0; i < 4; i++) {
                    if (bouees[i] != null) {
                        chenalRouge.set(3 - i + 7, bouees[i]); // inversé pour dépose avant Nerell
                    }
                }
                break;
            case C:
                assert bouees.length == 2;
                for (int i = 0; i < 2; i++) {
                    if (bouees[i] != null) {
                        chenalRouge.set(i, bouees[i]);
                    }
                }
                break;
        }
    }

}
