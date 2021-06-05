package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.ecran.EmptyResponse;
import org.arig.robot.communication.socket.ecran.ExitQuery;
import org.arig.robot.communication.socket.ecran.GetConfigQuery;
import org.arig.robot.communication.socket.ecran.GetConfigResponse;
import org.arig.robot.communication.socket.ecran.UpdateMatchQuery;
import org.arig.robot.communication.socket.ecran.UpdatePhotoQuery;
import org.arig.robot.communication.socket.ecran.UpdateStateQuery;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdatePhotoInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.arig.robot.system.communication.AbstractSocketClient;

import java.io.File;

@Slf4j
public class EcranOverSocket extends AbstractSocketClient<EcranAction> implements IEcran {

    public EcranOverSocket(String hostname, Integer port) {
        super(hostname, port, 1000);
    }

    public EcranOverSocket(File socketFile) {
        super(socketFile);
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
    public GetConfigInfos configInfos() {
        GetConfigInfos r;
        try {
            openIfNecessary();
            GetConfigResponse rawResponse = sendToSocketAndGet(new GetConfigQuery(), GetConfigResponse.class);
            r = rawResponse.getData();
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
            r = new GetConfigInfos();
            r.setTeam(-1);
            r.setStrategy(-1);
            r.setStartCalibration(false);
        }

        return r;
    }

    @Override
    public void updateState(UpdateStateInfos data) {
        try {
            openIfNecessary();
            UpdateStateQuery query = new UpdateStateQuery();
            query.setData(data);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void updateMatch(UpdateMatchInfos data) {
        try {
            openIfNecessary();
            UpdateMatchQuery query = new UpdateMatchQuery();
            query.setData(data);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void updatePhoto(UpdatePhotoInfos data) {
        try {
            openIfNecessary();
            UpdatePhotoQuery query = new UpdatePhotoQuery();
            query.setData(data);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }
}
