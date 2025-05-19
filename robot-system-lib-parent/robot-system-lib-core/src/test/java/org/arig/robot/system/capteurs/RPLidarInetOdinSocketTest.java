package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import org.arig.robot.system.capteurs.socket.RPLidarA2TelemeterOverSocket;
import org.arig.robot.utils.SocketUtils;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;

public class RPLidarInetOdinSocketTest extends RPLidarA2TelemeterOverSocketTest {

  @BeforeAll
  @SneakyThrows
  static void initTest() {
    String host = "odin";
    int port = 8686;
    Assumptions.assumeTrue(SocketUtils.serverListening(host, port));

    rpLidar = new RPLidarA2TelemeterOverSocket(host, port, 2000);
    rpLidar.printDeviceInfo();
  }
}
