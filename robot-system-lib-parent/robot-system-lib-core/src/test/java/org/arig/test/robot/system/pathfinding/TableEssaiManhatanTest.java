package org.arig.test.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PathFindingTestContext.class })
public class TableEssaiManhatanTest {

    @Autowired
    @Qualifier("multiPathTableEssai")
    private MultiPathFinderImpl pf;

    @Test
    public void testFindFirstPath() throws IOException, NoPathFoundException {
        Point from = new Point(36, 21);
        Point to = new Point(90, 140);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(4, c.nbPoints());
    }

    @Test
    public void testFindSecondPath() throws IOException, NoPathFoundException {
        Point from = new Point(90,140);
        Point to = new Point(25, 120);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(2, c.nbPoints());
    }

    @Test
    public void testFindThirdPath() throws IOException, NoPathFoundException {
        Point from = new Point(25, 120);
        Point to = new Point(70, 50);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(4, c.nbPoints());
    }

    @Test
    public void testFindFourthPath() throws IOException, NoPathFoundException {
        Point from = new Point(70, 50);
        Point to = new Point(25, 50);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(1, c.nbPoints());
    }

    @Test
    public void testAddObstacle() throws IOException, NoPathFoundException, InterruptedException {
        Point from = new Point(36, 21);
        Point to = new Point(90, 140);

        Polygon obs = new Polygon();
        obs.addPoint(-30, 15);
        obs.addPoint(-15, 30);
        obs.addPoint(15, 30);
        obs.addPoint(30, 15);
        obs.addPoint(30, -15);
        obs.addPoint(15, -30);
        obs.addPoint(-15, -30);
        obs.addPoint(-30, -15);
        obs.translate(70, 50);

        pf.addObstacles(obs);
        Chemin c1 = pf.findPath(from, to);
        Assert.assertNotNull(c1);
        Assert.assertEquals(4, c1.nbPoints());

        LocalDateTime waitTime = LocalDateTime.now().plusSeconds(6);
        while (LocalDateTime.now().isBefore(waitTime));

        Chemin c2 = pf.findPath(from, to);
        Assert.assertNotNull(c2);
        Assert.assertEquals(4, c2.nbPoints());

        boolean idem = true;
        while(c1.hasNext() && c2.hasNext() && idem) {
            Point p1 = c1.next();
            Point p2 = c2.next();
            idem = p1.getX() == p2.getX() && p1.getY() == p2.getY();
        }
        Assert.assertFalse(idem);
    }
}
