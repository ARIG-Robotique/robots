package org.arig.robot.exception;

public class MovementCancelledException extends AvoidingException {

  public MovementCancelledException() {
    super("Annulation du mouvement");
  }

}
