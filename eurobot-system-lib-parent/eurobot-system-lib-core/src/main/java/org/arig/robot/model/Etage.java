package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Etage {
  ETAGE_1((byte) 0),
  ETAGE_2((byte) 1),
  ETAGE_3((byte) 2);

  final byte idx;

  static Etage fromIdx(byte etage) {
    return switch (etage) {
      case 0 -> ETAGE_1;
      case 1 -> ETAGE_2;
      case 2 -> ETAGE_3;
      default -> throw new IllegalArgumentException("Invalid etage index: " + etage);
    };
  }

  public Etage next() {
    return switch (this) {
      case ETAGE_1 -> ETAGE_2;
      case ETAGE_2 -> ETAGE_3;
      case ETAGE_3 -> throw new IllegalStateException("No more etage");
    };
  }
}
