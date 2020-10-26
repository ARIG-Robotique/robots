package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.balise.EtalonnageBalise;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.communication.balise.enums.BoueeDetectee;
import org.arig.robot.model.communication.balise.enums.CouleurDetectee;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class BaliseService {

    @Autowired
    private IVisionBalise balise;

    @Autowired
    private NerellRobotStatus rs;

    private StatutBalise statut;
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

    public void updateStatus() {
        statut = balise.getStatut();
    }

    public void startDetection() {
        if (!detectionStarted) {
            detectionStarted = balise.startDetection();
        }
    }

    public String getPhoto() {
        log.info("Prise d'une photo");
        return balise.getPhoto();
    }

    public void lectureGirouette() {
        if (statut != null && statut.getDetection() != null) {
            rs.setDirectionGirouette(statut.getDetection().getDirection());
        }
    }

    public boolean lectureCouleurEcueil() {
        boolean valid = false;

        if (statut != null && statut.getDetection() != null && !ArrayUtils.isEmpty(statut.getDetection().getEcueil())) {
            valid = Stream.of(statut.getDetection().getEcueil())
                    .allMatch(c -> c != CouleurDetectee.UNKNOWN);

            if (valid) {
                final CouleurDetectee[] detection = statut.getDetection().getEcueil();
                final ECouleurBouee[] couleursAdverse = new ECouleurBouee[5];
                final ECouleurBouee[] couleursEquipe = new ECouleurBouee[5];

                // Récupération de gauche a droite par la balise.
                // Les pinces arrières sont dans l'autre sens, on inverse le tableau
                for (int i = 0; i < 5; i++) {
                    if (detection[4 - i] == CouleurDetectee.RED) {
                        couleursAdverse[i] = ECouleurBouee.ROUGE;
                        couleursEquipe[4 - i] = ECouleurBouee.VERT;
                    } else {
                        couleursAdverse[i] = ECouleurBouee.VERT;
                        couleursEquipe[4 - i] = ECouleurBouee.ROUGE;
                    }
                }
                rs.setCouleursEcueilCommunAdverse(couleursAdverse);
                rs.setCouleursEcueilCommunEquipe(couleursEquipe);
            }
        }

        return valid;
    }

    public boolean lectureCouleurBouees() {
        if (statut != null && statut.getDetection() != null && !ArrayUtils.isEmpty(statut.getDetection().getBouees())) {
            BoueeDetectee[] bouees = statut.getDetection().getBouees();
            for (int i = 0; i < bouees.length; i++) {
                // les bouees sont lues en partant de la plus proche de la balise
                // BLEU : 12=>7
                // JAUNE : 5=>10
                int numBouee = rs.getTeam() == ETeam.BLEU ? 12 - i : 5 + i;
                rs.bouee(numBouee).setPresente(bouees[i] == BoueeDetectee.PRESENT);
            }

            return true;
        }

        return false;
    }

    public void lectureEcueilAdverse() {
        if (statut != null && statut.getDetection() != null && !rs.isEcueilCommunAdversePris()) {
            byte nbBouees = (byte) Stream.of(statut.getDetection().getEcueil())
                    .filter(c -> c != CouleurDetectee.UNKNOWN)
                    .count();

            if (rs.getTeam() == ETeam.BLEU) {
                rs.setEcueilCommunJauneDispo(nbBouees);
            } else {
                rs.setEcueilCommunBleuDispo(nbBouees);
            }
        }
    }

    public EtalonnageBalise etalonnage(int[][] ecueil, int[][] bouees) {
        log.info("Démarrage de l'étalonnage");
        return balise.etalonnage(ecueil, bouees);
    }

    public void idle() {
        balise.idle();
    }
}
