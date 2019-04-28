package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Carousel;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Gestion automatique de la lecture couleur quand le carousel est dispo
 */
@Slf4j
@Service
public class CarouselService {

    @Autowired
    private IIOService ioService;

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private ServosService servosService;

    @Autowired
    private ICarouselManager carouselManager;

    public void lectureCouleur(int index) {
        tourner(index - Carousel.LECTEUR);

        Palet.Couleur couleur = ioService.couleurPalet();
        robotStatus.getCarousel().setColor(Carousel.LECTEUR, couleur);
    }

    public boolean tourner(int index, Palet.Couleur couleur) {
        return tourner(index, Collections.singletonList(couleur));
    }

    public boolean tourner(int index, List<Palet.Couleur> couleurs) {
        int targetIndex = robotStatus.getCarousel().firstIndexOf(couleurs, index);

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
        robotStatus.getCarousel().rotate(nb);
        carouselManager.tourneIndex(nb);
    }

    public void ejectionAvantRetourStand() {
        if (!robotStatus.getCarousel().has(Palet.Couleur.ANY)) {
            return;
        }

        // ventouse en haut pour utiliser son capteur
        // barillet ouvert
        servosService.ascenseurDroitCarousel();
        servosService.pivotVentouseDroitCarousel();
        servosService.porteBarilletDroitOuvert();

        while (robotStatus.getCarousel().has(Palet.Couleur.ANY)) {
            tourner(robotStatus.getCarousel().firstIndexOf(Palet.Couleur.ANY, Carousel.PINCE_DROITE));

            // on attend que la palet soit enlevé (ou tombe ?)
            while (ioService.presencePaletVentouseDroit()) {
                ThreadUtils.sleep(100);
            }
        }

        servosService.porteBarilletDroitFerme();
    }
}
