package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.communication.balise.enums.DirectionGirouette;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BaliseService {

    @Autowired
    private IVisionBalise balise;

    @Autowired
    private RobotStatus rs;

    private CircularFifoQueue<DirectionGirouette> historiqueDirectionGirouette = new CircularFifoQueue<>(IConstantesNerellConfig.directionGirouetteBuffer);

    public boolean isConnected() {
        return balise.isOpen();
    }

    public boolean tryConnect() {
        try {
            balise.openSocket();
            log.info("Connecté à la balise");
            return true;
        } catch (Exception e) {
            log.warn("Impossible de se connecter à la balise", e);
            return false;
        }
    }

    public void updateStatus() {
        StatutBalise statut = balise.getStatut();

        // 20 secondes après le début du match, on commence à lire la girouette
        // la direction est déterminée parmi les 10 dernières lectures (20 secondes normalement)
        // il faut 60% de lectures identiques pour que la valeur soit acceptée
        if (statut != null && rs.getElapsedTime() > 20000) {
            historiqueDirectionGirouette.add(statut.getDetection().getDirection());

            if (historiqueDirectionGirouette.isAtFullCapacity()) {
                final DirectionGirouette mostDetected = historiqueDirectionGirouette.stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream()
                        .filter(e -> 1.0f * e.getValue() / IConstantesNerellConfig.directionGirouetteBuffer >= IConstantesNerellConfig.directionGirouetteMajority)
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElse(DirectionGirouette.UNKNOWN);

                rs.setDirectionGirouette(mostDetected);
            }
        }
    }

    public void startDetection() {
        balise.startDetection();
    }

    public byte[] getPhoto() {
        return balise.getPhoto(800);
    }

}
