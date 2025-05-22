package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Rang {
  RANG_1((byte) 0),
  RANG_2((byte) 1),
  RANG_3((byte) 2),
  CONSTRUCTION((byte) 3);

  final byte idx;

  static Rang fromIdx(byte rang) {
    return switch (rang) {
      case 0 -> RANG_1;
      case 1 -> RANG_2;
      case 2 -> RANG_3;
      default -> throw new IllegalArgumentException("Invalid rang index: " + rang);
    };
  }

  public boolean before(Rang rang2) {
    return this.idx < rang2.idx;
  }

  boolean after(Rang rang2) {
    return this.idx > rang2.idx;
  }
}
