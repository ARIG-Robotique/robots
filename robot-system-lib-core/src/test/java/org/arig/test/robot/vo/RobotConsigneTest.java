package org.arig.test.robot.vo;

import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.enums.TypeConsigne;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * The Class RobotConsigneTest.
 * 
 * @author GregoryDepuille
 */

/** The Constant log. */
@RunWith(BlockJUnit4ClassRunner.class)
public class RobotConsigneTest {

    /** The consigne. */
    private final CommandeRobot consigne = new CommandeRobot();

    /**
     * Test is type.
     */
    @Test
    public void testIsType() {
        Assert.assertFalse(consigne.isType(TypeConsigne.LINE));
        Assert.assertTrue(consigne.isType(TypeConsigne.DIST));
    }

    /**
     * Test is all types.
     */
    @Test
    public void testIsAllTypes() {
        Assert.assertFalse(consigne.isAllTypes(TypeConsigne.LINE, TypeConsigne.ANGLE));
        Assert.assertTrue(consigne.isAllTypes(TypeConsigne.DIST, TypeConsigne.ANGLE));
    }
}
