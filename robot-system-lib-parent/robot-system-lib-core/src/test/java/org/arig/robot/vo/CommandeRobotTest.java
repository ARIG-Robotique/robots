package org.arig.robot.vo;

import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeConsigne;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The Class CommandeRobotTest.
 *
 * @author GregoryDepuille
 */
@ExtendWith(SpringExtension.class)
public class CommandeRobotTest {

    private final CommandeRobot consigne = new CommandeRobot();

    @Test
    public void testIsType() {
        Assertions.assertFalse(consigne.isType(TypeConsigne.LINE));
        Assertions.assertTrue(consigne.isType(TypeConsigne.DIST));
        Assertions.assertTrue(consigne.isType(TypeConsigne.LINE, TypeConsigne.ANGLE));
        Assertions.assertTrue(consigne.isType(TypeConsigne.DIST, TypeConsigne.ANGLE));
        Assertions.assertTrue(consigne.isType(TypeConsigne.XY, TypeConsigne.ANGLE));
        Assertions.assertFalse(consigne.isType(TypeConsigne.XY, TypeConsigne.CIRCLE));
    }

    @Test
    public void testIsAllTypes() {
        Assertions.assertFalse(consigne.isAllTypes(TypeConsigne.LINE, TypeConsigne.ANGLE));
        Assertions.assertTrue(consigne.isAllTypes(TypeConsigne.DIST, TypeConsigne.ANGLE));
    }
}
