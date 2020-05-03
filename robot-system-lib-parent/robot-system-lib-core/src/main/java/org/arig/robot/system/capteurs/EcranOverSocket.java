package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.ecran.*;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateEtalonnageData;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;

import java.io.File;
import java.io.IOException;

@Slf4j
public class EcranOverSocket extends AbstractSocketClient<EcranAction> implements IEcran {

    public EcranOverSocket(String hostname, Integer port) throws Exception {
        super(hostname, port);
    }

    public EcranOverSocket(File socketFile) throws Exception {
        super(socketFile);
    }

    @Override
    public void end() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new ExitQuery(), EmptyResponse.class);
            } catch (IOException e) {
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
            r = rawResponse.getDatas();
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
    public void updateState(UpdateStateInfos datas) {
        try {
            openIfNecessary();
            UpdateStateQuery query = new UpdateStateQuery();
            query.setDatas(datas);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void updateMatch(UpdateMatchInfos datas) {
        try {
            openIfNecessary();
            UpdateMatchQuery query = new UpdateMatchQuery();
            query.setDatas(datas);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void updatePhoto(String photo) {
        try {
            openIfNecessary();
            UpdatePhotoQuery query = new UpdatePhotoQuery();
            query.setDatas(photo);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void updateEtalonnage(UpdateEtalonnageData etalonnage) {
        try {
            openIfNecessary();
            UpdateEtalonnageQuery query = new UpdateEtalonnageQuery();
            query.setDatas(etalonnage);
            sendToSocketAndGet(query, EmptyResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }
}
