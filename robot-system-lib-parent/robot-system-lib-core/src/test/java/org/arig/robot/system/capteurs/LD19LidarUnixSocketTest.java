package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import org.arig.robot.system.capteurs.socket.LD19LidarTelemeterOverSocket;
import org.arig.robot.system.capteurs.socket.RPLidarA2TelemeterOverSocket;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;

public class LD19LidarUnixSocketTest extends LD19LidarTelemeterOverSocketTest {

    @BeforeAll
    @SneakyThrows
    static void initTest() {
        File socketFile = new File("/tmp/ldlidar.sock");
        Assumptions.assumeTrue(socketFile.exists());

        ldLidar = new LD19LidarTelemeterOverSocket(socketFile);
        ldLidar.printDeviceInfo();
    }
}
