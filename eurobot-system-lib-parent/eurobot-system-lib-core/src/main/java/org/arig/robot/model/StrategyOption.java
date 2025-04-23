package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum StrategyOption {
  OPTION_1("Option 1"),
  OPTION_2("Option 2");

  @Getter
  @Accessors(fluent = true)
  private final String description;

}
