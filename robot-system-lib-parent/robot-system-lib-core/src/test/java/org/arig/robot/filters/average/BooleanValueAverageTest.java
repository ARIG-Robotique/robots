package org.arig.robot.filters.average;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 08/05/15.
 */
@ExtendWith(SpringExtension.class)
public class BooleanValueAverageTest {

  @Test
  public void testFullTrueAverage() {
    BooleanValueAverage avg = new BooleanValueAverage();
    Assertions.assertTrue(avg.filter(true));
    Assertions.assertTrue(avg.filter(true));
    Assertions.assertTrue(avg.filter(true));
  }

  @Test
  public void testFullFalseAverage() {
    BooleanValueAverage avg = new BooleanValueAverage();
    Assertions.assertFalse(avg.filter(false));
    Assertions.assertFalse(avg.filter(false));
    Assertions.assertFalse(avg.filter(false));
  }

  @Test
  public void testIntermitentAverage() {
    BooleanValueAverage avg = new BooleanValueAverage();
    Assertions.assertFalse(avg.filter(false));
    Assertions.assertTrue(avg.filter(true));
    Assertions.assertTrue(avg.filter(true));
    Assertions.assertTrue(avg.filter(true));
    Assertions.assertTrue(avg.filter(false));
    Assertions.assertFalse(avg.filter(false));
    Assertions.assertFalse(avg.filter(false));
  }
}
