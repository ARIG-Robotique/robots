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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PathFindingTestContext.class })
public class TableJauneTest {

    @Autowired
    @Qualifier("multiPathTableJaune")
    private MultiPathFinderImpl pf;

    @Test
    public void testFindPath() throws IOException, NoPathFoundException {
        Point from = new Point(149, 150);
        Point to = new Point(170, 100);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertEquals(2, c.nbPoints());
    }
}
