package org.arig.test.robot.system.bras;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.bras.impl.Bras2SegmentImpl;
import org.arig.robot.vo.Point3D;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * @author gdepuille on 04/01/14.
 */
@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class Bras2SegmentTest {

    private static Bras2SegmentImpl impl;

    @BeforeClass
    public static void initClass() {
        impl = new Bras2SegmentImpl(new Point3D(0, 0, 1.5), 7, 3);
    }

    @Test
    public void testToP() {
        impl.toP(new Point3D(8, 0, 0));
    }
}
