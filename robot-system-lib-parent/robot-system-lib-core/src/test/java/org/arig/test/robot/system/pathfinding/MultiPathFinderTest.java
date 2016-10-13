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

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class MultiPathFinderTest {

    private static MultiPathFinderImpl pf;

    private static File dir;

    private Point from;
    private Point to;

    @BeforeClass
    public static void initClass() {
        pf = new MultiPathFinderImpl();
        pf.setNbTileX(40);
        pf.setNbTileY(40);
        pf.setAllowDiagonal(true);

        String tmpDir = System.getProperty("java.io.tmpdir");
        dir = new File(tmpDir + "/arig/robot/path");
        if (dir.exists()) {
            dir.delete();
        }
        dir.mkdirs();
        pf.setPathDir(dir);
    }

    @Before
    public void beforeTest() {
        URL url = getClass().getResource("/assets/labyrinthe.png");
        pf.construitGraphDepuisImageNoirEtBlanc(new File(url.getPath()));

        from = new Point(25, 25);
        to = new Point(365, 305);
    }

    @Test
    public void testStartNodeDoesntExist() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        try {
            pf.findPath(new Point(5, 5), to);
            Assert.fail();
        } catch (NoPathFoundException npfe) {
            Assert.assertEquals(NoPathFoundException.ErrorType.START_NODE_DOES_NOT_EXIST, npfe.getErrorType());
        }
    }

    @Test
    public void testEndNodeDoesntExist() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        try {
            pf.findPath(from, new Point(225, 285));
            Assert.fail();
        } catch (NoPathFoundException npfe) {
            Assert.assertEquals(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST, npfe.getErrorType());
        }
    }

    @Test
    public void testFindPathAStarManhattan() throws IOException, NoPathFoundException  {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_MANHATTAN);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindPathAStarEuclidian() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindPathDijkstra() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindPathBreadthFirstSearch() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.BREADTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    public void testFindPathDepthFirstSearch() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.DEPTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }
}
