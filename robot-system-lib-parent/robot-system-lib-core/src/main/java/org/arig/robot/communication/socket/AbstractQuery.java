package org.arig.robot.communication.socket;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractQuery<T extends Enum<T>> {

    private final T action;

}
