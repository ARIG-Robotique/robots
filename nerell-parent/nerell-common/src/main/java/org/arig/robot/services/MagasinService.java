package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestion du magasin
 */
@Service
@Slf4j
public class MagasinService implements InitializingBean {

    private static final int NB_MAX_MAGASIN = 3; // TODO à valider

    @Autowired
    private RightSideService rightSideService;

    @Autowired
    private LeftSideService leftSideService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private RobotStatus robotStatus;

    private Map<Integer, Boolean> ejection = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        ejection.put(rightSideService.id(), false);
        ejection.put(leftSideService.id(), false);
    }

    /**
     * Fermeture automatique du magasin quand il est vide
     */
    public void process() {
        endEjection(rightSideService);
        endEjection(leftSideService);
    }

    /**
     * Ouverture du magasin
     */
    public void startEjection(IRobotSide side) {
        ejection.put(side.id(), true);
        side.ejectionMagasinOuvert();
    }

    private void endEjection(IRobotSide side) {
        if (ejection.get(side.id()) && side.nbPaletDansMagasin() == 0) {
            side.ejectionMagasinFerme();
            ejection.put(side.id(), false);
        }
    }

    /**
     * Stockage d'un palet dans le magasin depuis le carousel
     */
    public boolean stockage(Palet.Couleur couleur, IRobotSide side) {
        if (side.nbPaletDansMagasin() >= NB_MAX_MAGASIN) {
            log.warn("Le magasin est déjà plein");
            return false;
        }

        if (!robotStatus.getCarousel().has(couleur)) {
            log.warn("Pas de {} dans le carousel", couleur);
            return false;
        }

        int nbPaletInit = side.nbPaletDansMagasin();

        carouselService.tourner(side.positionCarouselMagasin(), couleur);

        side.trappeMagasinOuvert();
        servosService.waitTrappeMagasin();

        side.trappeMagasinFerme();
        servosService.waitTrappeMagasin();

        if (side.nbPaletDansMagasin() > nbPaletInit) {
            robotStatus.getCarousel().unstore(side.positionCarouselMagasin());
            return true;
        } else {
            log.warn("Un problème est survenu pendant le stockage");
            return false;
        }
    }

    public void ejectionAvantRetourStand() {
        if (rightSideService.nbPaletDansMagasin() > 0 || leftSideService.nbPaletDansMagasin() > 0) {
            rightSideService.ejectionMagasinOuvert();
            leftSideService.ejectionMagasinOuvert();

            while (rightSideService.nbPaletDansMagasin() > 0 || leftSideService.nbPaletDansMagasin() > 0) {
                ThreadUtils.sleep(1000);
            }

            rightSideService.ejectionMagasinFerme();
            leftSideService.ejectionMagasinFerme();
        }
    }

}
