package org.arig.robot.system.capteurs.socket;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.AbstractBaliseResponseWithData;
import org.arig.robot.communication.socket.balise.AliveQuery;
import org.arig.robot.communication.socket.balise.ConfigQuery;
import org.arig.robot.communication.socket.balise.ConfigQueryData;
import org.arig.robot.communication.socket.balise.DataQuery;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.EmptyResponse;
import org.arig.robot.communication.socket.balise.ExitQuery;
import org.arig.robot.communication.socket.balise.IdleQuery;
import org.arig.robot.communication.socket.balise.IdleQueryData;
import org.arig.robot.communication.socket.balise.IdleResponse;
import org.arig.robot.communication.socket.balise.ImageQuery;
import org.arig.robot.communication.socket.balise.ImageQueryData;
import org.arig.robot.communication.socket.balise.ImageResponse;
import org.arig.robot.communication.socket.balise.ProcessQuery;
import org.arig.robot.communication.socket.balise.StatusQuery;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.TeamQuery;
import org.arig.robot.communication.socket.balise.TeamQueryData;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.system.communication.AbstractSocketClient;

import java.io.File;
import java.io.Serializable;

@Slf4j
public abstract class AbstractVisionBaliseOverSocket<DATA extends Serializable>
        extends AbstractSocketClient<BaliseAction>
    implements IVisionBalise<DATA> {

    private final Class<? extends AbstractBaliseResponseWithData<DATA>> dataResponseType;

    public AbstractVisionBaliseOverSocket(String hostname, Integer port,
                                          Class<? extends AbstractBaliseResponseWithData<DATA>> statutReponseType) {
        super(hostname, port, 10000);
        this.dataResponseType = statutReponseType;
    }

    public AbstractVisionBaliseOverSocket(File socketFile,
                                          Class<? extends AbstractBaliseResponseWithData<DATA>> statutReponseType) {
        super(socketFile);
        this.dataResponseType = statutReponseType;
    }

    @Override
    public void end() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new ExitQuery(), EmptyResponse.class);
            } catch (Exception e) {
                log.warn("Erreur de lecture", e);
            }
        }
        super.end();
    }

    @Override
    public EmptyResponse keepAlive() {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new AliveQuery(), EmptyResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de recupération de la photo", e);
            return null;
        }
    }

    @Override
    public EmptyResponse setConfig(ConfigQueryData queryData) {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new ConfigQuery(queryData), EmptyResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de modification de la configuration", e);
            return null;
        }
    }

    @Override
    public StatusResponse getStatus() {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new StatusQuery(), StatusResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de recupération du statut", e);
            return null;
        }
    }

    @Override
    public EmptyResponse setTeam(TeamQueryData queryData) {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new TeamQuery(queryData), EmptyResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de modification de la team", e);
            return null;
        }
    }

    @Override
    public AbstractBaliseResponseWithData<DATA> getData(DataQueryData<?> queryData) {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new DataQuery(queryData), dataResponseType);
        } catch (Exception e) {
            log.warn("Erreur de lecture des données de vision", e);
            return null;
        }
    }

    @Override
    public ImageResponse getImage(ImageQueryData queryData) {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new ImageQuery(queryData), ImageResponse.class);

        } catch (Exception e) {
            log.warn("Erreur de recupération de l'image", e);
            return null;
        }
    }

    @Override
    public EmptyResponse process() {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new ProcessQuery(), EmptyResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de traitement de l'image", e);
            return null;
        }
    }

    @Override
    public IdleResponse setIdle(IdleQueryData queryData) {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new IdleQuery(queryData), IdleResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de modification de l'idle", e);
            return null;
        }
    }

}
