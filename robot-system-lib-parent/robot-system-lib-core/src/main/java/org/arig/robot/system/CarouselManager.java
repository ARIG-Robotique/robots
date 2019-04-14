package org.arig.robot.system;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.system.encoders.AbstractEncoder;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.utils.ConvertionCarouselUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Slf4j
public class CarouselManager implements ICarouselManager {

    @Autowired
    @Qualifier("asservissementCarousel")
    private IAsservissement asservissementCarousel;

    @Autowired
    private AbstractEncoder encoder;

    @Autowired
    private AbstractMotor motor;

    @Autowired
    private ConvertionCarouselUnit conv;

    @Autowired
    private CommandeAsservissementPosition cmdCarousel;

    @Getter
    private boolean positionAtteint = false;

    private final double arretPosition;

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
    public void tourne(final long pulse){
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
}
