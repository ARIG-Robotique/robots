package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.balise.DetectionResult;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public boolean hasAtomesSurTable() {
        DetectionResult detectionResult = rs.getStatutBalise().getDetection();

        return detectionResult != null && (
                detectionResult.getFoundBlue().size() > 0 ||
                        detectionResult.getFoundGreen().size() > 0 ||
                        detectionResult.getFoundRed().size() > 0
        );
    }

    public List<Pair<CouleurPalet, Point>> getAtomes() {
        List<Pair<CouleurPalet, Point>> atomes = new ArrayList<>();
        DetectionResult detectionResult = rs.getStatutBalise().getDetection();

        detectionResult.getFoundRed().forEach(pt -> {
            atomes.add(Pair.of(CouleurPalet.ROUGE, pt));
        });
        detectionResult.getFoundGreen().forEach(pt -> {
            atomes.add(Pair.of(CouleurPalet.VERT, pt));
        });
        detectionResult.getFoundBlue().forEach(pt -> {
            atomes.add(Pair.of(CouleurPalet.BLEU, pt));
        });

        return atomes;
    }

    public long nbAtomes() {
        DetectionResult detectionResult = rs.getStatutBalise().getDetection();

        return detectionResult != null ?
                detectionResult.getFoundBlue().size() +
                        detectionResult.getFoundGreen().size() +
                        detectionResult.getFoundRed().size() : 0;
    }
}
