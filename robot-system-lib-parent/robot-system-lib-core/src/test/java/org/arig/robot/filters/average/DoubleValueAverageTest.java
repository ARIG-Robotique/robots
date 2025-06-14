package org.arig.robot.filters.average;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 28/10/2017.
 */
@ExtendWith(SpringExtension.class)
public class DoubleValueAverageTest {

  @Test
  public void testAverage() {
    DoubleValueAverage avg = new DoubleValueAverage();

    double expected = 0;
    for (double i = 0; i < 20; i++) {
      double res = avg.filter(i);
      expected = (expected + i) / 2;
      Assertions.assertEquals(expected, res, 1);
    }

    avg.reset();
    expected = 0;
    for (double i = 20; i > 0; i--) {
      double res = avg.filter(i);
      expected = (expected + i) / 2;
      Assertions.assertTrue(expected - res <= 1);
    }
  }

  @Test
  public void testChangeLimit() {
    int limit = 5;
    DoubleValueAverage avg = new DoubleValueAverage(limit);
    for (double i = 0; i < limit * 2; i++) {
      avg.filter(i);
      Assertions.assertEquals(Math.min(i + 1, limit), avg.size(), 0);
    }
    Assertions.assertEquals(limit, avg.size());

    int limit2 = 10;
    avg.setNbValues(limit2);
    for (double i = 0; i < limit2 * 2; i++) {
      avg.filter(i);
      Assertions.assertEquals(Math.min(i + limit + 1, limit2), avg.size(), 0);
    }
    Assertions.assertEquals(limit2, avg.size());
  }

  @Test
  public void testReset() {
    int limit = 5, nbExec = 0;
    DoubleValueAverage avg = new DoubleValueAverage(limit);
    do {
      for (double i = 0; i < limit * 2; i++) {
        avg.filter(i);
        Assertions.assertEquals(Math.min(i + 1, limit), avg.size(), 0);
      }
      Assertions.assertEquals(limit, avg.size());

      avg.reset();
      Assertions.assertEquals(0, avg.size());

      nbExec++;
    } while (nbExec < 3);
  }
}
