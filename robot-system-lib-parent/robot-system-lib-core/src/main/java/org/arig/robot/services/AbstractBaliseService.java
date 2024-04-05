package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Slf4j
public abstract class AbstractBaliseService<STATUT extends Serializable> {

    @Autowired
    protected IVisionBalise<STATUT> balise;

    private boolean detectionStarted = false;

    public boolean isConnected() {
        return balise.isOpen();
    }

    public boolean tryConnect() {
        try {
            balise.openSocket();
            log.info("Connecté à la balise");
            return true;
        } catch (Exception e) {
            log.warn("Impossible de se connecter à la balise");
            return false;
        }
    }

    public void heartbeat() {
        balise.heartbeat();
    }

    abstract public void updateStatus();

    public void startDetection() {
        if (!detectionStarted) {
            log.info("Démarrage de la détection balise");
            this.detectionStarted = balise.startDetection();
        }
    }

    public PhotoResponse getPhoto() {
        log.info("Prise d'une photo");
        return balise.getPhoto();
    }

    public EtalonnageResponse etalonnage() {
        log.info("Étalonnage de la balise");
        detectionStarted = false;
        return balise.etalonnage();
    }

    public void idle() {
        detectionStarted = false;
        balise.idle();
    }
}
