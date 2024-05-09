package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JardiniereTest {

    @Test
    public void testMiseEnPot() {
        Jardiniere jardiniere = new Jardiniere("test");

        jardiniere.add(new Plante[]{
                new Plante(TypePlante.AUCUNE, true),
                new Plante(TypePlante.AUCUNE, true),
                new Plante(TypePlante.AUCUNE, true),
                new Plante(TypePlante.AUCUNE, true),
                new Plante(TypePlante.AUCUNE, true),
                new Plante(TypePlante.AUCUNE, true)
        });

        Assertions.assertEquals(jardiniere.score(), 0);

        jardiniere.add(new Plante[]{
                new Plante(TypePlante.INCONNU, false),
                new Plante(TypePlante.INCONNU, false),
                new Plante(TypePlante.INCONNU, false)
        });

        Assertions.assertEquals(jardiniere.score(), 15);

        jardiniere.add(new Plante[]{
                new Plante(TypePlante.INCONNU, false),
                new Plante(TypePlante.INCONNU, false)
        });

        Assertions.assertEquals(jardiniere.score(), 25);
    }

}
