package org.arig.robot.communication.bouchon;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.can.AbstractCANManager;
import org.arig.robot.communication.can.CANManagerDevice;
import org.arig.robot.model.bouchon.BouchonCANDevice;

@Slf4j
public class BouchonCANManager extends AbstractCANManager<BouchonCANDevice> {

    @Override
    protected void scanDevice(final CANManagerDevice<BouchonCANDevice> device) { }

    @Override
    public void sendData(String deviceName, byte... data) { }

    @Override
    public byte getData(String deviceName) {
        return 0;
    }

    @Override
    public byte[] getData(String deviceName, int size) {
      return new byte[size];
    }
}
