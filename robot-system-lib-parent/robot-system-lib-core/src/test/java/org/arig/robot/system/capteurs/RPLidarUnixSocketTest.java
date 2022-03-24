package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;

public class RPLidarUnixSocketTest extends RPLidarA2TelemeterOverSocketTest {

    @BeforeAll
    @SneakyThrows
    static void initTest() {
        File socketFile = new File("/tmp/lidar.sock");
        Assumptions.assumeTrue(socketFile.exists());

        rpLidar = new RPLidarA2TelemeterOverSocket(socketFile);
        rpLidar.printDeviceInfo();
    }
}
