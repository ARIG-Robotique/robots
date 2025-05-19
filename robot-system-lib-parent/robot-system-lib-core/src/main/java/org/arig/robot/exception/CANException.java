package org.arig.robot.exception;

/**
 * The Class I2CException.
 *
 * @author gdepuille
 */
public class CANException extends Exception {


  public CANException() {
    super();
  }

  public CANException(final String message, final Throwable cause,
                      final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public CANException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public CANException(final String message) {
    super(message);
  }

  public CANException(final Throwable cause) {
    super(cause);
  }

}
