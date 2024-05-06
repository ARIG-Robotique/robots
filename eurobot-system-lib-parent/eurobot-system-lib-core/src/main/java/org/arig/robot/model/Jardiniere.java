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

    public Jardiniere clone() {
        Jardiniere newJardiniere = new Jardiniere(null);
        newJardiniere.add(data().toArray(new Plante[0]));
        return newJardiniere;
    }

}
