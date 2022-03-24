package org.arig.robot.communication;

public interface I2CMultiplexerDevice {
    boolean selectChannel(byte channel);
    void disable();
}
