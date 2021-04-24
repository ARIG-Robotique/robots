package org.arig.robot.strategy;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;

public abstract class AbstractAction implements IAction {

    @Autowired
    protected TrajectoryManager mv;

    @Autowired
    protected ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    protected Position position;

    @Autowired
    protected TableUtils tableUtils;

    @Getter
    @Accessors(fluent = true)
    private String uuid = java.util.UUID.randomUUID().toString();

    @Getter
    @Setter
    private LocalDateTime validTime = LocalDateTime.now();

    @Getter
    private boolean completed = false;

    public abstract Point entryPoint();

    protected boolean isTimeValid() {
        return validTime.isBefore(LocalDateTime.now());
    }

    protected void updateValidTime() {
        setValidTime(LocalDateTime.now().plusSeconds(2));
    }

    protected void complete() { completed = true; }

}
