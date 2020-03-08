package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gestion du magasin
 */
@Service
@Slf4j
public class MagasinService {

    private static final int TEMPS_MAX_AVAILABLE = 3000;

    @Autowired
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    private ServosService servosService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    private RobotStatus rs;

    private Map<CouleurPalet, ESide> couleur2side = new EnumMap<CouleurPalet, ESide>(CouleurPalet.class);

    /**
     * Fermeture automatique du magasin quand il est vide
     */
    public void process() {
        stockageAuto();
    }

    /**
     * Ouverture du magasin
     */
    public void startEjection() {
        sideServices.get(ESide.DROITE).ejectionMagasinOuvert(false);
        sideServices.get(ESide.GAUCHE).ejectionMagasinOuvert(true);
    }

    public void endEjection() {
        sideServices.get(ESide.DROITE).ejectionMagasinFerme(false);
        sideServices.get(ESide.GAUCHE).ejectionMagasinFerme(true);
    }

    public void startEjection(ESide side) {
        IRobotSide service = sideServices.get(side);
        service.ejectionMagasinOuvert(true);
    }

    public void endEjection(ESide side) {
        IRobotSide service = sideServices.get(side);
        service.ejectionMagasinFerme(true);
    }

    /**
     * Stockage automatique dans le magasin si l'accelerateur et le magasin sont pleins ou qu'ilne reste plus beaucoup de temps
     */
    private void stockageAuto() {
        // stockage intelligent en fonction du temps, de l'accel et de la balance
        if (rs.strategyActive(EStrategy.TIMER_MAGASIN)) {
            if (rs.getRemainingTime() < 30000 ||
                    rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax &&
                            rs.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax
            ) {
                final Optional<CouleurPalet> couleur = Stream.of(CouleurPalet.ROUGE, CouleurPalet.VERT)
                        .filter(carousel::has)
                        .findFirst();

                if (couleur.isPresent()) {
                    final ESide side = rs.getSideMagasin(couleur.get());

                    if (side != null) {
                        stockage(couleur.get(), side);
                    }
                }
            }

            // stockage con juste le rouge
        } else {
//            if (carouselService.isLocked()) {
            if (!carouselService.isRotating()) {
                List<ESide> coolSides = Stream.of(ESide.values())
                        .filter(side -> carousel.get(side.getPositionMagasin()) == CouleurPalet.ROUGE && rs.getMagasin().get(side).size() < IConstantesNerellConfig.nbPaletsMagasinMax)
                        .collect(Collectors.toList());

                if (!coolSides.isEmpty()) {
                    log.info("Palets en position pour le magasin {}", coolSides);
                    try {
                        carouselService.lock(ESide.DROITE.getPositionMagasin(), 1000);

                        if (coolSides.size() == 2) {
                            storeDoubleSide();
                        } else {
                            storeOneSide(CouleurPalet.ROUGE, coolSides.get(0));
                        }

                    } catch (CarouselNotAvailableException e) {
                        log.warn("Stockage magasin echoué", e);
                    }

                    carouselService.release(ESide.DROITE.getPositionMagasin());
                }
            }
//            }
//            else if (carousel.has(CouleurPalet.ROUGE)) {
//                Stream.of(ESide.values())
//                        .filter(s -> rs.getMagasin().get(s).size() < IConstantesNerellConfig.nbPaletsMagasinMax)
//                        .min((a, b) -> rs.getMagasin().get(a).size() - rs.getMagasin().get(b).size())
//                        .ifPresent(s -> stockage(CouleurPalet.ROUGE, s));
//            }
        }
    }

    /**
     * Stockage d'un palet dans le magasin depuis le carousel
     */
    public boolean stockage(CouleurPalet couleur, ESide side) {
        try {
            IRobotSide service = sideServices.get(side);

            if (rs.getMagasin().get(side).size() >= IConstantesNerellConfig.nbPaletsMagasinMax) {
                log.warn("Le magasin est déjà plein");
                return false;
            }

            if (!carousel.has(couleur)) {
                log.warn("Pas de {} dans le carousel", couleur);
                return false;
            }

            if (carousel.get(service.positionCarouselMagasin()) == couleur) {
                carouselService.lock(service.positionCarouselMagasin(), TEMPS_MAX_AVAILABLE);

            } else {
                carouselService.fullLock(service.positionCarouselMagasin(), TEMPS_MAX_AVAILABLE);

                carouselService.tourner(service.positionCarouselMagasin(), couleur);
            }

            storeOneSide(couleur, side);
            carouselService.release(service.positionCarouselMagasin());

            return true;

        } catch (CarouselNotAvailableException e) {
            carouselService.release(side.getPositionMagasin());
            log.warn("Stockage magasin echoué", e);
            return false;
        }
    }

    public void digerer(final CouleurPalet couleur) {
        log.info("Remplissage complet du magasin");

        carouselService.forceLectureCouleur();

        int k = 0;
        while (k < 6 && carousel.has(couleur) && (rs.getMagasin().get(ESide.DROITE).size() < IConstantesNerellConfig.nbPaletsMagasinMax || rs.getMagasin().get(ESide.GAUCHE).size() < IConstantesNerellConfig.nbPaletsMagasinMax)) {

            if (rs.getMagasin().get(ESide.DROITE).size() < IConstantesNerellConfig.nbPaletsMagasinMax && rs.getMagasin().get(ESide.GAUCHE).size() < IConstantesNerellConfig.nbPaletsMagasinMax) {
                int coolIndex = carousel.findAdjancent(couleur);

                if (coolIndex != -1) {
                    try {
                        carouselService.fullLock(ICarouselManager.MAGASIN_DROIT, TEMPS_MAX_AVAILABLE);
                        carouselService.tourner(coolIndex, ICarouselManager.MAGASIN_DROIT);

                        storeDoubleSide();

                    } catch (CarouselNotAvailableException e) {
                        log.warn("Stockage magasin echoué", e);
                    }

                    carouselService.release(ICarouselManager.MAGASIN_DROIT);

                    k += 2;
                    continue;
                }
            }

            Stream.of(ESide.values())
                    .filter(s -> rs.getMagasin().get(s).size() < IConstantesNerellConfig.nbPaletsMagasinMax)
                    .min(Comparator.comparingInt(a -> rs.getMagasin().get(a).size()))
                    .ifPresent(s -> stockage(couleur, s));

            k++;
        }
    }

    private void storeOneSide(final CouleurPalet couleur, final ESide side) {
        final IRobotSide service = sideServices.get(side);

        service.trappeMagasinOuvert(true);
        ThreadUtils.sleep(200);
        service.trappeMagasinFerme(true);

        rs.getMagasin().get(side).add(couleur);
        carousel.unstore(service.positionCarouselMagasin());
    }

    private void storeDoubleSide() {
        sideServices.get(ESide.DROITE).trappeMagasinOuvert(false);
        sideServices.get(ESide.GAUCHE).trappeMagasinOuvert(true);

        ThreadUtils.sleep(200);

        sideServices.get(ESide.DROITE).trappeMagasinFerme(false);
        sideServices.get(ESide.GAUCHE).trappeMagasinFerme(true);

        rs.getMagasin().get(ESide.DROITE).add(carousel.get(ICarouselManager.MAGASIN_DROIT));
        rs.getMagasin().get(ESide.GAUCHE).add(carousel.get(ICarouselManager.MAGASIN_GAUCHE));

        carousel.unstore(ICarouselManager.MAGASIN_DROIT);
        carousel.unstore(ICarouselManager.MAGASIN_GAUCHE);
    }
}
