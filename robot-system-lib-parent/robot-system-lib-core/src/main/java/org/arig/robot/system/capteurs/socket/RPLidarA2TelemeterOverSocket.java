package org.arig.robot.system.capteurs.socket;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.lidar.DeviceInfos;

import java.io.File;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
public class RPLidarA2TelemeterOverSocket extends AbstractTelemeterOverSocket {

  public static final String DRIVER_NAME = "rplidar";

  public static short LOW_MORTOR_PWM = 250;
  public static short MAX_MOTOR_PWM = 1023;
  public static short DEFAULT_MOTOR_PWM = 660;

  public RPLidarA2TelemeterOverSocket(String hostname, Integer port) throws Exception {
    this(hostname, port, 1000);
  }

  public RPLidarA2TelemeterOverSocket(String hostname, Integer port, int timeout) throws Exception {
    super(hostname, port, true, timeout);
  }

  public RPLidarA2TelemeterOverSocket(File socketFile) throws Exception {
    super(socketFile);
  }

  @Override
  public void printDeviceInfo() {
    DeviceInfos d = deviceInfo();
    log.info("RPLidar A2 version [Bridge driver : {} ; Firmware : {} ; Hardware {} ; Serial number : {}]",
      d.getDriver(), d.getFirmwareVersion(), d.getHardwareVersion(), d.getSerialNumber());
  }
}
