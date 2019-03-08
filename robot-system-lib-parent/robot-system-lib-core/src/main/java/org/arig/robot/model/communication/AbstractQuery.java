package org.arig.robot.model.communication;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractQuery<T extends Enum> {

    private final T action;

}
