package org.arig.test.robot.model;

import org.arig.robot.model.AbstractRobotStatus;

import java.util.Collections;
import java.util.Map;

public class TestRobotStatus extends AbstractRobotStatus {

    public TestRobotStatus() {
        super(0);
    }

    @Override
    public int calculerPoints() {
        return 2;
    }

    @Override
    public Map<String, ?> gameStatus() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        return Collections.emptyMap();
    }
}
