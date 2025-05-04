package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum StrategyOption {
  LIMIT_2_ETAGES("Limit à 2 étages");

  @Getter
  @Accessors(fluent = true)
  private final String description;

}
