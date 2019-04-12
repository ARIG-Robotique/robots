package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
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

    @Autowired
    private RobotStatus rs;

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
        StatutBalise statut = balise.getStatut();
        rs.setStatutBalise(statut);
        rs.setBaliseOk(statut != null);
    }

    public void startEtallonage() {
        balise.startEtallonage();
    }

    public void startDetection() {
        balise.startDetection();
    }

    public byte[] getPhoto() {
        return balise.getPhoto(800);
    }

}
