package org.arig.test.robot.system.pathfinding;

import lombok.SneakyThrows;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.geom.Ellipse2D;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PathFindingTestContext.class})
public class MultiPathFinderTest {

    @Autowired
    @Qualifier("multiPathTableJaune")
    private MultiPathFinderImpl pf;

    private Point from = new Point(89, 16);
    private Point to = new Point(227, 178);

    @Test
    @SneakyThrows
    public void testStartNodeDoesntExist() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR);
        Chemin c = pf.findPath(new Point(67, 81), to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testEndNodeDoesntExist() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR);
        try {
            pf.findPath(from, new Point(225, 285));
            Assert.fail();
        } catch (NoPathFoundException npfe) {
            Assert.assertEquals(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST, npfe.getErrorType());
        }
    }

    @Test
    @SneakyThrows
    public void testFindPathAStar() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(8, c.nbPoints());
    }

    @Test
    @SneakyThrows
    public void testFindPathDijkstra() {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(6, c.nbPoints());
    }

    @Test
    @SneakyThrows
    public void testFindPathLazyThetaStar() {
        pf.setAlgorithm(PathFinderAlgorithm.LAZY_THETA_STAR);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(7, c.nbPoints());
    }

    @Test
    @SneakyThrows
    public void testFindPathAnya16() {
        pf.setAlgorithm(PathFinderAlgorithm.ANYA16);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(6, c.nbPoints());
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    public void testFindPathLazyThetaStarAvecObstacle() {
        pf.setAlgorithm(PathFinderAlgorithm.LAZY_THETA_STAR);
        Chemin cheminSansObstacles = pf.findPath(from, to);
        Assert.assertNotNull(cheminSansObstacles);
        Assert.assertTrue(cheminSansObstacles.hasNext());

        pf.addObstacles(new Ellipse2D.Double(195 - 20, 80 - 20, 40, 40));
        Chemin cheminAvecObstacles = pf.findPath(from, to);
        Assert.assertNotNull(cheminAvecObstacles);
        Assert.assertTrue(cheminAvecObstacles.hasNext());

        Assert.assertNotEquals(cheminSansObstacles.nbPoints(), cheminAvecObstacles.nbPoints());
    }
}
