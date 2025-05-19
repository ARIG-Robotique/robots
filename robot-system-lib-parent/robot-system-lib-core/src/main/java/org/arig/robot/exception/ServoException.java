package org.arig.robot.exception;

public class ServoException extends RuntimeException {

  public ServoException(final String servoName) {
    super("Mouvement impossible du servo " + servoName);
  }

  public ServoException(Throwable cause) {
    super("Mouvement impossible", cause);
  }
}
