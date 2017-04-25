package org.arig.test.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PathFindingTestContext.class })
public class MultiPathFinderTest {

    @Autowired
    @Qualifier("multiPathLabyrinthe")
    private MultiPathFinderImpl pf;

    private Point from = new Point(25, 25);
    private Point to = new Point(365, 305);

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
        Assert.assertEquals(20, c.nbPoints());
    }

    @Test
    public void testFindPathAStarEuclidian() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(22, c.nbPoints());
    }

    @Test
    public void testFindPathDijkstra() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(19, c.nbPoints());
    }

    @Test
    public void testFindPathBreadthFirstSearch() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.BREADTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(21, c.nbPoints());
    }

    @Test
    public void testFindPathDepthFirstSearch() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.DEPTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(35, c.nbPoints());
    }
}
