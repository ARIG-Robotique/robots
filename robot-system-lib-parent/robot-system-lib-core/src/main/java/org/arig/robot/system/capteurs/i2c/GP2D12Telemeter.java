package org.arig.robot.system.capteurs.i2c;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.average.DoubleValueAverage;
import org.arig.robot.filters.sensors.GP2DPhantomFilter;
import org.arig.robot.model.Point;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GP2D12Telemeter implements ILidarTelemeter {

    @Autowired
    private I2CAdcAnalogInput analogReader;

    @Setter
    @Getter
    private boolean enabled = false;

    @Getter
    private final boolean clusterable = false;

    private final static Point ORIG = new Point(0, 0);

    public static class Device extends Point {
        private final byte adcId;
        private final double angleRad;
        private final DeviceFilter filter = new DeviceFilter(50);

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

    public static class DeviceFilter extends DoubleValueAverage {
        public static final double INVALID = 0;

        private final int size;

        public DeviceFilter(int size) {
            super(size);
            this.size = size;
        }

        @Override
        protected Double effectiveAverage(Double reducedValue, int queueSize) {
            return queueSize < size / 2 ? DeviceFilter.INVALID : super.effectiveAverage(reducedValue, queueSize);
        }
    }


    private final List<Device> devices;
    private final int tailleObstacle;
    private final GP2DPhantomFilter phantomFilter;

    public GP2D12Telemeter(List<Device> devices, int tailleObstacle) {
        this.devices = devices;
        this.tailleObstacle = tailleObstacle;
        this.phantomFilter = new GP2DPhantomFilter(3, 5);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void printDeviceInfo() {
        log.info("GP2D12 telemeter, {} sensors", devices.size());
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
                double value = analogReader.readCapteurValue(device.adcId);

                // ignore les out of range
                if (value < 450 || value > 2000) {
                    device.filter.reset();
                    ignored++;
                    continue;
                }

                //double dstValue = device.filter.filter((26208 / value - 4.693) * 10); // empirique, cf NerellIOCommands
                double dstValue = phantomFilter.filter((26208 / value - 4.693) * 10); // empirique, cf NerellIOCommands

                if (dstValue == DeviceFilter.INVALID) {
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
