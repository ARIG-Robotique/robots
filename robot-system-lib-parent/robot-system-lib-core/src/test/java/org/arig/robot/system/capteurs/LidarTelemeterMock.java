package org.arig.robot.system.capteurs;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AngleRange;
import org.arig.robot.model.Point;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class LidarTelemeterMock implements ILidarTelemeter {

  @Setter
  @Getter
  private boolean enabled = true;

  @Getter
  private Point sensorOrigin = new Point(0, 0);

  @Getter
  private final List<AngleRange> anglesFiltered = new ArrayList<>();

  @Getter
  @Setter
  private double couloirXMm = -1;

  @Getter
  @Setter
  private double couloirYMm = -1;

  @Override
  public boolean isClusterable() {
    return true;
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public void printDeviceInfo() {
  }

  @Override
  public void end() {
  }

  @Override
  public DeviceInfos deviceInfo() {
    DeviceInfos r = new DeviceInfos();
    r.setHardwareVersion((short) -1);
    r.setSerialNumber("1234567890");
    r.setFirmwareVersion("BOUCHON");

    return r;
  }

  @Override
  public HealthInfos healthInfo() {
    HealthInfos r = new HealthInfos();
    r.setState(HealthState.OK);

    return r;
  }

  @Override
  public void startScan() {
  }

  @Override
  public void startScan(final Short speed) {
  }

  @Override
  public void stopScan() {
  }

  @Override
  public void setSpeed(final Short speed) {
  }

  @Override
  public void setConfiguration(boolean reverse, double offsetAngle, int excludeLowerThan, int excludeGreaterThan) {
  }

  @Override
  public void setSensorOrigin(double x, double y) {
    log.info("Set sensor origin to X={}mm ; Y={}mm", x, y);
    this.sensorOrigin.setX(x);
    this.sensorOrigin.setY(y);
  }

  @Override
  public ScanInfos grabData() {
    ScanInfos r = new ScanInfos();
    r.setIgnored((short) 359);
    r.setScan(Collections.emptyList());
    return r;
  }
}
