package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.balise.enums.ZoneMines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public abstract class EurobotStatus extends AbstractRobotStatus {

    protected EurobotStatus(boolean mainRobot) {
        super(EurobotConfig.matchTimeMs, mainRobot, false);
    }

    protected EurobotStatus(boolean mainRobot, boolean pamiRobot) {
        super(EurobotConfig.matchTimeMs, mainRobot, pamiRobot);
    }

    private Team team;

    private Strategy strategy = Strategy.QUALIF;

    @Override
    public String strategyDescription() {
        return strategy == null ? "Aucune stratégie" : strategy.description();
    }

    /**
     * CONFIGURATION
     */

    private boolean limit2Etages = false;

    /**
     * STATUT
     */

    @Setter(AccessLevel.NONE)
    private boolean banderolleDansRobot = false;

    @Setter(AccessLevel.NONE)
    private boolean banderolleDeployee = false;

    @Setter(AccessLevel.NONE)
    private boolean faceAvantFull = false;

    @Setter(AccessLevel.NONE)
    private boolean faceArriereFull = false;

    private GradinBrutStocks gradinBrutStocks = new GradinBrutStocks();

    @Setter(AccessLevel.NONE)
    private ConstructionArea grandGradinEquipe = new ConstructionArea("Grand gradin coté équipe", (byte) 3);
    @Setter(AccessLevel.NONE)
    private ConstructionArea petitGradinEquipe = new ConstructionArea("Petit gradin coté équipe");
    @Setter(AccessLevel.NONE)
    private ConstructionArea grandGradinAdverse = new ConstructionArea("Grand gradin coté adverse", (byte) 3);
    @Setter(AccessLevel.NONE)
    private ConstructionArea petitGradinAdverse = new ConstructionArea("Petit gradin coté adverse");

    private BackstageState backstage = BackstageState.OUTSIDE;

    private List<ZoneMines> mines = new ArrayList<>();

    public void banderolleDeployee(boolean banderolleDeployee) {
        log.info("[RS] Banderolle déployée : {}", banderolleDeployee);
        this.banderolleDeployee = banderolleDeployee;
    }

    public void faceAvantFull(boolean faceAvantFull) {
        log.info("[RS] Face avant full : {}", faceAvantFull);
        this.faceAvantFull = faceAvantFull;
    }

    public void faceArriereFull(boolean faceArriereFull) {
        log.info("[RS] Face arriere full : {}", faceArriereFull);
        this.faceArriereFull = faceArriereFull;
    }

    public void backstage(BackstageState backstage) {
        log.info("[RS] Backstage : {}", backstage);
        this.backstage = backstage;
    }

    /* *************************** SCORE *************************** */
    /* ************************************************************* */

    private int scoreGradins() {
        return grandGradinEquipe.score() +
            petitGradinEquipe.score() +
            grandGradinAdverse.score() +
            petitGradinAdverse.score();
    }

    private int scoreBanderolle() {
        return banderolleDeployee ? 20 : 0;
    }

    private int scoreRetourBackstage() {
        return backstage == BackstageState.TARGET_REACHED ? 10 : 0;
    }

    public int calculerPoints() {
        int points = 0;

        // le robot secondaire ne compte pas les points si la comm est ok
        // le pami ne compte pas les points tous le temps
        if ((robotGroupOk() && !mainRobot()) || pamiRobot()) {
            return points;
        }

        points += scoreGradins();
        points += scoreBanderolle();
        points += scoreRetourBackstage();

        return points;
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        Map<String, Integer> r = new HashMap<>();
        r.put("Grand gradin equipe", grandGradinEquipe.score());
        r.put("Petit gradin equipe", petitGradinEquipe.score());
        r.put("Grand gradin adverse", grandGradinAdverse.score());
        r.put("Petit gradin adverse", petitGradinAdverse.score());
        r.put("Banderolle", scoreBanderolle());
        r.put("Retour backstage", scoreRetourBackstage());
        return r;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = new HashMap<>();
        r.put("gradinBrutStock", gradinBrutStocks.data);
        r.put("airesConstruction",
            Map.of(
                "grandEquipe", grandGradinEquipe.data(),
                "petitEquipe", petitGradinEquipe.data(),
                "grandAdverse", grandGradinAdverse.data(),
                "petitAdverse", petitGradinAdverse.data()
            )
        );
        return r;
    }

    @Override
    public Map<String, Boolean> gameFlags() {
        Map<String, Boolean> r = new HashMap<>();
        r.put("Banderolle dans le robot", banderolleDansRobot);
        r.put("Banderolle déployé", banderolleDeployee);
        r.put("Face avant full", faceAvantFull);
        r.put("Face arriere full", faceArriereFull);
        return r;
    }

    @Override
    public Map<String, Boolean> gameConfigs() {
        Map<String, Boolean> r = new HashMap<>();
        r.put("Limit 2 étages", limit2Etages);
        return r;
    }

    @Override
    public Map<String, String> deposesStatus() {
        Map<String, String> r = new HashMap<>();
        r.put("Grand gradin equipe", grandGradinEquipe.toString());
        r.put("Petit gradin equipe", petitGradinEquipe.toString());
        r.put("Grand gradin adverse", grandGradinAdverse.toString());
        r.put("Petit gradin adverse", petitGradinAdverse.toString());
        return r;
    }
}
