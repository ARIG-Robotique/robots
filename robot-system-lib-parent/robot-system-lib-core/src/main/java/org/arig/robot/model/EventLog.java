package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventLog<T extends Enum<T>> {

    private final T event;
    private final Byte value;

}
