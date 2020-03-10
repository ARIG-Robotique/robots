package org.arig.test.robot;

import org.arig.robot.model.Point;
import org.arig.robot.services.avoiding.AvoidingUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AvoidingUtilsTest {

    @Test
    public void testCalculateCentroidObs() {

        List<Point> points = new ArrayList<>() {{
            add(new Point(45, 55));
            add(new Point(51, 45));
            add(new Point(51, 40));
            add(new Point(50,50));
        }};

        List<Point> result = AvoidingUtils.calculateCenterObs(points);

        Assert.assertEquals("Il y a qu'un seul centroid" , 1, result.size());
    }

    @Test
    public void testCalculateCentroidLultiObs() {

        List<Point> points = new ArrayList<>() {{
            add(new Point(45, 55));
            add(new Point(51, 45));
            add(new Point(51, 40));
            add(new Point(50,50));

            add(new Point(200,200));
        }};

        List<Point> result = AvoidingUtils.calculateCenterObs(points);

        Assert.assertEquals("Il y a 2 centroids" , 2, result.size());
    }

    @Test
    public void testCalculateCentroidLultiObsWithShortDist() {

        List<Point> points = new ArrayList<>() {{
            add(new Point(35, 35));
            add(new Point(19, 45));
            add(new Point(15, 53));
            add(new Point(20, 40));
            add(new Point(22, 40));
            add(new Point(25, 40));
            add(new Point(19,50));
        }};

        List<Point> result = AvoidingUtils.calculateCenterObs(points);

        Assert.assertEquals("Il y a 1 centroid" , 1, result.size());
    }
}
