package org.arig.test.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.pathfinding.impl.NoPathFinderImpl;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Created by mythril on 30/12/13.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class NoPathFinderTest {

    private NoPathFinderImpl pf = new NoPathFinderImpl();

    @Test
    public void testFindPath() throws NoPathFoundException {
        Point from = new Point(10, 10);
        Point to = new Point(20, 20);

        Chemin c = pf.findPath(from, to);
        Assert.assertEquals(1, c.nbPoints());
        Assert.assertTrue(c.hasNext());

        Point r = c.next();
        Assert.assertFalse(c.hasNext());
        Assert.assertTrue(to.getX() == r.getX());
        Assert.assertTrue(to.getY() == r.getY());
    }
}
