package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.CarouselManager;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class CarouselService {

    @Autowired
    private IIOService ioService;

    @Autowired
    private RightSideService rightSideService;

    @Autowired
    private LeftSideService leftSideService;

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

        if (ioService.presenceLectureCouleur()) {
            ioService.enableLedCapteurCouleur();
            ThreadUtils.sleep(50);
            CouleurPalet couleur = ioService.couleurPalet();
            ioService.disableLedCapteurCouleur();

            carouselManager.setColor(ICarouselManager.LECTEUR, couleur);
        } else {
            carouselManager.setColor(ICarouselManager.LECTEUR, null);
        }

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
     * Tourner dans le sens le plus rapide
     */
    private void tourner(int nb) {
        if (nb > 3) {
            nb -= 6;
        } else if (nb < -3) {
            nb += 6;
        }
        carouselManager.tourneIndex(nb);
    }

    public void ejectionAvantRetourStand() {
        if (!carouselManager.has(CouleurPalet.ANY)) {
            return;
        }

        // ventouse en haut pour utiliser son capteurs
        rightSideService.porteBarilletOuvert(false);
        leftSideService.porteBarilletOuvert(false);
        rightSideService.ascenseurTableGold(false);
        leftSideService.ascenseurTableGold(true);

        for (int i = 0 ; i < 6 ; i++) {
            tourner(1);
        }

        ThreadUtils.sleep(1000);

        rightSideService.porteBarilletFerme(false);
        leftSideService.porteBarilletFerme(true);
    }
}
