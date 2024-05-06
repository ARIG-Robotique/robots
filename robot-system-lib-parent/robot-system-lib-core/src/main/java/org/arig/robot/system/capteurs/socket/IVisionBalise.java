package org.arig.robot.system.capteurs.socket;

import org.arig.robot.communication.socket.balise.AbstractBaliseResponseWithData;
import org.arig.robot.communication.socket.balise.ConfigQueryData;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.EmptyResponse;
import org.arig.robot.communication.socket.balise.IdleQueryData;
import org.arig.robot.communication.socket.balise.IdleResponse;
import org.arig.robot.communication.socket.balise.ImageQueryData;
import org.arig.robot.communication.socket.balise.ImageResponse;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.TeamQueryData;

import java.io.Serializable;

public interface IVisionBalise<DATA extends Serializable> {

    void openSocket() throws Exception;

    boolean isOpen();

    void end();

    EmptyResponse keepAlive();

    EmptyResponse setConfig(ConfigQueryData queryData);

    StatusResponse getStatus();

    EmptyResponse setTeam(TeamQueryData queryData);

    AbstractBaliseResponseWithData<DATA> getData(DataQueryData<?> queryData);

    ImageResponse getImage(ImageQueryData queryData);

    EmptyResponse process();

    IdleResponse setIdle(IdleQueryData queryData);

}
