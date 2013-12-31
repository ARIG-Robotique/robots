package org.arig.test.robot.system.pathfinding;

import org.arig.robot.system.pathfinding.PathFinderAlgorithm;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.utils.ImageUtils;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by mythril on 30/12/13.
 */
public class MultiPathFinderTest {

    private static MultiPathFinderImpl pf;

    @BeforeClass
    public static void initClass() {

        pf = new MultiPathFinderImpl(PathFinderAlgorithm.A_STAR_MANHATTAN);
        pf.setNbTileX(20);
        pf.setNbTileY(20);
        pf.setAllowDiagonal(true);
    }

    @Before
    public void beforeTest() {
        URL url = getClass().getResource("/assets/labyrinthe.png");
        pf.makeGraphFromBWImage(new File(url.getPath()));
    }

    @Test
    public void testFindPath() throws IOException  {
        Point from = new Point(25, 25);
        Point to = new Point(85, 185);

        Chemin c = pf.findPath(from, to);
        Assert.assertNotNull(c);
        Assert.assertTrue(c.hasNext());


        URL url = getClass().getResource("/assets/labyrinthe.png");
        BufferedImage img = ImageUtils.mirrorX(ImageIO.read(new File(url.getPath())));
        Graphics2D g = img.createGraphics();
        g.setBackground(Color.WHITE);
        g.setColor(Color.BLUE);
        while(c.hasNext()) {
            Point p = c.next();
            g.fillOval((int) p.getX(), (int) p.getY(), 10, 10);
        }
        g.dispose();
        
        ImageIO.write(ImageUtils.mirrorX(img), "png", new File("C:/Users/GregoryDepuille/Desktop/outputPath.png"));
    }
}
