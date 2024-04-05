package org.arig.robot.communication.can;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import tel.schich.javacan.CanFrame;

@Data
@Builder
@Accessors(fluent = true)
public class CANManagerDevice<D> {
    private final D device;
    private final String deviceName;
    private final CanFrame scanFrame;

    @Override
    public String toString() {
        return deviceName;
    }
}
