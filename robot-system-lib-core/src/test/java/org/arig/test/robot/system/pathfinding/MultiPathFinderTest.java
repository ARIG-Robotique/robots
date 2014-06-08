package org.arig.test.robot.system.pathfinding;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ImageUtils;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by mythril on 30/12/13.
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
    }

    @Before
    public void beforeTest() {
        URL url = getClass().getResource("/assets/labyrinthe.png");
        pf.makeGraphFromBWImage(new File(url.getPath()));

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

        saveFile(c, new File(dir, "outputAStarManhattan.png"));
    }

    @Test
    public void testFindPathAStarEuclidian() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.A_STAR_EUCLIDIAN);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());

        saveFile(c, new File(dir, "outputAStarEuclidian.png"));
    }

    @Test
    public void testFindPathDijkstra() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.DIJKSTRA);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());

        saveFile(c, new File(dir, "outputDijkstra.png"));
    }

    @Test
    public void testFindPathBreadthFirstSearch() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.BREADTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());

        saveFile(c, new File(dir, "outputBreadthFirstSearch.png"));
    }

    @Test
    public void testFindPathDepthFirstSearch() throws IOException, NoPathFoundException {
        pf.setAlgorithm(PathFinderAlgorithm.DEPTH_FIRST_SEARCH);
        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());

        saveFile(c, new File(dir, "outputDepthFirstSearch.png"));
    }

    private void saveFile(Chemin c, File f) throws IOException {
        URL url = getClass().getResource("/assets/labyrinthe.png");
        BufferedImage img = ImageUtils.mirrorX(ImageIO.read(new File(url.getPath())));
        Graphics2D g = img.createGraphics();
        g.setBackground(Color.WHITE);
        Point currentPoint = null;
        Point precedencePoint = null;
        while(c.hasNext()) {
            // Couleur du premier et des autres points
            g.setColor((currentPoint == null) ? Color.RED : Color.BLACK);
            currentPoint = c.next();

            // Couleur du dernier point
            if (!c.hasNext()) {
               g.setColor(Color.GREEN);
            }
            if (precedencePoint != null) {
                Color back = g.getColor();

                g.setColor(Color.BLUE);
                g.drawLine((int) precedencePoint.getX(), (int) precedencePoint.getY(),
                        (int) currentPoint.getX(), (int) currentPoint.getY());

                g.setColor(back);
            }

            g.fillOval((int) currentPoint.getX() - 5, (int) currentPoint.getY() - 5, 10, 10);

            precedencePoint = currentPoint;
        }
        g.dispose();

        ImageIO.write(ImageUtils.mirrorX(img), "png", f);
    }
}
