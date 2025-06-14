package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.StrategyOption;
import org.arig.robot.model.Team;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranState;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class EcranService extends AbstractEcranService<EcranConfig, EcranState> {

  public EcranService() {
    super(new EcranState());
  }

  @Override
  public void updateStateInfo(EcranState stateInfos) {
    super.updateStateInfo(stateInfos);
  }

  @Override
  public EcranConfig config() {
    EcranConfig config = super.config();
    if (config == null) {
      config = new EcranConfig();
    }
    return config;
  }

  @Override
  protected EcranParams getParams() {
    EcranParams ecranParams = new EcranParams();
    ecranParams.setTeams(Map.of(
      Team.JAUNE.name(), "yellow",
      Team.BLEU.name(), "blue"
    ));
    ecranParams.setStrategies(Stream.of(Strategy.values()).map(Strategy::description).collect(Collectors.toList()));
    ecranParams.setOptions(Stream.of(StrategyOption.values()).map(s -> s.description() + (s.defaultValue() ? "|1" : "")).collect(Collectors.toList()));
    return ecranParams;
  }
}
