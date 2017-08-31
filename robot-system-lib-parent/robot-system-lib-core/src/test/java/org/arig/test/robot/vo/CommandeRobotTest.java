package org.arig.test.robot.vo;

import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * The Class CommandeRobotTest.
 * 
 * @author GregoryDepuille
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class CommandeRobotTest {

    private final CommandeRobot consigne = new CommandeRobot();

    @Test
    public void testIsType() {
        Assert.assertFalse(consigne.isType(TypeConsigne.LINE));
        Assert.assertTrue(consigne.isType(TypeConsigne.DIST));
    }

    @Test
    public void testIsAllTypes() {
        Assert.assertFalse(consigne.isAllTypes(TypeConsigne.LINE, TypeConsigne.ANGLE));
        Assert.assertTrue(consigne.isAllTypes(TypeConsigne.DIST, TypeConsigne.ANGLE));
    }
}
