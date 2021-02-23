package org.arig.robot.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExitProgram extends RuntimeException {

    @Getter
    private final boolean wait;

}
