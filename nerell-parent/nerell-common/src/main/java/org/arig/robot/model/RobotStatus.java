package org.arig.robot.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    private ETeam team = ETeam.UNKNOWN;

    public void setTeam(int value) {
        switch (value) {
            case 1 : team = ETeam.JAUNE;break;
            case 2 : team = ETeam.BLEU;break;
            default: team = ETeam.UNKNOWN;
        }
    }

    private EStrategy strategy = EStrategy.BASIC;

    public void setStrategy(int value) {
        switch (value) {
            case 1 : strategy = EStrategy.AGGRESSIVE;break;
            case 2 : strategy = EStrategy.FINALE;break;
            default: strategy = EStrategy.BASIC;
        }
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

    private ECouleurBouee[] couleursEcueil = new ECouleurBouee[]{ECouleurBouee.VERT, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.ROUGE};

    private int ecueilAdverseDispo = 0;

    @Setter(AccessLevel.NONE)
    private boolean pincesEnabled = false;

    public void enablePinces() {
        pincesEnabled = true;
    }

    public void disablePinces() {
        pincesEnabled = false;
    }

    /**
     * STATUT
     */
    @Setter
    @Accessors(fluent = true)
    boolean mancheAAir1 = false;

    @Setter
    @Accessors(fluent = true)
    boolean mancheAAir2 = false;

    @Setter
    @Accessors(fluent = true)
    boolean phare = false;

    @Setter
    @Accessors(fluent = true)
    boolean bonPort = false;

    @Setter
    @Accessors(fluent = true)
    boolean mauvaisPort = false;

    @Setter
    @Accessors(fluent = true)
    boolean pavillon = false;

    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    List<ECouleurBouee> grandPort = new ArrayList<>();

    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    List<ECouleurBouee> petitPort = new ArrayList<>();

    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    Chenaux grandChenaux = new Chenaux();

    @Accessors(fluent = true)
    @Setter(AccessLevel.NONE)
    Chenaux petitChenaux = new Chenaux();

    @Setter(AccessLevel.NONE)
    ECouleurBouee[] pincesArriere = new ECouleurBouee[]{null, null, null, null, null};

    @Setter(AccessLevel.NONE)
    ECouleurBouee[] pincesAvant = new ECouleurBouee[]{null, null, null, null};

    public void pinceArriere(int pos, ECouleurBouee bouee) {
        pincesArriere[pos] = bouee;
    }

    public void pinceAvant(int pos, ECouleurBouee bouee) {
        pincesAvant[pos] = bouee;
    }

    public void clearPincesArriere() {
        Arrays.fill(pincesArriere, null);
    }

    public void clearPincesAvant() {
        Arrays.fill(pincesAvant, null);
    }

    public boolean pincesArriereEmpty() {
        return Arrays.stream(pincesArriere).filter(Objects::nonNull).count() == 0;
    }

    public boolean pincesAvantEmpty() {
        return Arrays.stream(pincesAvant).filter(Objects::nonNull).count() == 0;
    }

    /**
     * INIT
     */
    @Override
    public void afterPropertiesSet() {

    }

    public int calculerPoints() {
        int points = 2 + (phare ? 13 : 0); // phare
        points += grandPort.size();
        points += petitPort.size();
        points += grandChenaux.score();
        points += petitChenaux.score();
        points += (mancheAAir1 && mancheAAir2) ? 15 : (mancheAAir1 || mancheAAir2) ? 5 : 0;
        points += bonPort ? 10 : (mauvaisPort ? 5 : 0);
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
}
