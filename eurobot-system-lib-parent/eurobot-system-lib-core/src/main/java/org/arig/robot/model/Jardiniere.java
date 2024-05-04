package org.arig.robot.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Jardiniere extends ZoneDepose {

    private boolean rang1 = false; // dépose au rang 1 (plus proche de la table)
    private boolean rang2 = false; // dépose au rang 2 (plus au fond)

    @Override
    public int score() {
        return score(true);
    }

    public Jardiniere clone() {
        Jardiniere newJardiniere = new Jardiniere();
        newJardiniere.add(data.toArray(new Plante[0]));
        return newJardiniere;
    }

}
