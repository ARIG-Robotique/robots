package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.lidar.DeviceInfosQuery;
import org.arig.robot.communication.socket.lidar.DeviceInfosResponse;
import org.arig.robot.communication.socket.lidar.GrabDataQuery;
import org.arig.robot.communication.socket.lidar.GrabDataResponse;
import org.arig.robot.communication.socket.lidar.HealthInfosQuery;
import org.arig.robot.communication.socket.lidar.HealthInfosResponse;
import org.arig.robot.communication.socket.lidar.SetSpeedQuery;
import org.arig.robot.communication.socket.lidar.SetSpeedResponse;
import org.arig.robot.communication.socket.lidar.StartScanQuery;
import org.arig.robot.communication.socket.lidar.StartScanResponse;
import org.arig.robot.communication.socket.lidar.StopScanQuery;
import org.arig.robot.communication.socket.lidar.StopScanResponse;
import org.arig.robot.communication.socket.lidar.enums.LidarAction;
import org.arig.robot.model.lidar.DeviceInfos;
import org.arig.robot.model.lidar.HealthInfos;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.model.lidar.enums.HealthState;

import java.io.File;
import java.util.Collections;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
public class RPLidarA2TelemeterOverSocket extends AbstractSocketClient<LidarAction> implements ILidarTelemeter {

    public static short LOW_MORTOR_PWM = 250;
    public static short MAX_MOTOR_PWM = 1023;
    public static short DEFAULT_MOTOR_PWM = 660;

    public RPLidarA2TelemeterOverSocket(String hostname, Integer port) throws Exception {
        super(hostname, port);
        openSocket();
    }

    public RPLidarA2TelemeterOverSocket(File socketFile) throws Exception {
        super(socketFile);
        openSocket();
    }

    @Override
    public void printDeviceInfo() {
        DeviceInfos d = deviceInfo();
        log.info("RPLidar A2 version [Firmware : {} ; Hardware {} ; Serial number : {}",
                d.getFirmwareVersion(), d.getHardwareVersion(), d.getSerialNumber());
    }

    @Override
    public DeviceInfos deviceInfo() {
        DeviceInfos r;
        try {
            DeviceInfosResponse rawResponse = sendToSocketAndGet(new DeviceInfosQuery(), DeviceInfosResponse.class);
            r = rawResponse.getDatas();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new DeviceInfos();
            r.setFirmwareVersion("UNKNOWN");
            r.setHardwareVersion((short) -1);
            r.setSerialNumber("UNKNOWN");
        }

        return r;
    }

    @Override
    public HealthInfos healthInfo() {
        HealthInfos r;
        try {
            HealthInfosResponse rawResponse = sendToSocketAndGet(new HealthInfosQuery(), HealthInfosResponse.class);
            r = rawResponse.getDatas();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new HealthInfos();
            r.setErrorCode((short) -99);
            r.setValue((short) -99);
            r.setState(HealthState.UNKNOWN);
        }

        return r;
    }

    @Override
    public void startScan() {
        startScan((short) -1);
    }

    @Override
    public void startScan(Short speed) {
        try {
            StartScanQuery query = new StartScanQuery();
            if (speed > -1) {
                query.setSpeed(speed);
            }
            sendToSocketAndGet(query, StartScanResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void stopScan() {
        try {
            sendToSocketAndGet(new StopScanQuery(), StopScanResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void setSpeed(Short speed) {
        try {
            sendToSocketAndGet(new SetSpeedQuery(speed), SetSpeedResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public ScanInfos grabDatas() {
        ScanInfos r;
        try {
            GrabDataResponse rawResponse = sendToSocketAndGet(new GrabDataQuery(), GrabDataResponse.class);
            return rawResponse.getDatas();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new ScanInfos();
            r.setIgnored((short) 360);
            r.setScan(Collections.emptyList());
        }

        return r;
    }

}
