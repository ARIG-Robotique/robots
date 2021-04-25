package org.arig.robot.communication;

public interface II2CMultiplexerDevice {
    boolean selectChannel(byte channel);
    void disable();
}
