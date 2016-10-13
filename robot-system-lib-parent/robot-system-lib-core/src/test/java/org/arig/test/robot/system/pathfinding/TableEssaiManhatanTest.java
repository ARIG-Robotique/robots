package org.arig.test.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class TableEssaiManhatanTest {

    private static MultiPathFinderImpl pf;

    private static File dir;
    private static File imgSource;

    @BeforeClass
    public static void initClass() {
        pf = new MultiPathFinderImpl();
        pf.setNbTileX(118);
        pf.setNbTileY(180);
        pf.setAllowDiagonal(true);

        URL url = TableEssaiManhatanTest.class.getClass().getResource("/assets/table-test-obstacle.png");
        imgSource = new File(url.getPath());
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);

        String tmpDir = System.getProperty("java.io.tmpdir");
        dir = new File(tmpDir + "/arig/robot/pathTableEssai");
        if (dir.exists()) {
            dir.delete();
        }
        pf.setPathDir(dir);
    }

    @Before
    public void beforeTest() {
        pf.construitGraphDepuisImageNoirEtBlanc(imgSource);
    }

    @Test
    public void testFindFirstPath() throws IOException, NoPathFoundException {
        Point from = new Point(36, 21);
        Point to = new Point(90, 140);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindSecondPath() throws IOException, NoPathFoundException {
        Point from = new Point(90,140);
        Point to = new Point(25, 120);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindThirdPath() throws IOException, NoPathFoundException {
        Point from = new Point(25, 120);
        Point to = new Point(70, 50);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindFourthPath() throws IOException, NoPathFoundException {
        Point from = new Point(70, 50);
        Point to = new Point(25, 50);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testAddObstacle() throws IOException, NoPathFoundException, InterruptedException {
        Point from = new Point(36, 21);
        Point to = new Point(90, 140);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());

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
        c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());

        LocalDateTime waitTime = LocalDateTime.now().plusSeconds(6);
        while (LocalDateTime.now().isBefore(waitTime)) ;
    }
}
