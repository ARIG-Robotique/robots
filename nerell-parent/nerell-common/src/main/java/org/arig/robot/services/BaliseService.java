package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.communication.balise.enums.CouleurDetectee;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class BaliseService {

    @Autowired
    private IVisionBalise balise;

    @Autowired
    private RobotStatus rs;

    private StatutBalise statut;

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
        statut = balise.getStatut();
    }

    public void startDetection() {
        balise.startDetection();
    }

    public byte[] getPhoto() {
        return balise.getPhoto(800);
    }

    public void lectureGirouette() {
        if (statut != null && statut.getDetection() != null) {
            rs.setDirectionGirouette(statut.getDetection().getDirection());
        }
    }

    public void lectureCouleurEccueil() {
        if (statut != null && statut.getDetection() != null) {
            boolean valid = Stream.of(statut.getDetection().getColors())
                    .allMatch(c -> c != CouleurDetectee.UNKNONW);

            if (valid) {
                rs.setCouleursEccueil(
                        Stream.of(statut.getDetection().getColors())
                                .map(c -> { // comme on lit sur le distributeur adverse, les couleurs sont inversées
                                    if (c == CouleurDetectee.RED) {
                                        return ECouleurBouee.VERT;
                                    } else {
                                        return ECouleurBouee.ROUGE;
                                    }
                                })
                                .collect(Collectors.toList())
                );
            }
        }
    }

    public void lectureEcueilAdverse() {
        if (statut != null && statut.getDetection() != null) {
            rs.setEccueilAdverseDispo(
                    Stream.of(statut.getDetection().getColors())
                            .filter(c -> c == CouleurDetectee.UNKNONW)
                            .count() < 4
            );
        }
    }

}
