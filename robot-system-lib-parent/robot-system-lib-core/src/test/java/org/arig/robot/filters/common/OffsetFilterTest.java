package org.arig.robot.filters.common;

import org.arig.robot.filters.common.OffsetFilter.OffsetType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class OffsetFilterTest {

    private static OffsetFilter simpleOffset;
    private static OffsetFilter mirrorOffset;

    @BeforeAll
    public static void initClass() {
        simpleOffset = new OffsetFilter(10d);
        mirrorOffset = new OffsetFilter(10d, OffsetType.MIRROR);
    }

    @BeforeEach
    public void beforeTest() {
        simpleOffset.reset();
        mirrorOffset.reset();
    }

    @Test
    public void testSimpleFilter() {
        Assertions.assertEquals(9d, simpleOffset.filter(-1d), 0d);
        Assertions.assertEquals(20d, simpleOffset.filter(10d), 0d);
        Assertions.assertEquals(20d, simpleOffset.filter(10d), 0d);
        Assertions.assertEquals(19d, simpleOffset.filter(9d), 0d);
    }

    @Test
    public void testMirrorFilter() {
        Assertions.assertEquals(-11d, mirrorOffset.filter(-1d), 0d);
        Assertions.assertEquals(20d, mirrorOffset.filter(10d), 0d);
        Assertions.assertEquals(-20d, mirrorOffset.filter(-10d), 0d);
        Assertions.assertEquals(11d, mirrorOffset.filter(1d), 0d);
    }
}
