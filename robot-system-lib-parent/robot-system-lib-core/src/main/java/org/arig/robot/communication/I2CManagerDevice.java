package org.arig.robot.communication;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@Accessors(fluent = true)
public class I2CManagerDevice<D> {
    private final D device;
    private final String deviceName;
    private final byte[] scanCmd;

    private final String multiplexerDeviceName;
    private final Byte multiplexerChannel;

    public boolean isMultiplexed() {
        return StringUtils.isNotBlank(multiplexerDeviceName) && multiplexerChannel > 0;
    }

    @Override
    public String toString() {
        if (isMultiplexed()) {
            return deviceName + " multiplexe par " + multiplexerDeviceName + " sur le canal " + multiplexerChannel;
        }
        return deviceName;
    }
}
