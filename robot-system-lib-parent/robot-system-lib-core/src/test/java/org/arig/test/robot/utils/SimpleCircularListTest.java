package org.arig.test.robot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.utils.SimpleCircularList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class SimpleCircularListTest {

    @Test
    public void testNoRotate() {
        SimpleCircularList<String> list = initList();

        Assert.assertEquals("A", list.get(0));
        Assert.assertEquals("E", list.get(4));
    }

    @Test
    public void testRotate1() {
        SimpleCircularList<String> list = initList();

        list.rotate(1);

        Assert.assertEquals("B", list.get(0));
        Assert.assertEquals("F", list.get(4));
    }

    @Test
    public void testRotateMinus1() {
        SimpleCircularList<String> list = initList();

        list.rotate(-1);

        Assert.assertEquals("F", list.get(0));
        Assert.assertEquals("D", list.get(4));
    }

    @Test
    public void testRotate6() {
        SimpleCircularList<String> list = initList();

        list.rotate(6);

        Assert.assertEquals("A", list.get(0));
        Assert.assertEquals("E", list.get(4));
    }

    @Test
    public void testRotate1Then1() {
        SimpleCircularList<String> list = initList();

        list.rotate(1);
        list.rotate(1);

        Assert.assertEquals("C", list.get(0));
        Assert.assertEquals("A", list.get(4));
    }

    @Test
    public void testRotate1ThenMinus3() {
        SimpleCircularList<String> list = initList();

        list.rotate(1);
        list.rotate(-3);

        Assert.assertEquals("E", list.get(0));
        Assert.assertEquals("C", list.get(4));
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
