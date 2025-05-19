package org.arig.robot.filters.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ChangeFilterTest {

  private static ChangeFilter<Integer> filter;

  @BeforeAll
  public static void initClass() {
    filter = new ChangeFilter<>(-1);
  }

  @BeforeEach
  public void beforeTest() {
    filter.reset();
  }

  @Test
  public void testFilter() {
    Assertions.assertEquals(false, filter.filter(-1));
    Assertions.assertEquals(true, filter.filter(10));
    Assertions.assertEquals(false, filter.filter(10));
    Assertions.assertEquals(true, filter.filter(9));
  }

  @Test
  public void testReset() {
    filter.filter(12);
    Assertions.assertEquals(12, filter.getLastValue(), 0);
    filter.reset();
    Assertions.assertEquals(-1, filter.getLastValue(), 0);
  }
}
