package org.arig.robot.exception;

/**
 * Created by mythril on 18/12/13.
 */
public class NotYetImplementedException extends RuntimeException {

    private static final String MSG = "Not Yet Implemented";

    public NotYetImplementedException() {
        super(MSG);
    }

    public NotYetImplementedException(Throwable cause) {
        super(MSG, cause);
    }

    protected NotYetImplementedException(Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(MSG, cause, enableSuppression, writableStackTrace);
    }
}
