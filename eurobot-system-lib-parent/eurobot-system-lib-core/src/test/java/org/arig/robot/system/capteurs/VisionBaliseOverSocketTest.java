package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.ConfigQueryData;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.DataResponse;
import org.arig.robot.communication.socket.balise.EmptyResponse;
import org.arig.robot.communication.socket.balise.IdleQueryData;
import org.arig.robot.communication.socket.balise.IdleResponse;
import org.arig.robot.communication.socket.balise.ImageQueryData;
import org.arig.robot.communication.socket.balise.ImageResponse;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.TeamQueryData;
import org.arig.robot.communication.socket.balise.enums.BaliseMode;
import org.arig.robot.model.Team;
import org.arig.robot.model.balise.enums.FiltreBalise;
import org.arig.robot.utils.SocketUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
class VisionBaliseOverSocketTest {

  private static VisionBaliseOverSocket balise;

  @BeforeAll
  @SneakyThrows
  static void initTest() {
    String host = "192.168.0.114";
    int port = 50667;
    Assumptions.assumeTrue(SocketUtils.serverListening(host, port));

    balise = new VisionBaliseOverSocket(host, port);
    balise.openSocket();
    Assertions.assertTrue(balise.isOpen());
    IdleQueryData queryData = new IdleQueryData(false);
    balise.setIdle(queryData);
  }

  @AfterAll
  static void stopTest() {
    if (balise != null) {
      balise.end();
    }
  }

  @BeforeEach
  @SneakyThrows
  void waitForSocketClosed() {
    Thread.sleep(1_000);
  }

  @Test
  @SneakyThrows
  void testSetConfigModeMillimeter2D() {
    ConfigQueryData queryData = new ConfigQueryData(BaliseMode.MILLIMETER_2D);
    EmptyResponse response = balise.setConfig(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    StatusResponse status = balise.getStatus();
    Assertions.assertEquals(BaliseMode.MILLIMETER_2D, status.getData().getMode());
  }

  @Test
  @SneakyThrows
  void testSetConfigModeFloat2D() {
    ConfigQueryData queryData = new ConfigQueryData(BaliseMode.FLOAT_2D);
    EmptyResponse response = balise.setConfig(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    StatusResponse status = balise.getStatus();
    Assertions.assertEquals(BaliseMode.FLOAT_2D, status.getData().getMode());
  }

  @Test
  @SneakyThrows
  void testGetStatus() {
    StatusResponse response = balise.getStatus();
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    Assertions.assertNotNull(response.getData());
    Assertions.assertFalse(response.getData().getStatusMessage().isEmpty());
    Assertions.assertTrue(response.getData().isAllOK());
  }

  @Test
  @SneakyThrows
  void testSetTeamJaune() {
    TeamQueryData queryData = new TeamQueryData(Team.JAUNE.name());
    EmptyResponse response = balise.setTeam(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    StatusResponse status = balise.getStatus();
    Assertions.assertEquals(Team.JAUNE.name(), status.getData().getTeam());
  }

  @Test
  @SneakyThrows
  void testSetTeamBleu() {
    TeamQueryData queryData = new TeamQueryData(Team.BLEU.name());
    EmptyResponse response = balise.setTeam(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    StatusResponse status = balise.getStatus();
    Assertions.assertEquals(Team.BLEU.name(), status.getData().getTeam());
  }

  @Test
  @SneakyThrows
  void testGetData() {
    DataQueryData<FiltreBalise> queryData = new DataQueryData<>(FiltreBalise.TOUT);
    DataResponse response = (DataResponse) balise.getData(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    Assertions.assertNotNull(response.getData());
    Assertions.assertFalse(response.getData().getData3D().isEmpty());
  }

  @Test
  @SneakyThrows
  void testGetImage() {
    ImageQueryData queryData = new ImageQueryData();
    ImageResponse response = balise.getImage(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    Assertions.assertNotNull(response.getData());
    Assertions.assertFalse(response.getData().getCameras().isEmpty());
    Assertions.assertFalse(response.getData().getCameras().get(0).getData().isEmpty());
  }

  @Test
  @SneakyThrows
  void testGetImageWithReduction() {
    ImageQueryData queryData = new ImageQueryData(0.5f);
    ImageResponse response = balise.getImage(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    Assertions.assertNotNull(response.getData());
    Assertions.assertFalse(response.getData().getCameras().isEmpty());
    Assertions.assertFalse(response.getData().getCameras().get(0).getData().isEmpty());
  }

  @Test
  @SneakyThrows
  void testProcess() {
    Assertions.assertNull(balise.process());
  }

  @Test
  @SneakyThrows
  void testSetIdleTrue() {
    IdleQueryData queryData = new IdleQueryData(true);
    IdleResponse response = balise.setIdle(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    StatusResponse status = balise.getStatus();
    Assertions.assertEquals(true, status.getData().getIdle());
  }

  @Test
  @SneakyThrows
  @Order(Integer.MAX_VALUE)
  void testSetIdleFalse() {
    IdleQueryData queryData = new IdleQueryData(false);
    IdleResponse response = balise.setIdle(queryData);
    Assertions.assertNotNull(response);
    Assertions.assertTrue(response.isOk());
    StatusResponse status = balise.getStatus();
    Assertions.assertEquals(false, status.getData().getIdle());
  }

}
