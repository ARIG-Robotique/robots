package org.arig.robot.system;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.encoders.AbstractEncoder;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.utils.ConvertionCarouselUnit;
import org.arig.robot.utils.SimpleCircularList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class CarouselManager implements ICarouselManager {

    @Autowired
    @Qualifier("asservissementCarousel")
    private IAsservissement asservissementCarousel;

    @Autowired
    private AbstractEncoder encoder;

    @Autowired
    @Qualifier("motorCarousel")
    private AbstractMotor motor;

    @Autowired
    private ConvertionCarouselUnit conv;

    @Autowired
    private CommandeAsservissementPosition cmdCarousel;

    @Getter
    private boolean positionAtteint = false;

    private final double arretPosition;

    private final SimpleCircularList<CouleurPalet> list = new SimpleCircularList<>(6, null);

    public CarouselManager(final double arretPosition) {
        this.arretPosition = arretPosition;
    }

    /**
     * Fonction permettant d'initialiser les composants externe pour le fonctionnement
     */
    @Override
    public void init() {
        // Initialisation de la carte codeur
        resetEncodeur();

        // Initialisation du contrôle moteurs
        motor.init();
        motor.printVersion();

        // Arret
        stop();
    }

    /**
     * Reset encodeurs.
     */
    @Override
    public void resetEncodeur() {
        encoder.reset();
    }

    @Override
    public void rawMotorSpeed(int speed) {
        motor.speed(speed);
    }

    /**
     * Stop.
     */
    @Override
    public void stop() {
        motor.stop();
        asservissementCarousel.reset(true);
    }

    /**
     * Process. Cette méthode permet de réaliser les fonctions lié aux déplacements.
     */
    @Override
    public void process() {
        // 1. Calcul de la position du carousel
        encoder.lectureValeur();

        // 2. Calcul de la consigne
        cmdCarousel.getConsigne().setValue((long) (cmdCarousel.getConsigne().getValue() - encoder.getValue()));

        // 3. Asservissement sur la consigne
        asservissementCarousel.process();

        // 4. Envoi aux moteurs
        motor.speed(cmdCarousel.getMoteur().getValue());

        // 5. Gestion des flags pour indiquer l'approche et l'atteinte sur l'objectif
        positionAtteint = Math.abs(cmdCarousel.getConsigne().getValue()) < arretPosition;
    }

    /**
     * Méthode permettant d'effectuer une rotation de nombre d'index de carousel.
     *
     * @param index Nombre d'index de rotation
     */
    @Override
    public void tourneIndex(final int index) {
        tourne(conv.indexToPulse(index));
    }

    @Override
    public void tourne(final long pulse) {
        list.rotate(conv.pulseToIndex(pulse));

        cmdCarousel.getConsigne().setValue(pulse);
        cmdCarousel.setFrein(true);

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode pour préparer le prochain mouvement.
     */
    private void prepareNextMouvement() {
        // Reset de l'erreur de l'asservissement sur le mouvement précédent lorsqu'il
        // s'agit d'un nouveau mouvement au départ vitesse presque nulle.
        if (positionAtteint) {
            asservissementCarousel.reset();
        }

        // Réinitialisation des infos de trajet.
        positionAtteint = false;
    }

    @Override
    public void setVitesse(long vitesse) {
        cmdCarousel.getVitesse().setValue(vitesse);
    }

    @Override
    public void waitMouvement() {
        log.info("Attente fin de rotation carousel");
        while (!isPositionAtteint()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.error("Problème dans l'attente d'atteinte de la position : {}", e.toString());
            }
        }
        log.info("Carousel en position");
    }

    /**
     * Vérifie si un emplacement est libre
     */
    @Override
    public boolean isFree(int index) {
        return list.get(index) == null;
    }

    /**
     * Retourne le palet à un emplacement
     */
    @Override
    public CouleurPalet get(int index) {
        return list.get(index);
    }

    /**
     * Vérifie s'il y a au moins un palet de la couleur
     */
    @Override
    public boolean has(CouleurPalet couleur) {
        return list.stream()
                .anyMatch(getPaletCouleurPredicate(couleur));
    }

    @Override
    public long count(CouleurPalet couleur) {
        return list.stream()
                .filter(getPaletCouleurPredicate(couleur))
                .count();
    }

    /**
     * Renvoie la premier position de la couleur, la plus proche d'une autre position
     */
    @Override
    public int firstIndexOf(CouleurPalet couleur, int ref) {
        Predicate<CouleurPalet> predicate = getPaletCouleurPredicate(couleur);
        for (int i = ref; i < ref + 6; i++) {
            int realIndex = i < 6 ? i : i - 6;
            if (predicate.test(get(realIndex))) {
                return realIndex;
            }
        }

        return -1;
    }

    /**
     * Change la couleur d'un palet
     */
    @Override
    public void setColor(int index, CouleurPalet couleur) {
        if (isFree(index)) {
            log.warn("L'emplacement {} était vide", index);
        } else {
            log.info("Changement de la couleur de {} : {}", index, couleur);
            list.set(index, couleur);
        }
    }

    /**
     * Stocke un palet à l'emplacement
     */
    @Override
    public boolean store(int index, CouleurPalet palet) {
        if (!isFree(index)) {
            return false;
        }

        log.info("Stockage d'un {} à {}", palet, index);

        list.set(index, palet);
        return true;
    }

    /**
     * Enlève le palet à l'emplacement
     */
    @Override
    public void unstore(int index) {
        if (isFree(index)) {
            log.warn("L'emplacement {} était déjà vide", index);
        } else {
            log.info("Destockage depuis {}", index);
            list.set(index, null);
        }
    }

    private Predicate<CouleurPalet> getPaletCouleurPredicate(CouleurPalet couleur) {
        return p -> {
            if (p == null && couleur == null) {
                return true;
            }
            if (p != null && couleur != null && couleur == CouleurPalet.ANY) {
                return true;
            }
            if (p != null && couleur != null && couleur == p) {
                return true;
            }
            return false;
        };
    }
}
