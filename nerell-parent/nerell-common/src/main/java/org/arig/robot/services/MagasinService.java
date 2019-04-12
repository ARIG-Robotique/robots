package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    private ServosService servosService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private RobotStatus robotStatus;

    private Map<ESide, Boolean> ejection = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        ejection.put(ESide.DROITE, false);
        ejection.put(ESide.GAUCHE, false);
    }

    /**
     * Fermeture automatique du magasin quand il est vide
     */
    public void process() {
        endEjection(ESide.GAUCHE);
        endEjection(ESide.DROITE);
    }

    /**
     * Ouverture du magasin
     */
    public void startEjection(ESide side) {
        IRobotSide service = sideServices.get(side);
        ejection.put(side, true);
        service.ejectionMagasinOuvert();
    }

    private void endEjection(ESide side) {
        IRobotSide service = sideServices.get(side);
        if (ejection.get(service.id()) && service.nbPaletDansMagasin() == 0) {
            service.ejectionMagasinFerme();
            ejection.put(service.id(), false);
        }
    }

    /**
     * Stockage d'un palet dans le magasin depuis le carousel
     */
    public boolean stockage(Palet.Couleur couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (service.nbPaletDansMagasin() >= NB_MAX_MAGASIN) {
            log.warn("Le magasin est déjà plein");
            return false;
        }

        if (!robotStatus.getCarousel().has(couleur)) {
            log.warn("Pas de {} dans le carousel", couleur);
            return false;
        }

        int nbPaletInit = service.nbPaletDansMagasin();

        carouselService.tourner(service.positionCarouselMagasin(), couleur);

        service.trappeMagasinOuvert();
        servosService.waitTrappeMagasin();

        service.trappeMagasinFerme();
        servosService.waitTrappeMagasin();

        if (service.nbPaletDansMagasin() > nbPaletInit) {
            robotStatus.getCarousel().unstore(service.positionCarouselMagasin());
            return true;
        } else {
            log.warn("Un problème est survenu pendant le stockage");
            return false;
        }
    }

    public void ejectionAvantRetourStand() {
        IRobotSide rightSideService = sideServices.get(ESide.DROITE);
        IRobotSide leftSideService = sideServices.get(ESide.GAUCHE);

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
