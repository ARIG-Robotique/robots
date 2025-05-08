package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructionAreaTest {

    @Test
    public void testCalculScore() {
        ConstructionArea area = new ConstructionArea("zone", (byte) 3);

        Assertions.assertTrue(area.isEmpty());
        Assertions.assertEquals(0, area.score());

        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertFalse(area.isEmpty());
        Assertions.assertEquals(4, area.score());

        area.addGradin(ConstructionArea.Rang.RANG_2, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(8, area.score());

        area.addGradin(ConstructionArea.Rang.RANG_3, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(12, area.score());

        area.removeGradin(ConstructionArea.Rang.RANG_3, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(8, area.score());
        area.addGradin(ConstructionArea.Rang.RANG_2, ConstructionArea.Etage.ETAGE_2);
        Assertions.assertEquals(16, area.score());

        area.removeGradin(ConstructionArea.Rang.RANG_2, ConstructionArea.Etage.ETAGE_2);
        area.removeGradin(ConstructionArea.Rang.RANG_2, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(4, area.score());
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_2);
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_3);
        Assertions.assertEquals(28, area.score());
    }

    @Test
    public void testFirstRangConstructiblePetiteZoneLimit2Etage() {
        final boolean limit2Etage = true;
        ConstructionArea area = new ConstructionArea("petite zone test", (byte) 1);

        // Vide
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_1, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 1
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_2, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 2
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_2);
        Assertions.assertNull(area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertNull(area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));
        Assertions.assertNull(area.getFirstConstructibleEtage(null, limit2Etage));
    }

    @Test
    public void testFirstRangConstructiblePetiteZone() {
        final boolean limit2Etage = false;
        ConstructionArea area = new ConstructionArea("petite zone test", (byte) 1);

        // Vide
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_1, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 1
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_2, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 2
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_2);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_3, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 3
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_3);
        Assertions.assertNull(area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertNull(area.getFirstConstructibleEtage(null, limit2Etage));
    }

    @Test
    public void testFirstRangConstructibleGrandeZoneLimit2Etage() {
        boolean limit2Etage = true;
        ConstructionArea area = new ConstructionArea("grande zone test", (byte) 3);

        // Vide
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_1, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 1
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_1, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_2, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_1, limit2Etage));

        // Rang 1 etage 2
        area.addGradin(ConstructionArea.Rang.RANG_1, ConstructionArea.Etage.ETAGE_2);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_2, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_1, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_2, limit2Etage));

        // Rang 2 etage 1
        area.addGradin(ConstructionArea.Rang.RANG_2, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_2, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_2, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_2, limit2Etage));

        // Rang 2 etage 2
        area.addGradin(ConstructionArea.Rang.RANG_2, ConstructionArea.Etage.ETAGE_2);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_3, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_1, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_3, limit2Etage));

        // Rang 3 etage 1
        area.addGradin(ConstructionArea.Rang.RANG_3, ConstructionArea.Etage.ETAGE_1);
        Assertions.assertEquals(ConstructionArea.Rang.RANG_3, area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertEquals(ConstructionArea.Etage.ETAGE_2, area.getFirstConstructibleEtage(ConstructionArea.Rang.RANG_3, limit2Etage));

        // Rang 3 etage 2
        area.addGradin(ConstructionArea.Rang.RANG_3, ConstructionArea.Etage.ETAGE_2);
        Assertions.assertNull(area.getFirstConstructibleRang(limit2Etage));
        Assertions.assertNull(area.getFirstConstructibleEtage(null, limit2Etage));
    }
}
