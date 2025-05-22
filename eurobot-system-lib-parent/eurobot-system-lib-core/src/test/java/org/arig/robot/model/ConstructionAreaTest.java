package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructionAreaTest {

  @Test
  public void testCalculScore() {
    ConstructionArea area = new ConstructionArea("zone", (byte) 3);

    Assertions.assertTrue(area.isEmpty());
    Assertions.assertEquals(0, area.score());

    area.addGradin(Rang.RANG_1, Etage.ETAGE_1);
    Assertions.assertFalse(area.isEmpty());
    Assertions.assertEquals(4, area.score());

    area.addGradin(Rang.RANG_2, Etage.ETAGE_1);
    Assertions.assertEquals(8, area.score());

    area.addGradin(Rang.RANG_3, Etage.ETAGE_1);
    Assertions.assertEquals(12, area.score());

    area.removeGradin(Rang.RANG_3, Etage.ETAGE_1);
    Assertions.assertEquals(8, area.score());
    area.addGradin(Rang.RANG_2, Etage.ETAGE_2);
    Assertions.assertEquals(16, area.score());

    area.removeGradin(Rang.RANG_2, Etage.ETAGE_2);
    area.removeGradin(Rang.RANG_2, Etage.ETAGE_1);
    Assertions.assertEquals(4, area.score());
    area.addGradin(Rang.RANG_1, Etage.ETAGE_2);
    area.addGradin(Rang.RANG_1, Etage.ETAGE_3);
    Assertions.assertEquals(28, area.score());
  }

  @Test
  public void testFirstRangConstructiblePetiteZone() {
    ConstructionArea area = new ConstructionArea("petite zone test", (byte) 1);

    // Vide
    Assertions.assertEquals(Rang.RANG_1, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_1, area.getFirstConstructibleEtage(Rang.RANG_1));

    // Rang 1 etage 1
    area.addGradin(Rang.RANG_1, Etage.ETAGE_1);
    Assertions.assertEquals(Rang.RANG_1, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_2, area.getFirstConstructibleEtage(Rang.RANG_1));

    // Rang 1 etage 2
    area.addGradin(Rang.RANG_1, Etage.ETAGE_2);
    Assertions.assertNull(area.getFirstConstructibleRang());
    Assertions.assertNull(area.getFirstConstructibleEtage(Rang.RANG_1));
    Assertions.assertNull(area.getFirstConstructibleEtage(null));
  }

  @Test
  public void testFirstRangConstructibleGrandeZone() {
    ConstructionArea area = new ConstructionArea("grande zone test", (byte) 3);

    // Vide
    Assertions.assertEquals(Rang.RANG_1, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_1, area.getFirstConstructibleEtage(Rang.RANG_1));

    // Rang 1 etage 1
    area.addGradin(Rang.RANG_1, Etage.ETAGE_1);
    Assertions.assertEquals(Rang.RANG_1, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_2, area.getFirstConstructibleEtage(Rang.RANG_1));

    // Rang 1 etage 2
    area.addGradin(Rang.RANG_1, Etage.ETAGE_2);
    Assertions.assertEquals(Rang.RANG_2, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_1, area.getFirstConstructibleEtage(Rang.RANG_2));

    // Rang 1 etage 3
    area.addGradin(Rang.RANG_1, Etage.ETAGE_3);
    Assertions.assertEquals(Rang.RANG_2, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_1, area.getFirstConstructibleEtage(Rang.RANG_2));

    // Rang 2 etage 1
    area.addGradin(Rang.RANG_2, Etage.ETAGE_1);
    Assertions.assertEquals(Rang.RANG_2, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_2, area.getFirstConstructibleEtage(Rang.RANG_2));

    // Rang 2 etage 2
    area.addGradin(Rang.RANG_2, Etage.ETAGE_2);
    Assertions.assertEquals(Rang.RANG_3, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_1, area.getFirstConstructibleEtage(Rang.RANG_3));

    // Rang 2 etage 3
    area.addGradin(Rang.RANG_2, Etage.ETAGE_3);
    Assertions.assertEquals(Rang.RANG_3, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_1, area.getFirstConstructibleEtage(Rang.RANG_3));

    // Rang 3 etage 1
    area.addGradin(Rang.RANG_3, Etage.ETAGE_1);
    Assertions.assertEquals(Rang.RANG_3, area.getFirstConstructibleRang());
    Assertions.assertEquals(Etage.ETAGE_2, area.getFirstConstructibleEtage(Rang.RANG_3));

    // Rang 3 etage 2
    area.addGradin(Rang.RANG_3, Etage.ETAGE_2);
    Assertions.assertNull(area.getFirstConstructibleRang());
    Assertions.assertNull(area.getFirstConstructibleEtage(null));

    // Rang 3 etage 3
    area.addGradin(Rang.RANG_3, Etage.ETAGE_3);
    Assertions.assertNull(area.getFirstConstructibleRang());
    Assertions.assertNull(area.getFirstConstructibleEtage(null));
  }
}
