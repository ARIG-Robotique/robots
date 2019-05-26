package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Gestion du magasin
 */
@Service
@Slf4j
public class MagasinService implements InitializingBean {

    private static final int NB_MAX_MAGASIN = 3;
    private static final int TEMPS_MAX_AVAILABLE = 3000;

    @Autowired
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    private ServosService servosService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private ICarouselManager carouselManager;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    private RobotStatus rs;

    private Map<ESide, Boolean> ejection = new EnumMap<ESide, Boolean>(ESide.class);

    private Map<CouleurPalet, ESide> couleur2side = new EnumMap<CouleurPalet, ESide>(CouleurPalet.class);

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

        stockageAuto();
    }

    /**
     * Ouverture du magasin
     */
    public void startEjection(ESide side) {
        IRobotSide service = sideServices.get(side);
        ejection.put(side, true);
        service.ejectionMagasinOuvert(true);
    }

    private void endEjection(ESide side) {
        IRobotSide service = sideServices.get(side);
        if (ejection.get(service.id()) && service.nbPaletDansMagasin() == 0) {
            service.ejectionMagasinFerme(true);
            ejection.put(service.id(), false);
        }
    }

    /**
     * Stockage automatique dans le magasin si l'accelerateur et le magasin sont pleins ou qu'ilne reste plus beaucoup de temps
     */
    private void stockageAuto() {
        if (!carouselService.isWorking() && (
                rs.getRemainingTime() < 30000 ||
                        rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax &&
                                rs.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax
        )) {

            final Optional<CouleurPalet> couleur = Stream.of(CouleurPalet.ROUGE, CouleurPalet.VERT)
                    .filter(carouselManager::has)
                    .findFirst();

            if (couleur.isPresent()) {
                final ESide side = rs.getSideMagasin(couleur.get());

                if (side != null) {
                    try {
                        stockage(couleur.get(), side);
                    } catch (CarouselNotAvailableException e) {
                        log.warn("Impossible de stocker dans le magasin", e);
                    }
                }
            }
        }
    }

    /**
     * Stockage d'un palet dans le magasin depuis le carousel
     */
    public boolean stockage(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        IRobotSide service = sideServices.get(side);

        // TODO
//        if (service.nbPaletDansMagasin() >= NB_MAX_MAGASIN) {
//            log.warn("Le magasin est déjà plein");
//            return false;
//        }

        if (!carousel.has(couleur)) {
            log.warn("Pas de {} dans le carousel", couleur);
            return false;
        }

        int nbPaletInit = service.nbPaletDansMagasin();

        carouselService.waitAvailable(TEMPS_MAX_AVAILABLE);

        carouselService.tourner(service.positionCarouselMagasin(), couleur);

        service.trappeMagasinOuvert(true);
        ThreadUtils.sleep(200);
        service.trappeMagasinFerme(true);

        // TODO tinylidar
//        if (service.nbPaletDansMagasin() > nbPaletInit) {
        rs.getMagasin().get(side).add(couleur);
        carousel.unstore(service.positionCarouselMagasin());
        carouselService.release();
        return true;
//        } else {
//            log.warn("Un problème est survenu pendant le stockage");
//            carouselService.release();
//            return false;
//        }
    }

    public void ejectionAvantRetourStand() {
        IRobotSide rightSideService = sideServices.get(ESide.DROITE);
        IRobotSide leftSideService = sideServices.get(ESide.GAUCHE);

        while (rightSideService.nbPaletDansMagasin() > 0) {
            rightSideService.ejectionMagasinOuvert(true);
            rightSideService.ejectionMagasinFerme(true);

            ThreadUtils.sleep(1000);
        }

        while (leftSideService.nbPaletDansMagasin() > 0) {
            leftSideService.ejectionMagasinOuvert(true);
            leftSideService.ejectionMagasinFerme(true);

            ThreadUtils.sleep(1000);
        }
    }

}
