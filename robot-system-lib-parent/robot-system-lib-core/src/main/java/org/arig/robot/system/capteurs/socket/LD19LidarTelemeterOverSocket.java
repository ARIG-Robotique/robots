package org.arig.robot.system.capteurs.socket;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.lidar.DeviceInfos;

import java.io.File;

@Slf4j
public class LD19LidarTelemeterOverSocket extends AbstractTelemeterOverSocket {

  public LD19LidarTelemeterOverSocket(String hostname, Integer port) throws Exception {
    this(hostname, port, 1000);
  }

  public LD19LidarTelemeterOverSocket(String hostname, Integer port, int timeout) throws Exception {
    super(hostname, port, true, timeout);
  }

  public LD19LidarTelemeterOverSocket(File socketFile) throws Exception {
    super(socketFile);
  }

  @Override
  public void printDeviceInfo() {
    DeviceInfos d = deviceInfo();
    log.info("LD19 Lidar D500 version [Firmware : {} ; Hardware {} ; Serial number : {}",
        d.getFirmwareVersion(), d.getHardwareVersion(), d.getSerialNumber());
  }
}
