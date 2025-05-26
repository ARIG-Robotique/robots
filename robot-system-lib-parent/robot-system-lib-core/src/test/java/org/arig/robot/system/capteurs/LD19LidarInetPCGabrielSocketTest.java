package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import org.arig.robot.system.capteurs.socket.LD19LidarTelemeterOverSocket;
import org.arig.robot.utils.SocketUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

public class LD19LidarInetPCGabrielSocketTest extends LD19LidarTelemeterOverSocketTest {

  @BeforeAll
  @SneakyThrows
  static void initTest() {
    String host = "192.168.0.114";
    int port = 8686;
    Assumptions.assumeTrue(SocketUtils.serverListening(host, port));

    ldLidar = new LD19LidarTelemeterOverSocket(host, port);
    ldLidar.printDeviceInfo();
  }
}
