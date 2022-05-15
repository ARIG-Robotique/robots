package org.arig.robot.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.model.enums.TypeCalage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Flag d'attente de la tirette
     */
    private boolean waitTirette = false;

    /**
     * Indication de fonctionnement pour le thread principal
     */
    @Setter(AccessLevel.NONE)
    private boolean mainThread = false;

    public void enableMainThread() {
        log.info("Activation du thread principal de gestion de tâche");
        mainThread = true;
    }

    public void disableMainThread() {
        log.info("Desactivation du thread principal de gestion de tâche");
        mainThread = false;
    }

    /**
     * Il y'a deux robots sur la table
     */
    private boolean twoRobots = false;

    public boolean twoRobots() {
        return groupOk ? true : twoRobots;
    }

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
    private List<TypeCalage> calage = new ArrayList<>(3);

    private List<TypeCalage> calageCompleted = new ArrayList<>(3);

    public void enableCalageBordure(TypeCalage main, TypeCalage ... others) {
        calageCompleted.clear();
        calage.clear();
        calage.add(main);
        if (others != null) {
            calage.addAll(Arrays.asList(others));
        }
        log.info("Activation calage bordure : {}", calage.stream()
                .map(Enum::name).collect(Collectors.joining(", ")));
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calage.clear();
    }

    public void refreshState() {
        // To be overidded
    }

    private String currentAction = null;

    private String otherCurrentAction = null;

    @Setter(AccessLevel.PRIVATE)
    private Point otherPosition = null;

    public void otherPosition(int x, int y) {
        if (otherPosition == null) {
            otherPosition = new Point(x, y);
        } else {
            otherPosition.setX(x);
            otherPosition.setY(y);
        }
    }

    public abstract int calculerPoints();

    public abstract Map<String, ?> gameStatus();

    public abstract Map<String, Boolean> gameFlags();

    public abstract Map<String, Integer> scoreStatus();

}
