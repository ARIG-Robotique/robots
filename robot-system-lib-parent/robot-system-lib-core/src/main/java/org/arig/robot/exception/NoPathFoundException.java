package org.arig.robot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gdepuille on 01/01/14.
 */
public class NoPathFoundException extends Exception {

    @Getter
    private ErrorType errorType;

    public NoPathFoundException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public NoPathFoundException(ErrorType errorType, Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
    }

    public NoPathFoundException(ErrorType errorType, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorType.getMessage(), cause, enableSuppression, writableStackTrace);
        this.errorType = errorType;
    }

    @AllArgsConstructor
    public enum ErrorType {
        NO_PATH_FOUND("Aucun chemin disponible"),
        START_NODE_DOES_NOT_EXIST("Le noeud de départ n'éxiste pas"),
        END_NODE_DOES_NOT_EXIST("Le noeud d'arrivé n'éxiste pas");

        @Getter
        private String message;
    }
}
