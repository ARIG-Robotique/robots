package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum StrategyOption {
  LIMITER_2_ETAGES("Limiter à 2 étages");

  @Getter
  @Accessors(fluent = true)
  private final String description;

}
