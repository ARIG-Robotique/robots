package org.arig.robot.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GrandChenaux extends Chenaux {
    @Override
    protected Chenaux newInstance() {
        return new GrandChenaux();
    }
}
