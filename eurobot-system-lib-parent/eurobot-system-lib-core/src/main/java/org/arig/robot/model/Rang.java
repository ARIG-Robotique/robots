package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Rang {
  RANG_1((byte) 0),
  RANG_2((byte) 1),
  RANG_3((byte) 2);

  final byte idx;

  private static Rang fromIdx(byte rang) {
    return switch (rang) {
      case 0 -> RANG_1;
      case 1 -> RANG_2;
      case 2 -> RANG_3;
      default -> throw new IllegalArgumentException("Invalid rang index: " + rang);
    };
  }
}
