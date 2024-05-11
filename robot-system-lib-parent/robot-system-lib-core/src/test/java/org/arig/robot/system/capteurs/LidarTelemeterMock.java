package org.arig.robot.system.capteurs;

import lombok.Getter;
import lombok.Setter;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;

import java.util.Collections;

public class LidarTelemeterMock implements ILidarTelemeter {

    @Setter
    @Getter
    private boolean enabled = true;

    @Override
    public boolean isClusterable() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void printDeviceInfo() { }

    @Override
    public void end() { }

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
    public void startScan() { }

    @Override
    public void startScan(final Short speed) { }

    @Override
    public void stopScan() { }

    @Override
    public void setSpeed(final Short speed) { }

    @Override
    public ScanInfos grabData() {
        ScanInfos r = new ScanInfos();
        r.setIgnored((short) 359);
        r.setScan(Collections.emptyList());
        return r;
    }
}
