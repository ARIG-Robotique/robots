package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.communication.socket.balise.DetectionQuery;
import org.arig.robot.communication.socket.balise.DetectionResponse;
import org.arig.robot.communication.socket.balise.ExitQuery;
import org.arig.robot.communication.socket.balise.ExitResponse;
import org.arig.robot.communication.socket.balise.PhotoQuery;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.communication.socket.balise.StatusQuery;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.model.balise.StatutBalise;

import java.io.File;
import java.util.Base64;

@Slf4j
public class VisionBaliseOverSocket extends AbstractSocketClient<BaliseAction> implements IVisionBalise {

    public VisionBaliseOverSocket(String hostname, Integer port) {
        super(hostname, port);
    }

    public VisionBaliseOverSocket(File socketFile) {
        super(socketFile);
    }

    @Override
    public void end() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new ExitQuery(), ExitResponse.class);
            } catch (Exception e) {
                log.warn("Erreur de lecture", e);
            }
        }
        super.end();
    }

    @Override
    public void startDetection() {
        try {
            openIfNecessary();
            sendToSocketAndGet(new DetectionQuery(), DetectionResponse.class);

        } catch (Exception e) {
            log.warn("Erreur de récupération de la detection", e);
        }
    }

    @Override
    public StatutBalise getStatut() {
        try {
            openIfNecessary();
            StatusResponse response = sendToSocketAndGet(new StatusQuery(), StatusResponse.class);
            return response.getDatas();

        } catch (Exception e) {
            log.warn("Erreur de recupération du statut", e);
            return null;
        }
    }

    @Override
    public byte[] getPhoto(int width) {
        try {
            openIfNecessary();
            PhotoResponse response = sendToSocketAndGet(new PhotoQuery(width), PhotoResponse.class);
            return Base64.getDecoder().decode(response.getDatas());

        } catch (Exception e) {
            log.warn("Erreur de lecture", e);
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }
}
