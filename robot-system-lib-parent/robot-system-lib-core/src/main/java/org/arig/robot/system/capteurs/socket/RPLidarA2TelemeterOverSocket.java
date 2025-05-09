package org.arig.robot.system.capteurs.socket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.lidar.DeviceInfosQuery;
import org.arig.robot.communication.socket.lidar.DeviceInfosResponse;
import org.arig.robot.communication.socket.lidar.ExitQuery;
import org.arig.robot.communication.socket.lidar.ExitResponse;
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
import org.arig.robot.system.communication.AbstractSocketClient;

import java.io.File;
import java.util.Collections;

/**
 * @author gdepuille on 03/03/17.
 */
@Slf4j
public class RPLidarA2TelemeterOverSocket extends AbstractTelemeterOverSocket {

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
