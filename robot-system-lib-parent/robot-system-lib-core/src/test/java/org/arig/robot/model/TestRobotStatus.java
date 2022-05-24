package org.arig.robot.model;

import java.util.Collections;
import java.util.Map;

public class TestRobotStatus extends AbstractRobotStatus {

    public TestRobotStatus() {
        super(0, true);
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
    public Map<String, Boolean> gameFlags() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> deposesStatus() {
        return Collections.emptyMap();
    }
}
