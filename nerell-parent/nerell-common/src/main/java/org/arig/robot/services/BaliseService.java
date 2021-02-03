package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.communication.socket.balise.DetectionResponse;
import org.arig.robot.communication.socket.balise.EtalonnageResponse;
import org.arig.robot.communication.socket.balise.PhotoResponse;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.communication.balise.enums.ECouleurDetectee;
import org.arig.robot.model.communication.balise.enums.EPresenceBouee;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public void heartbeat() {
        balise.heartbeat();
    }

    public void updateStatus() {
        statut = balise.getStatut();
    }

    public void startDetection() {
        if (!detectionStarted) {
            DetectionResponse response = balise.startDetection();
            this.detectionStarted = response != null && response.isOk();
        }
    }

    public PhotoResponse getPhoto() {
        log.info("Prise d'une photo");
        return balise.getPhoto();
    }

    public void lectureGirouette() {
        if (statut != null && statut.getDetection() != null) {
            rs.directionGirouette(statut.getDetection().getGirouette());
        }
    }

    public boolean lectureCouleurEcueilEquipe() {
        boolean valid = false;

        if (statut != null && statut.getDetection() != null && !ArrayUtils.isEmpty(statut.getDetection().getEcueilEquipe())) {
            valid = Stream.of(statut.getDetection().getEcueilEquipe())
                    .allMatch(c -> c != ECouleurDetectee.UNKNOWN);

            if (valid) {
                final ECouleurDetectee[] detection = statut.getDetection().getEcueilEquipe();
                final ECouleurBouee[] couleursAdverse = new ECouleurBouee[5];
                final ECouleurBouee[] couleursEquipe = new ECouleurBouee[5];

                // Les pinces arrières sont dans l'autre sens, on inverse le tableau
                for (int i = 0; i < 5; i++) {
                    if (detection[i] == ECouleurDetectee.RED) {
                        couleursEquipe[4 - i] = ECouleurBouee.ROUGE;
                        couleursAdverse[i] = ECouleurBouee.VERT;
                    } else {
                        couleursEquipe[4 - i] = ECouleurBouee.VERT;
                        couleursAdverse[i] = ECouleurBouee.ROUGE;
                    }
                }
                rs.couleursEcueilCommunAdverse(couleursAdverse);
                rs.couleursEcueilCommunEquipe(couleursEquipe);
            }
        }

        return valid;
    }

    public boolean lectureCouleurBouees() {
        if (statut != null && statut.getDetection() != null && !ArrayUtils.isEmpty(statut.getDetection().getBouees())) {
            EPresenceBouee[] bouees = statut.getDetection().getBouees();
            for (int i = 0; i < bouees.length; i++) {
                rs.bouee(12 - i).setPresente(bouees[i] == EPresenceBouee.PRESENT);
            }

            return true;
        }

        return false;
    }

    public void lectureEcueilAdverse() {
        if (statut != null && statut.getDetection() != null && !rs.ecueilCommunAdversePris()) {
            byte nbBouees = (byte) Stream.of(statut.getDetection().getEcueilAdverse())
                    .filter(c -> c != ECouleurDetectee.UNKNOWN)
                    .count();

            if (rs.team() == ETeam.BLEU) {
                rs.ecueilCommunJauneDispo(nbBouees);
            } else {
                rs.ecueilCommunBleuDispo(nbBouees);
            }
        }
    }

    public void lectureHautFond() {
        if (statut != null && statut.getDetection() != null) {

            // construit les bouées détectées, avec un flag pour savoir si elles sont nouvelles ou pas
            final List<MutablePair<Bouee, Boolean>> hautFondDetecte = Stream.of(statut.getDetection().getHautFond())
                    .map(b -> {
                        ECouleurBouee couleur = b.getCol() == ECouleurDetectee.GREEN ? ECouleurBouee.VERT :
                                b.getCol() == ECouleurDetectee.RED ? ECouleurBouee.ROUGE : ECouleurBouee.INCONNU;
                        return new MutablePair<>(new Bouee(couleur, new Point(b.getPos()[0], 2000 - b.getPos()[1])), false);
                    })
                    .collect(Collectors.toList());

            final List<Bouee> nouveauHautFond = new ArrayList<>();

            // conserve les bouées qui n'ont pas bougé (delta < 1cm)
            rs.hautFond().forEach(bouee -> {
                Optional<MutablePair<Bouee, Boolean>> boueeExistante = hautFondDetecte.stream()
                        .filter(pair -> pair.getKey().couleur() == bouee.couleur() &&
                                Math.abs(pair.getKey().pt().getX() - bouee.pt().getX()) < 10 &&
                                Math.abs(pair.getKey().pt().getY() - bouee.pt().getY()) < 10)
                        .findFirst();

                if (boueeExistante.isPresent()) {
                    boueeExistante.get().setValue(true);
                    nouveauHautFond.add(bouee);
                }
            });

            // ajoute les bouées qui ont bougé
            hautFondDetecte.stream()
                    .filter(pair -> !pair.getValue())
                    .forEach(pair -> nouveauHautFond.add(pair.getKey()));

            rs.hautFond(nouveauHautFond);
        }
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
