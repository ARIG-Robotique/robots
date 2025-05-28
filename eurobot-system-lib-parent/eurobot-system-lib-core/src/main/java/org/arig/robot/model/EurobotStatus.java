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

  @Setter(AccessLevel.NONE)
  private Team team;

  public void team(Team team) {
    if (this.team != team) {
      log.info("[RS] Team : {} -> {}", this.team != null ? this.team.name() : "UNKNOWN", team != null ? team.name() : "UNKNOWN");
      this.team = team;
    }
  }

  @Setter(AccessLevel.NONE)
  private Strategy strategy = Strategy.QUALIF;

  public void strategy(Strategy strategy) {
    if (this.strategy != strategy) {
      log.info("[RS] Strategy : {} ({}) -> {} ({})",
        this.strategy.name(), this.strategy.description(),
        strategy.name(), strategy.description());
      this.strategy = strategy;
    }
  }

  @Override
  public String strategyDescription() {
    return strategy == null ? "Aucune stratégie" : strategy.description();
  }

  /**
   * CONFIGURATION
   */

  @Setter(AccessLevel.NONE)
  private boolean limiter2Etages = StrategyOption.LIMITER_2_ETAGES.defaultValue();

  public void limiter2Etages(boolean limiter2Etages) {
    if (this.limiter2Etages != limiter2Etages) {
      log.info("[RS] Limiter à 2 étages : {} -> {}", this.limiter2Etages, limiter2Etages);
      this.limiter2Etages = limiter2Etages;
    }
  }

  @Setter(AccessLevel.NONE)
  private boolean eviterCoteAdverse = StrategyOption.EVITER_COTE_ADVERSE.defaultValue();

  public void eviterCoteAdverse(boolean eviterCoteAdverse) {
    if (this.eviterCoteAdverse != eviterCoteAdverse) {
      log.info("[RS] Eviter coté adverse : {} -> {}", this.eviterCoteAdverse, eviterCoteAdverse);
      this.eviterCoteAdverse = eviterCoteAdverse;
    }
  }

  @Setter(AccessLevel.NONE)
  private boolean ejectionCoupDePute = StrategyOption.EJECTION_COUP_DE_PUTE.defaultValue();

  public void ejectionCoupDePute(boolean ejectionCoupDePute) {
    if (this.ejectionCoupDePute != ejectionCoupDePute) {
      log.info("[RS] Ejection coup de pute : {} -> {}", this.ejectionCoupDePute, ejectionCoupDePute);
      this.ejectionCoupDePute = ejectionCoupDePute;
    }
  }

  /**
   * STATUT
   */

  @Setter(AccessLevel.NONE)
  private boolean banderoleDeployee = false;

  private final StockFace faceAvant = new StockFace();
  private final StockFace faceArriere = new StockFace();
  private final GradinBrutStocks gradinBrutStocks = new GradinBrutStocks();

  @Setter(AccessLevel.NONE)
  private final ConstructionArea grandGradinEquipe = new ConstructionArea("Grand gradin coté équipe", (byte) 3);
  @Setter(AccessLevel.NONE)
  private final ConstructionArea petitGradinEquipe = new ConstructionArea("Petit gradin coté équipe");
  @Setter(AccessLevel.NONE)
  private final ConstructionArea grandGradinAdverse = new ConstructionArea("Grand gradin coté adverse", (byte) 3);
  @Setter(AccessLevel.NONE)
  private final ConstructionArea petitGradinAdverse = new ConstructionArea("Petit gradin coté adverse");

  private BackstageState backstage = BackstageState.OUTSIDE;

  private List<ZoneMines> mines = new ArrayList<>();

  public void banderoleDeployee(boolean banderoleDeployee) {
    if (this.banderoleDeployee != banderoleDeployee) {
      log.info("[RS] Banderole déployée : {}", banderoleDeployee);
      this.banderoleDeployee = banderoleDeployee;
    }
  }

  public void backstage(BackstageState backstage) {
    if (this.backstage != backstage) {
      log.info("[RS] Backstage : {} -> {}", this.backstage, backstage);
      this.backstage = backstage;
    }
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
    return banderoleDeployee ? 20 : 0;
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
    r.put("faceAvant", faceAvant);
    r.put("faceArriere", faceArriere);
    return r;
  }

  @Override
  public Map<String, Boolean> gameFlags() {
    Map<String, Boolean> r = new HashMap<>();
    r.put("Banderole déployé", banderoleDeployee);
    return r;
  }

  @Override
  public Map<String, Boolean> gameConfigs() {
    Map<String, Boolean> r = new HashMap<>();
    r.put(StrategyOption.LIMITER_2_ETAGES.description(), limiter2Etages);
    r.put(StrategyOption.EVITER_COTE_ADVERSE.description(), eviterCoteAdverse);
    r.put(StrategyOption.EJECTION_COUP_DE_PUTE.description(), ejectionCoupDePute);
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
