package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.communication.balise.*;
import org.arig.robot.model.communication.balise.enums.BaliseAction;

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
    public void startEtallonage() {
        try {
            sendToSocketAndGet(new EtallonageQuery(), EtallonageResponse.class);

        } catch (Exception e) {
            log.warn("Erreur de lecture", e);
        }
    }

    @Override
    public void startDetection() {
        try {
            sendToSocketAndGet(new DetectionQuery(), DetectionResponse.class);

        } catch (Exception e) {
            log.warn("Erreur de lecture", e);
        }
    }

    @Override
    public StatutBalise getStatut() {
        try {
            StatusResponse response = sendToSocketAndGet(new StatusQuery(), StatusResponse.class);
            return response.getDatas();

        } catch (Exception e) {
            log.warn("Erreur de lecture", e);
            return null;
        }
    }

    @Override
    public byte[] getPhoto(int width) {
        try {
            PhotoResponse response = sendToSocketAndGet(new PhotoQuery(width), PhotoResponse.class);
            return Base64.getDecoder().decode(response.getDatas());

        } catch (Exception e) {
            log.warn("Erreur de lecture", e);
            return null;
        }
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
}
