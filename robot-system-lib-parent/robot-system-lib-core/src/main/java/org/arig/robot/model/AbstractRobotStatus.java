package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class AbstractRobotStatus {

    @Setter(AccessLevel.NONE)
    private boolean forceMonitoring = false;

    public void enableForceMonitoring() {
        log.warn("Activation du monitoring en dehors du match");
        forceMonitoring = true;
    }

    public void disableForceMonitoring() {
        log.warn("Desactivation du monitoring en dehors du match");
        forceMonitoring = false;
    }

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
    private boolean captureEnabled = false;

    public void enableCapture() {
        log.info("Activation capture");
        captureEnabled = true;
    }

    public void disableCapture() {
        log.info("Désactivation capture");
        captureEnabled = false;
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

    public abstract long getElapsedTime();

    public abstract long getRemainingTime();

    public abstract int calculerPoints();

}
