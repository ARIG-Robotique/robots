package org.arig.robot.system.capteurs.i2c;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.average.DoubleValueAverage;
import org.arig.robot.model.Point;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GP2D120Telemeter implements ILidarTelemeter {

    @Autowired
    private PamiIOService pamiIOService;

    @Setter
    @Getter
    private boolean enabled = true;

    @Getter
    private boolean clusterable = false;

    private final static Point ORIG = new Point(0, 0);

    public static class Device extends Point {
        private final byte adcId;
        private final double angleRad;
        private final GP2D12Telemeter.DeviceFilter filter = new GP2D12Telemeter.DeviceFilter(1);

        public Device(
                byte adcId,
                int x,
                int y,
                float angleDeg
        ) {
            this.adcId = adcId;
            this.setX(x);
            this.setY(y);
            this.angleRad = Math.toRadians(angleDeg);
        }
    }

    private final List<Device> devices;
    private final int tailleObstacle;

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void printDeviceInfo() {
        log.info("GP2D120 telemeter, {} sensors", devices.size());
    }

    @Override
    public void end() {
        // NOOP
    }

    @Override
    public DeviceInfos deviceInfo() {
        return new DeviceInfos();
    }

    @Override
    public HealthInfos healthInfo() {
        HealthInfos r = new HealthInfos();
        r.setState(HealthState.OK);
        return null;
    }

    @Override
    public void startScan() {
        // NOOP
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
    public ScanInfos grabData() {
        if (!enabled) {
            return null;
        }

        ScanInfos r = new ScanInfos();
        r.setTailleObstacle(tailleObstacle);

        short ignored = 0;
        List<Scan> scans = new ArrayList<>();
        for (Device device : devices) {
            try {
                final double value;
                if (device.adcId == 1) {
                    value = pamiIOService.distanceGauche();
                } else if (device.adcId == 2) {
                    value = pamiIOService.distanceCentre();
                } else {
                    value = pamiIOService.distanceDroite();
                }

                // ignore les out of range
                if (value < 100 || value > 500) {
                    device.filter.reset();
                    ignored++;
                    continue;
                }

                double dstValue = device.filter.filter(20 * (12.08 * Math.pow(value * 5 / 1023, -1.058)));

                if (dstValue == GP2D12Telemeter.DeviceFilter.INVALID) {
                    ignored++;
                    continue;
                }

                Point pt = new Point(
                        dstValue * Math.cos(device.angleRad) + device.getX(),
                        dstValue * Math.sin(device.angleRad) + device.getY()
                );

                float angleDeg = (float) pt.angle(ORIG);
                float distance = (float) pt.distance(ORIG);

                scans.add(new Scan(angleDeg, distance, false, (short) 0));
            } catch (Exception e) {
                log.error("Erreur de lecture du GP2D {} : {}", device.adcId, e.toString());
                ignored++;
            }
        }

        r.setScan(scans);
        r.setIgnored(ignored);

        return r;
    }
}
