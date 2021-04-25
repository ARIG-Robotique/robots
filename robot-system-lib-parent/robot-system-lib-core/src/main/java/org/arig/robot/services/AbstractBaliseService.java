package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractBaliseService {

    @Autowired
    protected IVisionBalise balise;

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
            this.detectionStarted = balise.startDetection();
        }
    }

    public PhotoResponse getPhoto() {
        log.info("Prise d'une photo");
        return balise.getPhoto();
    }

    public EtalonnageResponse etalonnage() {
        log.info("Démarrage de l'étalonnage");
        detectionStarted = false;
        return balise.etalonnage();
    }

    public void idle() {
        balise.idle();
    }
}