package org.arig.robot.strategy;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.Point;

import java.time.LocalDateTime;

public abstract class AbstractAction implements IAction {

    @Getter
    @Accessors(fluent = true)
    private String uuid = java.util.UUID.randomUUID().toString();

    @Getter
    @Setter
    private LocalDateTime validTime = LocalDateTime.now();

    @Getter
    private boolean completed = false;

    protected abstract Point entryPoint();

    protected boolean isTimeValid() {
        return validTime.isBefore(LocalDateTime.now());
    }

    protected void updateValidTime() {
        setValidTime(LocalDateTime.now().plusSeconds(2));
    }

    protected void complete() { completed = true; }

}
