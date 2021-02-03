package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.DetectionQuery;
import org.arig.robot.communication.socket.balise.DetectionResponse;
import org.arig.robot.communication.socket.balise.EchoQuery;
import org.arig.robot.communication.socket.balise.EchoResponse;
import org.arig.robot.communication.socket.balise.EtalonnageQuery;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.ExitQuery;
import org.arig.robot.communication.socket.balise.ExitResponse;
import org.arig.robot.communication.socket.balise.IdleQuery;
import org.arig.robot.communication.socket.balise.IdleResponse;
import org.arig.robot.communication.socket.balise.PhotoQuery;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.communication.socket.balise.StatusQuery;
import org.arig.robot.communication.socket.balise.StatusResponse;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.model.balise.StatutBalise;

import java.io.File;

@Slf4j
public class VisionBaliseOverSocket extends AbstractSocketClient<BaliseAction> implements IVisionBalise {

    public VisionBaliseOverSocket(String hostname, Integer port) {
        super(hostname, port, 5000);
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
    public void idle() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new IdleQuery(), IdleResponse.class);
            } catch (Exception e) {
                log.warn("Erreur de lecture", e);
            }
        }
    }

    @Override
    public void heartbeat() {
        if (isOpen()) {
            try {
                sendToSocketAndGet(new EchoQuery("hello"), EchoResponse.class);
            } catch (Exception e) {
                log.warn("Erreur de lecture", e);
            }
        }
    }

    @Override
    public EtalonnageResponse etalonnage() {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new EtalonnageQuery(), EtalonnageResponse.class);

        } catch (Exception e) {
            log.warn("Erreur de récupération de l'étalonnage", e);
            return null;
        }
    }

    @Override
    public DetectionResponse startDetection() {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new DetectionQuery(), DetectionResponse.class);
        } catch (Exception e) {
            log.warn("Erreur de récupération de la detection", e);
            return null;
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
    public PhotoResponse getPhoto() {
        try {
            openIfNecessary();
            return sendToSocketAndGet(new PhotoQuery(), PhotoResponse.class);

        } catch (Exception e) {
            log.warn("Erreur de recupération de la photo", e);
            return null;
        }
    }
}
