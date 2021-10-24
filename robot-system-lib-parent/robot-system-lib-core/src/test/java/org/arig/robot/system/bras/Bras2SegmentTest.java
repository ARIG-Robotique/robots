package org.arig.robot.system.bras;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point3D;
import org.arig.robot.system.bras.impl.Bras2SegmentImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 04/01/14.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
public class Bras2SegmentTest {

    private static Bras2SegmentImpl impl;

    @BeforeAll
    public static void initClass() {
        impl = new Bras2SegmentImpl(new Point3D(0, 0, 1.5), 7, 3);
    }

    @Test
    public void testToP() {
        impl.toP(new Point3D(8, 0, 0));
    }
}
