package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class CarouselService {

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private ICarouselManager carouselManager;

    private AtomicBoolean working = new AtomicBoolean(false);

    /**
     * Verifie si le service est occupé
     */
    public boolean isWorking() {
        return working.get();
    }

    /**
     * Attends que le service se libère
     */
    public void waitAvailable(long waitTime) throws CarouselNotAvailableException {
        long remaining = waitTime;
        while (isWorking() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (isWorking()) {
            log.warn("Temps d'attente trop long du carousel");
            throw new CarouselNotAvailableException();
        } else {
            working.set(true);
        }
    }

    /**
     * Libère le service
     */
    public void release() {
        working.set(false);
    }

    /**
     * Lecture asynchrone d'une couleur
     */
    @Async
    public void lectureCouleurAsync(int index) {
        working.set(true);
        tourner(index - ICarouselManager.LECTEUR);

        CouleurPalet couleur = ioService.couleurPalet();
        carouselManager.setColor(ICarouselManager.LECTEUR, couleur);
        working.set(false);
    }

    /**
     * Tourne le carousel pour avoir une couleur en position
     */
    public boolean tourner(int index, CouleurPalet couleur) {
        int targetIndex = carouselManager.firstIndexOf(couleur, index);

        if (targetIndex == -1) {
            return false;
        }

        tourner(targetIndex - index);

        return true;
    }

    /**
     * Tourner dans le sens trigo
     */
    private void tourner(int nb) {
        carouselManager.tourneIndex(nb);
    }

    public void ejectionAvantRetourStand() {
        if (!carouselManager.has(CouleurPalet.ANY)) {
            return;
        }

        // ventouse en haut pour utiliser son capteur
        // barillet ouvert
        servosService.ascenseurDroitCarousel();
        servosService.pivotVentouseDroitCarouselVertical();
        servosService.porteBarilletDroitOuvert();

        while (carouselManager.has(CouleurPalet.ANY)) {
            tourner(carouselManager.firstIndexOf(CouleurPalet.ANY, ICarouselManager.VENTOUSE_DROITE));

            // on attend que la palet soit enlevé (ou tombe ?)
            while (ioService.presencePaletVentouseDroit()) {
                ThreadUtils.sleep(100);
            }
        }

        servosService.porteBarilletDroitFerme();
    }
}
