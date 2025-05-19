package org.arig.robot.system.capteurs.socket;

import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;

/**
 * @author gdepuille on 24/04/17.
 */
public interface ILidarTelemeter {

  boolean isEnabled();

  void setEnabled(boolean enabled);

  boolean isClusterable();

  boolean isOpen();

  void printDeviceInfo();

  void end();

  DeviceInfos deviceInfo();

  HealthInfos healthInfo();

  void startScan();

  void startScan(Short speed);

  void stopScan();

  void setSpeed(Short speed);

  void setConfiguration(boolean reverse, int offsetAngle, int excludeLowerThan, int excludeGreaterThan);

  ScanInfos grabData();
}
