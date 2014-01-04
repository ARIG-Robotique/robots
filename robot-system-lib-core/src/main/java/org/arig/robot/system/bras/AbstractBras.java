package org.arig.robot.system.bras;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Created by mythril on 04/01/14.
 */
@ToString
@EqualsAndHashCode
public abstract class AbstractBras implements IBrasManager {

    @Getter(AccessLevel.PROTECTED)
    private final int nbSegment;

    @Getter(AccessLevel.PROTECTED)
    private double [] longeurSegment;

    protected AbstractBras(int nbSegment, double [] longeurSegment) {
        Assert.isTrue(nbSegment == longeurSegment.length);

        this.nbSegment = nbSegment;
        this.longeurSegment = longeurSegment;
    }
}
