package org.arig.robot.communication.can;

import org.apache.commons.lang3.StringUtils;
import tel.schich.javacan.CanFilter;

import java.io.IOException;

public interface CANDevice {
    String deviceName();
    boolean scan() throws IOException;
    String version();

    default String signature() {
        return String.format("CAN device %s (version %s)", deviceName(), version());
    }
}
