package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.vo.Point;

import java.util.List;

/**
 * @author gdepuille on 08/05/15.
 */
@Slf4j
@Data
public abstract class AbstractRobotStatus {

    @Setter(AccessLevel.NONE)
    private boolean asservEnabled = false;

    public void enableAsserv() {
        log.info("Activation asservissement");
        asservEnabled = true;
    }

    public void disableAsserv() {
        log.info("Désactivation asservissement");
        asservEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean avoidanceEnabled = false;

    public void enableAvoidance() {
        log.info("Activation evittement");
        avoidanceEnabled = true;
    }

    public void disableAvoidance() {
        log.info("Désactivation evittement");
        avoidanceEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean matchEnabled = false;

    public void enableMatch() {
        matchEnabled = true;
    }

    public void disableMatch() {
        matchEnabled = false;
    }

    public abstract List<Point> echappementPointsCm();
}
