package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
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
    private boolean calageBordure = false;

    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordure = true;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordure = false;
    }

    private DirectionGirouette directionGirouette = DirectionGirouette.UNKNOWN;

    private List<ECouleurBouee> couleursEccueil = null;

    private boolean eccueilAdverseDispo = true;

    /**
     * STATUT
     */
    @Setter
    boolean mancheAAir1 = false;

    @Setter
    boolean mancheAAir2 = false;

    @Setter
    boolean bonPort = false;

    @Setter
    boolean pavillon = false;

    List<ECouleurBouee> grandPort = new ArrayList<>();

    List<ECouleurBouee> petitPort = new ArrayList<>();

    List<ECouleurBouee> grandChenalVert = new ArrayList<>();

    List<ECouleurBouee> grandChenalRouge = new ArrayList<>();

    List<ECouleurBouee> petitChenalVert = new ArrayList<>();

    List<ECouleurBouee> petitChenalRouge = new ArrayList<>();


    /**
     * INIT
     */
    @Override
    public void afterPropertiesSet() {

    }

    public int calculerPoints() {
        int points = 15; // phare
        points += grandPort.size();
        points += petitPort.size();
        points += pointsChenaux(grandChenalRouge, grandChenalVert);
        points += pointsChenaux(petitChenalRouge, petitChenalVert);
        points += (mancheAAir1 && mancheAAir2) ? 15 : (mancheAAir1 || mancheAAir2) ? 5 : 0;
        points += bonPort ? 10 : 0;
        points += pavillon ? 10 : 0;
        return points;
    }

    public double scoreFinal() {
        int points = calculerPoints();
        double score = points;
        score += 0.3 * points; // evaluation
        score += 5; // bonus;
        return score;
    }

    private int pointsChenaux(List<ECouleurBouee> chenalRouge, List<ECouleurBouee> chenalVert) {
        long nbBoueeOkRouge = chenalRouge.stream()
                .filter(b -> b == ECouleurBouee.ROUGE)
                .count();

        long nbBoueeOkVert = chenalVert.stream()
                .filter(b -> b == ECouleurBouee.VERT)
                .count();

        return (int) (nbBoueeOkRouge + nbBoueeOkVert + Math.min(nbBoueeOkRouge, nbBoueeOkVert) * 2);
    }

}
