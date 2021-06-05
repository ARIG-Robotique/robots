package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Map;

@Slf4j
@Data
@Accessors(fluent = true)
public abstract class AbstractRobotStatus {

    private final int matchTimeMs;

    /**
     * Il s'agit du robot principal
     */
    private final boolean mainRobot;

    public AbstractRobotStatus(int matchTimeMs, boolean mainRobot) {
        this.matchTimeMs = matchTimeMs;
        this.mainRobot = mainRobot;
    }

    /**
     * Il y'a deux robots sur la table
     */
    private boolean twoRobots = false;

    /**
     * Les deux robots communiquent
     */
    private boolean groupOk = false;

    private boolean simulateur = false;

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

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();

        log.info("Démarrage du match");

        // Activation
        this.enableMatch();
    }

    public void stopMatch() {
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs, et tout et tout
        this.disableAsserv();
        this.disableAvoidance();
        this.disableMatch();
        this.disableCalageBordure();
    }

    public boolean matchRunning() {
        return getElapsedTime() < matchTimeMs;
    }

    public long getElapsedTime() {
        return matchTime.getTime();
    }

    public long getRemainingTime() {
        return Math.max(0, matchTimeMs - getElapsedTime());
    }

    @Setter(AccessLevel.NONE)
    private boolean calageBordure = false;

    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordure = true;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordure = false;
    }

    private String currentAction = null;

    public abstract int calculerPoints();

    public abstract Map<String, ?> gameStatus();

    public abstract Map<String, Integer> scoreStatus();

}
