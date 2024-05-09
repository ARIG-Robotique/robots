package org.arig.robot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Jardiniere extends ZoneDepose {

    // jardinière: dépose au rang 1 (plus proche de la table)
    private boolean rang1 = false;
    // jardinière: dépose au rang 2 (plus au fond)
    private boolean rang2 = false;

    public Jardiniere(String name) {
        super(name);
    }

    public int score() {
        return score(true);
    }

    public boolean hasPots() {
        return data().stream().anyMatch(Plante::isDansPot);
    }

    @Override
    public void add(Plante[] plantes) {
        if (hasPots()) {
            for (Plante plante : plantes) {
                if (plante.getType() == TypePlante.AUCUNE && plante.isDansPot()) {
                    // cas des ajouts de pots sans plantes
                    data.add(plante);
                } else if (plante.getType() != TypePlante.AUCUNE) {
                    boolean done = false;
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getType() == TypePlante.AUCUNE) {
                            data.set(i, plante.withPot());
                            done = true;
                            break;
                        }
                    }
                    if (!done) {
                        data.add(plante);
                    }
                }
            }
        } else {
            super.add(plantes);
        }
    }

    public Jardiniere clone() {
        Jardiniere newJardiniere = new Jardiniere(null);
        newJardiniere.add(data().toArray(new Plante[0]));
        return newJardiniere;
    }

}
