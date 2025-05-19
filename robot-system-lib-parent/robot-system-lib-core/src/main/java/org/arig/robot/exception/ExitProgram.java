package org.arig.robot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExitProgram extends RuntimeException {

  @Getter
  private final boolean wait;

}
