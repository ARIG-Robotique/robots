package org.arig.robot.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestRobotStatus extends AbstractRobotStatus<ETestStatusEvent> {

    public TestRobotStatus() {
        super(0, true, ETestStatusEvent.class);
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

    @Override
    public void writeStatus(ObjectOutputStream os) throws IOException {

    }

    @Override
    public void readStatus(ObjectInputStream is) throws IOException {

    }

    @Override
    public void integrateJournal(List<EventLog<ETestStatusEvent>> journal, boolean self) {

    }
}
