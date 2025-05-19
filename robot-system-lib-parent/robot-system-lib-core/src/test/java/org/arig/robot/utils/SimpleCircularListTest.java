package org.arig.robot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
public class SimpleCircularListTest {

  @Test
  public void testNoRotate() {
    SimpleCircularList<String> list = initList();

    Assertions.assertEquals("A", list.get(0));
    Assertions.assertEquals("E", list.get(4));
  }

  @Test
  public void testRotate1() {
    SimpleCircularList<String> list = initList();

    list.rotate(1);

    Assertions.assertEquals("B", list.get(0));
    Assertions.assertEquals("F", list.get(4));
  }

  @Test
  public void testRotateMinus1() {
    SimpleCircularList<String> list = initList();

    list.rotate(-1);

    Assertions.assertEquals("F", list.get(0));
    Assertions.assertEquals("D", list.get(4));
  }

  @Test
  public void testRotate6() {
    SimpleCircularList<String> list = initList();

    list.rotate(6);

    Assertions.assertEquals("A", list.get(0));
    Assertions.assertEquals("E", list.get(4));
  }

  @Test
  public void testRotate1Then1() {
    SimpleCircularList<String> list = initList();

    list.rotate(1);
    list.rotate(1);

    Assertions.assertEquals("C", list.get(0));
    Assertions.assertEquals("A", list.get(4));
  }

  @Test
  public void testRotate1ThenMinus3() {
    SimpleCircularList<String> list = initList();

    list.rotate(1);
    list.rotate(-3);

    Assertions.assertEquals("E", list.get(0));
    Assertions.assertEquals("C", list.get(4));
  }

  private SimpleCircularList<String> initList() {
    SimpleCircularList<String> list = new SimpleCircularList<>(6, (i) -> StringUtils.EMPTY);

    list.set(0, "A");
    list.set(1, "B");
    list.set(2, "C");
    list.set(3, "D");
    list.set(4, "E");
    list.set(5, "F");

    return list;
  }
}
