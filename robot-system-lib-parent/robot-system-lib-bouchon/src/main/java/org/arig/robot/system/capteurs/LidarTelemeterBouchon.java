package org.arig.robot.system.capteurs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AngleRange;
import org.arig.robot.model.Point;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author gdepuille on 24/04/17.
 */
@Slf4j
@NoArgsConstructor
public class LidarTelemeterBouchon implements ILidarTelemeter {

  private final Random random = new Random();

  @Getter
  @Setter
  private boolean enabled = true;

  @Getter
  private final boolean clusterable = true;

  @Getter
  private final Point sensorOrigin = new Point(0, 0);

  @Getter
  private final List<AngleRange> anglesFiltered = new ArrayList<>();

  @Override
  public void printDeviceInfo() {
    DeviceInfos d = deviceInfo();
    log.info("Lidar Bouchon version [Firmware : {} ; Hardware {} ; Serial number : {}",
      d.getFirmwareVersion(), d.getHardwareVersion(), d.getSerialNumber());
  }

  @Override
  public void setSensorOrigin(double x, double y) {
    log.info("Set sensor origin to X={}mm ; Y={}mm", x, y);
    this.sensorOrigin.setX(x);
    this.sensorOrigin.setY(y);
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public void end() {
    // NOOP
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
    startScan((short) -1);
  }

  @Override
  public void startScan(Short speed) {
    // NOOP
  }

  @Override
  public void stopScan() {
    // NOOP
  }

  @Override
  public void setSpeed(Short speed) {
    // NOOP
  }

  @Override
  public void setConfiguration(boolean reverse, double offsetAngle, int excludeLowerThan, int excludeGreaterThan) {
    // NOOP
  }

  @Override
  public ScanInfos grabData() {
    ScanInfos r = new ScanInfos();
    r.setIgnored((short) 359);

    List<Scan> scans = new ArrayList<>();
    r.setScan(scans);

    Scan s = new Scan();
    scans.add(s);

    s.setQuality((short) 47);
    s.setDistanceMm(random.nextInt(3000) + 500);
    s.setAngleDeg(random.nextInt(360));

    return r;
  }
}
