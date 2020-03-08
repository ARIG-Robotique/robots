package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.ecran.ExitQuery;
import org.arig.robot.communication.socket.ecran.ExitResponse;
import org.arig.robot.communication.socket.ecran.GetConfigQuery;
import org.arig.robot.communication.socket.ecran.GetConfigResponse;
import org.arig.robot.communication.socket.ecran.UpdateMatchQuery;
import org.arig.robot.communication.socket.ecran.UpdateMatchResponse;
import org.arig.robot.communication.socket.ecran.UpdateStateQuery;
import org.arig.robot.communication.socket.ecran.UpdateStateResponse;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.communication.socket.lidar.StartScanResponse;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

@Slf4j
public class EcranOverSocket extends AbstractSocketClient<EcranAction> implements IEcran {

    public EcranOverSocket(String hostname, Integer port) throws Exception {
        super(hostname, port);
        openSocket();
    }

    public EcranOverSocket(File socketFile) throws Exception {
        super(socketFile);
        openSocket();
    }

    @Override
    public void end() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new ExitQuery(), ExitResponse.class);
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
            UpdateStateQuery query = new UpdateStateQuery();
            query.setDatas(datas);
            sendToSocketAndGet(query, UpdateStateResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }

    @Override
    public void updateMatch(UpdateMatchInfos datas) {
        try {
            UpdateMatchQuery query = new UpdateMatchQuery();
            query.setDatas(datas);
            sendToSocketAndGet(query, UpdateMatchResponse.class);
        } catch (Exception e) {
            log.error("Erreur de lecture", e);
        }
    }
}
