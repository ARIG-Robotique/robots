package org.arig.robot.strategy;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.Point;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public abstract class AbstractAction implements IAction {

    @Getter
    @Accessors(fluent = true)
    private String uuid = java.util.UUID.randomUUID().toString();

    @Getter
    @Setter
    private LocalDateTime validTime = LocalDateTime.now();

    @Getter
    private boolean completed = false;

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Collections.emptyList();

    public abstract Point entryPoint();

    protected boolean isTimeValid() {
        return validTime.isBefore(LocalDateTime.now());
    }

    protected void updateValidTime() {
        setValidTime(LocalDateTime.now().plusSeconds(2));
    }

    protected void complete() { completed = true; }

}
