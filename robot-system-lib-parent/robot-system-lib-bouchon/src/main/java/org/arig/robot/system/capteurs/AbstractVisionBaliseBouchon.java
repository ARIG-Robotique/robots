package org.arig.robot.system.capteurs;


import org.arig.robot.communication.socket.balise.ConfigQueryData;
import org.arig.robot.communication.socket.balise.EmptyResponse;
import org.arig.robot.communication.socket.balise.IdleQueryData;
import org.arig.robot.communication.socket.balise.IdleResponse;
import org.arig.robot.communication.socket.balise.ImageQueryData;
import org.arig.robot.communication.socket.balise.ImageResponse;
import org.arig.robot.communication.socket.balise.ImageResponseData;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.StatusResponseData;
import org.arig.robot.communication.socket.balise.TeamQueryData;
import org.arig.robot.communication.socket.balise.ZoneQueryData;
import org.arig.robot.communication.socket.balise.ZoneResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.communication.socket.balise.enums.BaliseMode;
import org.arig.robot.system.capteurs.socket.IVisionBalise;

import java.io.Serializable;
import java.util.Collections;

public abstract class AbstractVisionBaliseBouchon<DATA extends Serializable> implements IVisionBalise<DATA> {

  @Override
  public void openSocket() {
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public void end() {
  }

  @Override
  public EmptyResponse keepAlive() {
    EmptyResponse response = new EmptyResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.ALIVE);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    return response;
  }

  @Override
  public EmptyResponse setConfig(ConfigQueryData queryData) {
    EmptyResponse response = new EmptyResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.CONFIG);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    return response;
  }

  @Override
  public StatusResponse getStatus() {
    StatusResponse response = new StatusResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.STATUS);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    StatusResponseData data = new StatusResponseData(
      org.arig.robot.communication.socket.enums.StatusResponse.OK,
      org.arig.robot.communication.socket.enums.StatusResponse.OK,
      org.arig.robot.communication.socket.enums.StatusResponse.OK,
      "status message",
      BaliseMode.MILLIMETER_2D,
      "UNKNOWN",
      false
    );
    response.setData(data);

    return response;
  }

  @Override
  public EmptyResponse setTeam(TeamQueryData queryData) {
    EmptyResponse response = new EmptyResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.TEAM);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    return response;
  }

  @Override
  public ImageResponse getImage(ImageQueryData queryData) {
    ImageResponse response = new ImageResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.IMAGE);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);
    response.setData(new ImageResponseData(Collections.emptyList()));

    return response;
  }

  @Override
  public EmptyResponse process() {
    EmptyResponse response = new EmptyResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.PROCESS);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    return response;
  }

  @Override
  public ZoneResponse getMines(ZoneQueryData queryData) {
    return null;
  }

  @Override
  public IdleResponse setIdle(IdleQueryData queryData) {
    IdleResponse response = new IdleResponse();

    response.setIndex(0);
    response.setAction(BaliseAction.IDLE);
    response.setStatus(org.arig.robot.communication.socket.enums.StatusResponse.OK);

    return response;
  }

}
