package org.arig.test.robot.system.pathfinding;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URL;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class MultiPathFinderTest {

    private static MultiPathFinderImpl pf;

    private Point from;
    private Point to;

    @BeforeClass
    public static void initClass() {
        pf = new MultiPathFinderImpl();
        pf.setMaxDistanceArrivee(1);
        pf.setMaxDistanceDepart(29);
        pf.setNbTileX(40);
        pf.setNbTileY(40);
        pf.setAllowDiagonal(true);
    }

    @Before
    public void beforeTest() {
        URL url = getClass().getResource("/assets/labyrinthe.png");
        pf.construitGraphDepuisImageNoirEtBlanc(new File(url.getPath()));

        from = new Point(25, 25);
        to = new Point(365, 305);
    }

    @Test
    @SneakyThrows
    public void testStartNodeDoesntExist() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        Chemin c = pf.findPath(new Point(5, 5), to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
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
    @SneakyThrows
    public void testFindPathAStarManhattan() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_MANHATTAN);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testFindPathAStarEuclidian() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testFindPathDijkstra() {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testFindPathBreadthFirstSearch() {
        pf.setAlgorithm(PathFinderAlgorithm.BREADTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testFindPathDepthFirstSearch() {
        pf.setAlgorithm(PathFinderAlgorithm.DEPTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testFindPathDijkstraAvecObstacle () {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin cheminSansObstacles = pf.findPath(from, to);
        Assert.assertNotNull(cheminSansObstacles);
        Assert.assertTrue(cheminSansObstacles.hasNext());

        pf.addObstacles(new Rectangle2D.Double(100, 100, 200, 100));
        Chemin cheminAvecObstacles = pf.findPath(from, to);
        Assert.assertNotNull(cheminAvecObstacles);
        Assert.assertTrue(cheminAvecObstacles.hasNext());

        Assert.assertNotEquals(cheminSansObstacles.nbPoints(), cheminAvecObstacles.nbPoints());
    }
}
