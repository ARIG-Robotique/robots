package org.arig.robot.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.SimpleCircularList;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    private ServosService servosService;

    private AtomicBoolean rotating = new AtomicBoolean(false);

    private List<AtomicBoolean> locks = new SimpleCircularList<>(6, (i) -> new AtomicBoolean(false));

    private int hintIndex = -1;
    private CouleurPalet hintCouleur = null;

    public boolean isLocked() {
        return locks.stream().anyMatch(AtomicBoolean::get);
    }

    public boolean isRotating() {
        return rotating.get();
    }

    /**
     * Prend un lock complet sur le service (attends que tous les locks soit libérés)
     */
    public void fullLock(int index, long waitTime) throws CarouselNotAvailableException {
        long remaining = waitTime;
        while (isLocked() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (isLocked()) {
            log.warn("Temps d'attente trop long du carousel pour lock complet\n VG:{} VD:{} LC:{} MD:{} MG:{} NA:{}",
                    locks.get(0).get(), locks.get(1).get(), locks.get(2).get(), locks.get(3).get(), locks.get(4).get(), locks.get(5).get());
            throw new CarouselNotAvailableException();
        } else {
            locks.get(index).set(true);
            rotating.set(true);
        }
    }

    /**
     * Prend un lock sur une seule position pour empecher la rotation (attends que la rotation en cours soit terminée)
     */
    public void lock(int index, long waitTime) throws CarouselNotAvailableException {
        long remaining = waitTime;
        while (isRotating() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (isRotating()) {
            log.warn("Temps d'attente trop long du carousel pour lock partiel");
            throw new CarouselNotAvailableException();
        } else {
            locks.get(index).set(true);
        }
    }

    public void disableRotatingGereManuellement() {
        rotating.set(false);
    }

    public void release(int index) {
        locks.get(index).set(false);
    }

    public void setHint(int index, CouleurPalet couleur) {
        log.info("Carousel hint {} at {}", couleur, index);
        hintIndex = index;
        hintCouleur = couleur;
    }

    public void clearHint() {
        setHint(-1, null);
    }

    public void lectureCouleur() {
        try {
            if (!rotating.get() && carouselManager.get(ICarouselManager.LECTEUR) == CouleurPalet.INCONNU) {
                lock(ICarouselManager.LECTEUR, 1000);

                doLectureCouleur();

                release(ICarouselManager.LECTEUR);

            } else if (!isLocked() && carouselManager.has(CouleurPalet.INCONNU)) {
                fullLock(ICarouselManager.LECTEUR, 1000);

                tourner(carouselManager.firstIndexOf(CouleurPalet.INCONNU, ICarouselManager.LECTEUR) - ICarouselManager.LECTEUR);

                doLectureCouleur();

                release(ICarouselManager.LECTEUR);
            }

        } catch (CarouselNotAvailableException e) {
            log.warn("Erreur pendant la lecture couleur", e);
        }
    }

    private void doLectureCouleur() {
        if (ioService.presenceLectureCouleur()) {
            ioService.enableLedCapteurCouleur();
            ThreadUtils.sleep(50);
            CouleurPalet couleur = ioService.couleurPalet();
            ioService.disableLedCapteurCouleur();

            carouselManager.setColor(ICarouselManager.LECTEUR, couleur);
        } else {
            carouselManager.setColor(ICarouselManager.LECTEUR, null);
        }
    }

    public void positionIdeale() {
        try {
            if (!isLocked() && !carouselManager.has(CouleurPalet.INCONNU)) {
                /*if (hintCouleur != null) {
                    log.info("Positionnement idéal {} at {}", hintCouleur, hintIndex);

                    // essaye de respecter l'hint d'une action
                    if (carouselManager.has(hintCouleur) && carouselManager.get(hintIndex) != hintCouleur) {
                        fullLock(hintIndex, 1000);

                        tourner(hintIndex, hintCouleur);

                        release(hintIndex);
                    }

                } else*/
                if (carouselManager.get(ICarouselManager.VENTOUSE_DROITE) != null && carouselManager.get(ICarouselManager.VENTOUSE_GAUCHE) != null) {
                    log.info("Positionnement idéal d'un vide");

                    // essaye de trouver deux places vides l'une a coté de l'autre
                    int coolIndex = -1;
                    for (int i = 0; i < 6; i++) {
                        if (carouselManager.get(i) == null && carouselManager.get(i == 5 ? 0 : i + 1) == null) {
                            coolIndex = i;
                            break;
                        }
                    }

                    if (coolIndex == -1) {
                        coolIndex = carouselManager.firstIndexOf(null, ICarouselManager.VENTOUSE_GAUCHE);
                    }

                    if (coolIndex != -1) {
                        fullLock(ICarouselManager.VENTOUSE_GAUCHE, 1000);

                        tourner(coolIndex - ICarouselManager.VENTOUSE_GAUCHE);

                        release(ICarouselManager.VENTOUSE_GAUCHE);
                    }
                }
            }

        } catch (CarouselNotAvailableException e) {
            log.warn("Erreur pendant le positionnement idéal du carousel", e);
        }
    }

    /**
     * Tourne le carousel pour avoir une couleur en position
     */
    public boolean tourner(int index, CouleurPalet couleur) throws CarouselNotAvailableException {
        int targetIndex = carouselManager.firstIndexOf(couleur, index);

        if (targetIndex == -1) {
            release(index);
            rotating.set(false);
            return false;
        }

        tourner(targetIndex - index);

        return true;
    }

    public void tourner(int index, int pos) throws CarouselNotAvailableException {
        tourner(index - pos);
    }

    /**
     * Tourner dans le sens le plus rapide
     */
    private void tourner(int nb) throws CarouselNotAvailableException {
        if (nb > 3) {
            nb -= 6;
        } else if (nb < -3) {
            nb += 6;
        }

        if (servosService.isTrappeMagasinDroitOuvert() || servosService.isTrappeMagasinGaucheOuvert()) {
            log.warn("Impossible de tourner le carousel avec des portes ouvertes");
            throw new CarouselNotAvailableException();
        }

        rotating.set(true);
        carouselManager.tourneIndex(nb);
        rotating.set(false);
    }

    @SneakyThrows
    public void ejectionAvantRetourStand() {
        if (!carouselManager.has(CouleurPalet.ANY)) {
            return;
        }

        // ventouse en haut pour utiliser son capteurs
        rightSideService.porteBarilletOuvert(false);
        leftSideService.porteBarilletOuvert(false);
        rightSideService.ascenseurTableGold(false);
        leftSideService.ascenseurTableGold(true);

        for (int i = 0; i < 6; i++) {
            tourner(1);
            carouselManager.unstore(i);
        }

        ThreadUtils.sleep(1000);

        rightSideService.porteBarilletFerme(false);
        leftSideService.porteBarilletFerme(true);
    }

    public void forceLectureCouleur() {
        int i = 0;

        while (i < 6 && carouselManager.has(CouleurPalet.INCONNU)) {
            lectureCouleur();
            i++;
        }
    }
}
