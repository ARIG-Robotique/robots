package org.arig.test.robot.system.pathfinding;

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

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author gdepuille on 30/12/13.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class TableJauneTest {

    private static MultiPathFinderImpl pf;
    private static File imgSource;

    @BeforeClass
    public static void initClass() {
        pf = new MultiPathFinderImpl();
        pf.setNbTileX(200);
        pf.setNbTileY(300);
        pf.setAllowDiagonal(true);

        URL url = TableJauneTest.class.getClass().getResource("/assets/jaune.png");
        imgSource = new File(url.getPath());
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_MANHATTAN);
    }

    @Before
    public void beforeTest() {
        pf.construitGraphDepuisImageNoirEtBlanc(imgSource);
    }

    @Test
    public void testFindPath() throws IOException, NoPathFoundException {
        Point from = new Point(149, 150);
        Point to = new Point(170, 100);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }
}
