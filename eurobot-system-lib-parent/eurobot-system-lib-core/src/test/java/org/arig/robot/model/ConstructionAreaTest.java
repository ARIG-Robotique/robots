package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructionAreaTest {

    @Test
    public void testGrandeZone() {
        ConstructionArea area = new ConstructionArea("zone", (byte) 3);

        Assertions.assertTrue(area.isEmpty());
        Assertions.assertEquals(0, area.score());

        area.addTribune(0, 0);
        Assertions.assertEquals(4, area.score());

        area.addTribune(1, 0);
        Assertions.assertEquals(8, area.score());

        area.addTribune(2, 0);
        Assertions.assertEquals(12, area.score());

        area.removeTribune(2, 0);
        Assertions.assertEquals(8, area.score());
        area.addTribune(1, 1);
        Assertions.assertEquals(16, area.score());

        area.removeTribune(1, 1);
        area.removeTribune(1, 0);
        Assertions.assertEquals(4, area.score());
        area.addTribune(0, 1);
        area.addTribune(0, 2);
        Assertions.assertEquals(28, area.score());
    }

    @Test
    public void testGrandeZoneOversize() {
        ConstructionArea area = new ConstructionArea("zone");

        IllegalArgumentException ex = Assertions
            .assertThrows(IllegalArgumentException.class, () -> area.addTribune(2, 0));
        Assertions.assertEquals("Rang 2 out of bounds", ex.getMessage());

        ex = Assertions.assertThrows(IllegalArgumentException.class, () -> area.addTribune(0, 5));
        Assertions.assertEquals("Etage 5 out of bounds", ex.getMessage());
    }
}
