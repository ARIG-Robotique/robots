package org.arig.robot.model;

import lombok.Getter;

@Getter
public class JournalEvent {

    private final EStatusEvent event;
    private final String val;

    public JournalEvent(EStatusEvent event, String val) {
        this.event = event;
        this.val = val;
    }

}
