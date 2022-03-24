package org.arig.robot.system.pathfinding;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.geom.Ellipse2D;
import java.util.Collections;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
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
        Assertions.assertNotNull(c);
        Assertions.assertTrue(c.hasNext());
    }

    @Test
    @SneakyThrows
    public void testEndNodeDoesntExist() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR);
        try {
            pf.findPath(from, new Point(225, 285));
            Assertions.fail();
        } catch (NoPathFoundException npfe) {
            Assertions.assertEquals(NoPathFoundException.ErrorType.END_NODE_DOES_NOT_EXIST, npfe.getErrorType());
        }
    }

    @Test
    @SneakyThrows
    public void testFindPathAStar() {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR);
        Chemin c = pf.findPath(from, to);
        Assertions.assertNotNull(c);
        Assertions.assertEquals(8, c.nbPoints());
    }

    @Test
    @SneakyThrows
    public void testFindPathDijkstra() {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin c = pf.findPath(from, to);
        Assertions.assertNotNull(c);
        Assertions.assertEquals(6, c.nbPoints());
    }

    @Test
    @SneakyThrows
    public void testFindPathLazyThetaStar() {
        pf.setAlgorithm(PathFinderAlgorithm.LAZY_THETA_STAR);
        Chemin c = pf.findPath(from, to);
        Assertions.assertNotNull(c);
        Assertions.assertEquals(7, c.nbPoints());
    }

    @Test
    @SneakyThrows
    public void testFindPathAnya16() {
        pf.setAlgorithm(PathFinderAlgorithm.ANYA16);
        Chemin c = pf.findPath(from, to);
        Assertions.assertNotNull(c);
        Assertions.assertEquals(6, c.nbPoints());
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    public void testFindPathLazyThetaStarAvecObstacle() {
        pf.setAlgorithm(PathFinderAlgorithm.LAZY_THETA_STAR);
        Chemin cheminSansObstacles = pf.findPath(from, to);
        Assertions.assertNotNull(cheminSansObstacles);
        Assertions.assertTrue(cheminSansObstacles.hasNext());

        pf.setObstacles(Collections.singletonList(new Ellipse2D.Double(195 - 20, 80 - 20, 40, 40)));
        Chemin cheminAvecObstacles = pf.findPath(from, to);
        Assertions.assertNotNull(cheminAvecObstacles);
        Assertions.assertTrue(cheminAvecObstacles.hasNext());

        Assertions.assertNotEquals(cheminSansObstacles.nbPoints(), cheminAvecObstacles.nbPoints());
    }
}
