package org.arig.robot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.balise.StatutBalise;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus implements InitializingBean {

    @Setter(AccessLevel.NONE)
    private boolean simulateur = false;

    public void setSimulateur() {
        simulateur = true;
    }

    private boolean finale = false;

    private Team team = Team.UNKNOWN;

    private List<EStrategy> strategies = new ArrayList<>();

    public boolean strategyActive(EStrategy strategy) {
        return strategies.contains(strategy);
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

    public long getElapsedTime() {
        return matchTime.getTime();
    }

    public long getRemainingTime() {
        return Math.max(0, IConstantesNerellConfig.matchTimeMs - matchTime.getTime());
    }

    @Setter(AccessLevel.NONE)
    private EModeCalage calageBordure = null;

    @Setter(AccessLevel.NONE)
    private double calageBordureDistance = 0;

    public void enableCalageBordureArriere() {
        log.info("Activation calage bordure arrière");
        calageBordure = EModeCalage.ARRIERE;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordure = null;
    }

    public void enableCalageBordureAvant(double dst) {
        log.info("Activation calage bordure avant, {}mm", dst);
        calageBordure = EModeCalage.AVANT;
        calageBordureDistance = dst;
    }

    private boolean baliseOk = false;
    private StatutBalise statutBalise = null;

    /**
     * INIT
     */
    @Override
    public void afterPropertiesSet() {

    }

    public int calculerPoints() {
        return 0;
    }

    public double scoreFinal() {
        int points = calculerPoints();
        double score = points;
        score += 0.3 * points; // evaluation
        score += 10; // bonus;
        score += finale ? 30 : 0;
        return score;
    }

}
