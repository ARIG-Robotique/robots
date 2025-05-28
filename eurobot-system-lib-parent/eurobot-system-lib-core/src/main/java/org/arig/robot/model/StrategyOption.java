package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum StrategyOption {
  LIMITER_2_ETAGES("Limiter à 2 étages", true),
  EVITER_COTE_ADVERSE("Eviter coté adverse", true),
  EJECTION_COUP_DE_PUTE("Ejection coup de pute", false);

  @Getter
  @Accessors(fluent = true)
  private final String description;

  @Getter
  @Accessors(fluent = true)
  private final boolean defaultValue;

}
