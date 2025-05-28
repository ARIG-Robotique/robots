package org.arig.robot.system.capteurs.socket;

import org.arig.robot.model.AngleRange;
import org.arig.robot.model.Point;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;

import java.util.List;

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

  void setConfiguration(boolean reverse, double offsetAngle, int excludeLowerThan, int excludeGreaterThan);

  double getCouloirXMm();

  void setCouloirXMm(double couloirXMm);

  double getCouloirYMm();

  void setCouloirYMm(double couloirYMm);

  Point getSensorOrigin();

  default void setSensorOrigin(Point origin) {
    setSensorOrigin(origin.getX(), origin.getY());
  }

  default void addAnglesFiltered(AngleRange angleRange) {}

  List<AngleRange> getAnglesFiltered();

  void setSensorOrigin(double x, double y);

  ScanInfos grabData();

}
