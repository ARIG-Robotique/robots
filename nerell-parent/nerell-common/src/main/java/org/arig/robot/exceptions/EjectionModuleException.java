package org.arig.robot.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EjectionModuleException extends Exception {

    public EjectionModuleException(Throwable e) {
        super(e);
    }

}
