package org.arig.robot.strategy;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 23/05/17.
 */
public abstract class AbstractAction implements IAction {

    @Getter
    private String UUID = java.util.UUID.randomUUID().toString();

    @Getter
    @Setter
    private LocalDateTime validTime = LocalDateTime.now();

    protected boolean isTimeValid() {
        return validTime.isBefore(LocalDateTime.now());
    }

    protected void updateValidTime() {
        setValidTime(LocalDateTime.now().plusSeconds(2));
    }

}
