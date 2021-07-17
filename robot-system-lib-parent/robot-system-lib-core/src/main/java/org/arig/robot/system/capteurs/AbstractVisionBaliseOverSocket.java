package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractResponseWithData;
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
import org.arig.robot.communication.socket.balise.StatutQuery;
import org.arig.robot.communication.socket.balise.enums.BaliseAction;
import org.arig.robot.system.communication.AbstractSocketClient;

import java.io.File;
import java.io.Serializable;

@Slf4j
public abstract class AbstractVisionBaliseOverSocket<STATUT extends Serializable>
        extends AbstractSocketClient<BaliseAction>
        implements IVisionBalise<STATUT> {

    // type de retour de la méthode "getStatut"
    private final Class<? extends AbstractResponseWithData<BaliseAction, STATUT>> statutReponseType;

    public AbstractVisionBaliseOverSocket(String hostname, Integer port,
                                          Class<? extends AbstractResponseWithData<BaliseAction, STATUT>> statutReponseType) {
        super(hostname, port, 10000);
        this.statutReponseType = statutReponseType;
    }

    public AbstractVisionBaliseOverSocket(File socketFile,
                                          Class<? extends AbstractResponseWithData<BaliseAction, STATUT>> statutReponseType) {
        super(socketFile);
        this.statutReponseType = statutReponseType;
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
    public boolean startDetection() {
        try {
            openIfNecessary();
            DetectionResponse detectionResponse = sendToSocketAndGet(new DetectionQuery(), DetectionResponse.class);
            return detectionResponse.isOk();
        } catch (Exception e) {
            log.warn("Erreur de récupération de la detection", e);
            return false;
        }
    }

    @Override
    public STATUT getStatut() {
        try {
            openIfNecessary();
            AbstractResponseWithData<?, STATUT> response = sendToSocketAndGet(new StatutQuery(), statutReponseType);
            return response.getData();

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
