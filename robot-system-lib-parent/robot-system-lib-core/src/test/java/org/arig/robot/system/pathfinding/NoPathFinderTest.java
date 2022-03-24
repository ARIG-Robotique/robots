package org.arig.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
public class NoPathFinderTest {

    private NoPathFinderImpl pf = new NoPathFinderImpl();

    @Test
    public void testFindPath() throws NoPathFoundException {
        Point from = new Point(10, 10);
        Point to = new Point(20, 20);

        Chemin c = pf.findPath(from, to);
        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.nbPoints());
        Assertions.assertTrue(c.hasNext());

        Point r = c.next();
        Assertions.assertFalse(c.hasNext());
        Assertions.assertTrue(to.getX() == r.getX());
        Assertions.assertTrue(to.getY() == r.getY());
    }
}
