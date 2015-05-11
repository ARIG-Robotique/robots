package org.arig.test.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;
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
 * Created by mythril on 30/12/13.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class TableJauneTest {

    private static MultiPathFinderImpl pf;

    private static File dir;
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

        String tmpDir = System.getProperty("java.io.tmpdir");
        dir = new File(tmpDir + "/arig/robot/pathTableJaune");
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
    public void testFindPath() throws IOException, NoPathFoundException {
        Point from = new Point(149, 150);
        Point to = new Point(177, 110);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());
    }
}
