package org.arig.robot.system.capteurs.socket;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.ecran.EmptyResponse;
import org.arig.robot.communication.socket.ecran.ExitQuery;
import org.arig.robot.communication.socket.ecran.GetConfigQuery;
import org.arig.robot.communication.socket.ecran.SetParamsQuery;
import org.arig.robot.communication.socket.ecran.UpdateMatchQuery;
import org.arig.robot.communication.socket.ecran.UpdatePhotoQuery;
import org.arig.robot.communication.socket.ecran.UpdateStateQuery;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.AbstractEcranConfig;
import org.arig.robot.model.ecran.AbstractEcranState;
import org.arig.robot.model.ecran.EcranMatchInfo;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.system.communication.AbstractSocketClient;

import java.io.File;

@Slf4j
public abstract class AbstractEcranOverSocket<CONFIG extends AbstractEcranConfig, STATE extends AbstractEcranState>
        extends AbstractSocketClient<EcranAction>
        implements IEcran<CONFIG, STATE> {

    private final Class<? extends AbstractResponseWithData<EcranAction, CONFIG>> configInfosReponseType;

    public AbstractEcranOverSocket(String hostname, Integer port,
                                   Class<? extends AbstractResponseWithData<EcranAction, CONFIG>> configInfosReponseType) {
        super(hostname, port, 1000);
        this.configInfosReponseType = configInfosReponseType;
    }

    public AbstractEcranOverSocket(File socketFile,
                                   Class<? extends AbstractResponseWithData<EcranAction, CONFIG>> configInfosReponseType) {
        super(socketFile);
        this.configInfosReponseType = configInfosReponseType;
    }

    @Override
    public void end() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new ExitQuery(), EmptyResponse.class);
            } catch (Exception e) {
                log.warn("Impossible de fermer l'ecran");
            }
        }
        super.end();
    }

    @Override
    public boolean setParams(EcranParams params) {
        try {
            openIfNecessary();
            SetParamsQuery query = new SetParamsQuery();
            query.setData(params);
            EmptyResponse rawResponse = sendToSocketAndGet(query, EmptyResponse.class);
            return rawResponse.isOk();
        } catch (Exception e) {
            logException(e);
            return false;
        }
    }

    @Override
    public CONFIG configInfos() {
        try {
            openIfNecessary();
            AbstractResponseWithData<EcranAction, CONFIG> rawResponse = sendToSocketAndGet(new GetConfigQuery(), configInfosReponseType);
            return rawResponse.getData();
        } catch (Exception e) {
            logException(e);
            return null;
        }
    }

    @Override
    public boolean updateState(STATE data) {
        try {
            openIfNecessary();
            UpdateStateQuery query = new UpdateStateQuery();
            query.setData(data);
            sendToSocketAndGet(query, EmptyResponse.class);
            return true;
        } catch (Exception e) {
            logException(e);
            return false;
        }
    }

    @Override
    public boolean updateMatch(EcranMatchInfo data) {
        try {
            openIfNecessary();
            UpdateMatchQuery query = new UpdateMatchQuery();
            query.setData(data);
            sendToSocketAndGet(query, EmptyResponse.class);
            return true;
        } catch (Exception e) {
            logException(e);
            return false;
        }
    }

    @Override
    public void updatePhoto(EcranPhoto data) {
        try {
            openIfNecessary();
            UpdatePhotoQuery query = new UpdatePhotoQuery();
            query.setData(data);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            logException(e);
        }
    }

    private void logException(Exception e) {
        log.error("Erreur de lecture : {}", e.toString());
        if (log.isDebugEnabled()) {
            log.debug("Erreur de lecture", e);
        }
    }
}
