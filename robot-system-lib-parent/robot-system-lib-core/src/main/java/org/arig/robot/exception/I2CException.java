package org.arig.robot.exception;

/**
 * The Class I2CException.
 *
 * @author gdepuille
 */
public class I2CException extends Exception {

  /**
   * Instantiates a new i2 c exception.
   */
  public I2CException() {
    super();
  }

  /**
   * Instantiates a new i2 c exception.
   *
   * @param message            the message
   * @param cause              the cause
   * @param enableSuppression  the enable suppression
   * @param writableStackTrace the writable stack trace
   */
  public I2CException(final String message, final Throwable cause,
                      final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * Instantiates a new i2 c exception.
   *
   * @param message the message
   * @param cause   the cause
   */
  public I2CException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new i2 c exception.
   *
   * @param message the message
   */
  public I2CException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new i2 c exception.
   *
   * @param cause the cause
   */
  public I2CException(final Throwable cause) {
    super(cause);
  }

}
