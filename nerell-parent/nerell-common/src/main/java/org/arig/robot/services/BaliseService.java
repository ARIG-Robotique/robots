package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.balise.DetectionResult;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BaliseService {

    @Autowired
    private IVisionBalise balise;

    private StatutBalise lastStatus = new StatutBalise();

    public boolean isConnected() {
        return balise.isOpen();
    }

    public boolean tryConnect() {
        try {
            balise.openSocket();
            return true;
        } catch (Exception e) {
            log.warn("Impossible de se connecter à la balise", e);
            return false;
        }
    }

    public void updateStatus() {
        lastStatus = balise.getStatut();
    }

    public void startEtallonage() {
        balise.startEtallonage();
    }

    public boolean isEtallonageOk() {
        return lastStatus.isEtallonageOk();
    }

    public void startDetection() {
        balise.startDetection();
    }

    // TODO méthodes plus spécialisées
    public DetectionResult getDetectionResult() {
        return lastStatus.getDetection();
    }

    public byte[] getPhoto() {
        return balise.getPhoto(800);
    }

}
