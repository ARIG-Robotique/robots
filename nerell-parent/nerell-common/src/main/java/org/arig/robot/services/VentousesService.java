package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
@Service
public class VentousesService implements IVentousesService, InitializingBean {

    private static final int TEMPS_TENTATIVE_ASPIRATION = 1000;
    private static final int TEMPS_MAX_AVAILABLE = 6000;

    @Autowired
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private ICarouselManager carousel;

    private final Map<ESide, AtomicBoolean> working = new EnumMap<>(ESide.class);
    private final Map<ESide, CouleurPalet> couleur = new EnumMap<>(ESide.class);

    @Override
    public void afterPropertiesSet() {
        working.put(ESide.GAUCHE, new AtomicBoolean(false));
        working.put(ESide.DROITE, new AtomicBoolean(false));

        couleur.put(ESide.GAUCHE, null);
        couleur.put(ESide.DROITE, null);
    }

    /**
     * Vérifie si une ventouse est occupée
     */
    @Override
    public boolean isWorking(ESide side) {
        return working.get(side).get();
    }

    /**
     * Retourne la couleur qui est dans une ventouse
     */
    @Override
    public CouleurPalet getCouleur(ESide side) {
        return couleur.get(side);
    }

    /**
     * Attends qu'une ventouse ce libère
     */
    @Override
    public void waitAvailable(ESide side) throws VentouseNotAvailableException {
        long remaining = TEMPS_MAX_AVAILABLE;
        while (isWorking(side) && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (isWorking(side)) {
            throw new VentouseNotAvailableException();
        }

        log.info("Verrou de la ventouse {}", side);
        working.get(side).set(true);
    }

    /**
     * Prise de palet au sol
     */
    @Override
    public boolean priseTable(CouleurPalet couleur, ESide side) {
        log.info("Prise table à {}", side);

        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        service.pivotVentouseTable(true);
        service.ascenseurTable(true);

        if (!service.presencePaletVentouse()) {
            log.warn("Pas de palet visible");
            return false;
        }

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            this.couleur.put(side, couleur);
        }

        return ok;
    }

    /**
     * Met la ventouse en position pour prendre dans le distributeur
     * A appeller avant d'avancer
     */
    @Override
    @Async
    public CompletableFuture<Boolean> preparePriseDistributeur(ESide side) {
        log.info("Prépare prise distributeur {}", side);

        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return CompletableFuture.completedFuture(false);
        }

        service.pivotVentouseFacade(false);
        service.ascenseurDistributeur(true);

        service.enablePompeAVide();

        return CompletableFuture.completedFuture(true);
    }

    /**
     * Prise de palet dans le distributeur
     */
    @Override
    @Async
    public CompletableFuture<Boolean> priseDistributeur(CouleurPalet couleur, ESide side) {
        log.info("Prise distributeur à {}", side);

        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse()) {
            log.warn("Pas de palet visible");
            return CompletableFuture.completedFuture(false);
        }

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            this.couleur.put(side, couleur);

            service.ascenseurAccelerateur(false);
        }

        return CompletableFuture.completedFuture(ok);
    }

    /**
     * Termine le cycle de prise dans le distributeur
     * A appeller après avoir reculé pour pas percuter le décor
     */
    @Override
    @Async
    public CompletableFuture<Void> finishPriseDistributeur(boolean ok, ESide side) {
        log.info("Finish prise distributeur à {}", side);

        if (!ok) {
            servosHomeAndDisablePompeAndRelease(side);
        } else {
            stockageCarousel(side);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Met la ventouse en position pour prendre le goldenium
     * A appeller avant d'avancer
     */
    @Override
    public void preparePriseGoldenium(ESide side) {
        log.info("Prépare prise goldenium à {}", side);

        IRobotSide service = sideServices.get(side);

        service.pivotVentouseFacade(false);
        service.ascenseurGold(true);
    }

    /**
     * Prise du goldenium
     */
    @Override
    public boolean priseGoldenium(ESide side) {
        log.info("Prise goldenium à {}", side);

        IRobotSide service = sideServices.get(side);

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            service.ascenseurCarousel(true);

            rs.setGoldeniumPrit(true);
            couleur.put(side, CouleurPalet.GOLD);
        }

        return ok;
    }

    /**
     * Termine le cycle de prise du goldenium
     * A appeller après avoir reculé pour pas percuter le décor
     */
    @Override
    @Async
    public CompletableFuture<Void> finishPriseGoldenium(boolean ok, ESide side) {
        log.info("Finish prise goldenium à {}", side);

        IRobotSide service = sideServices.get(side);

        if (!ok) {
            servosHomeAndDisablePompeAndRelease(side);

        } else {
            service.pivotVentouseTable(false);
            service.ascenseurAccelerateur(false);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Mise en place pour dépose dans l'accélérateur
     * A faire avant d'avancer
     */
    @Override
    public void prepareDeposeAccelerateur(ESide side, ESide sideDepose) {
        log.info("Prépare dépose accelerateur à {}", side);

        IRobotSide service = sideServices.get(side);
        IRobotSide serviceDepose = sideServices.get(sideDepose);

        service.ascenseurAccelerateur(false);
        service.pivotVentouseCarouselVertical(true);

        serviceDepose.ascenseurAccelerateur(true);
        serviceDepose.pivotVentouseCarouselVertical(true);
    }

    /**
     * Mise en place pour la prise sur l'accélérateur
     * A faire avant d'avancer
     */
    @Override
    public boolean preparePriseAccelerateur(ESide side, ESide sideDepose) {
        log.info("Prépare prise accelerateur à {}", side);

        IRobotSide service = sideServices.get(side);
        IRobotSide serviceDepose = sideServices.get(sideDepose);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        service.pivotVentouseFacade(false);
        service.ascenseurAccelerateur(false);

        serviceDepose.pivotVentouseCarouselVertical(false);
        serviceDepose.ascenseurAccelerateur(true);

        return true;
    }

    /**
     * Active le poussage !
     */
    @Override
    public void pousseAccelerateur(ESide side) {
        log.info("Pousse accélérateur à {}", side);

        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurAction(true);
        service.pousseAccelerateurFerme(true);
    }

    /**
     * Prise du palet dans le distributeur
     */
    @Override
    public boolean priseAccelerateur(ESide side) {
        log.info("Prise accélerateur à {}", side);

        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse()) {
            log.warn("Pas de palet visible");
            return false;
        }

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            this.couleur.put(side, CouleurPalet.BLEU);
        }

        return ok;
    }

    /**
     * Dépose un palet dans l'accélérateur
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    @Override
    public boolean deposeAccelerateur(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        log.info("Dépose accelerateur à {}", side);

        IRobotSide service = sideServices.get(side);

        if (rs.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax) {
            log.warn("L'accelerateur est plein");
            return false;
        }

        priseCarousel(couleur, service, () -> {
            service.ascenseurAccelerateurDepose(true);
        });

        if (this.couleur.get(side) == null) {
            service.porteBarilletFerme(false);
            carouselService.release(service.positionCarouselVentouse());
            return false;
        }

        service.airElectroVanne();
        ThreadUtils.sleep(200);

        service.pivotVentouseCarouselVertical(false);
        service.ascenseurAccelerateur(true);

        service.porteBarilletFerme(true);
        carouselService.release(service.positionCarouselVentouse());

//        pousseAccelerateur(side);

        service.videElectroVanne();

        rs.getPaletsInAccelerateur().add(this.couleur.get(side));

        this.couleur.put(side, null);

        return true;
    }

    private void priseCarousel(CouleurPalet couleur, IRobotSide service, Runnable finish) throws CarouselNotAvailableException {
        if (!carousel.has(couleur)) {
            log.warn("Le carousel est vide");
            return;
        }

        carouselService.clearHint();

        if (carousel.get(service.positionCarouselVentouse()) == couleur) {
            // le carousel est déjà en position (coup de bol)
            carouselService.lock(service.positionCarouselVentouse(), TEMPS_MAX_AVAILABLE);

        } else {
            // il faut tourner le carousel
            carouselService.fullLock(service.positionCarouselVentouse(), TEMPS_MAX_AVAILABLE);

            carouselService.tourner(service.positionCarouselVentouse(), couleur);
        }

        service.porteBarilletOuvert(true);
        service.pivotVentouseCarouselVertical(true);
        service.ascenseurCarousel(true);

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (!ok) {
            service.airElectroVanne();
            service.ascenseurAccelerateur(false);
            service.porteBarilletFerme(false);

            carousel.setColor(service.positionCarouselVentouse(), CouleurPalet.INCONNU);

        } else {
            service.ascenseurCarouselDepose(true);
            service.pivotVentouseFacade(true);
            finish.run();

            this.couleur.put(service.id(), carousel.get(service.positionCarouselVentouse()));

            carousel.unstore(service.positionCarouselVentouse());
        }
    }

    /**
     * Fin de dépose dans l'accelerateur
     * A faire après avoir reculé
     */
    @Override
    public void finishDeposeAccelerateur(ESide side, ESide sideDepose) {
        log.info("Finish depose accelerateur à {}", side);

        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurFerme(false);

        servosHomeAndDisablePompeAndRelease(side);
        servosHomeAndDisablePompeAndRelease(sideDepose);
    }

    @Override
    public void prepareDeposeBalance(ESide side) {
        log.info("Prépare dépose balance à {}", side);

        IRobotSide service = sideServices.get(side);

        service.ascenseurAccelerateur(false);
        service.pivotVentouseCarouselVertical(true);
    }

    /**
     * Première phase de la dépose balance
     */
    @Override
    public boolean deposeBalance(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        log.info("Dépose balance à {}", side);

        IRobotSide service = sideServices.get(side);

        if (couleur == CouleurPalet.GOLD) {
            if (this.couleur.get(side) != CouleurPalet.GOLD) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne ventouse");
                return false;
            }

            service.ascenseurCarousel(false);
            service.pivotVentouseFacade(true);

            return true;

        } else {
            priseCarousel(couleur, service, () -> {
                service.ascenseurCarousel(true);
                service.porteBarilletFerme(false);
            });

            carouselService.release(service.positionCarouselVentouse());

            if (this.couleur.get(side) != null) {
                service.disablePompeAVide();
                service.airElectroVanne();

                rs.getPaletsInBalance().add(this.couleur.get(side));
                this.couleur.put(side, null);

                ThreadUtils.sleep(500);

                service.ascenseurAccelerateur(true);

                return true;
            }
        }

        return false;
    }

    /**
     * Fin de dépose balance
     * A faire après avoir reculé
     */
    @Override
    @Async
    public CompletableFuture<Void> finishDepose(ESide side) {
        log.info("Finish dépose à {}", side);

        rs.enableSerrage();
        servosHomeAndDisablePompeAndRelease(side);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Depose du goldenium sur la table
     */
    @Override
    public boolean deposeGoldeniumTable(ESide side) {
        log.info("Dépose goldenium table à {}", side);

        IRobotSide service = sideServices.get(side);

        if (couleur.get(side) != CouleurPalet.GOLD) {
            log.warn("On a pas le goldenium, ou il n'est pas dans la bonne ventouse");
            return false;
        }

        rs.disableSerrage();

        service.pinceSerrageRepos(false);
        service.ascenseurTableGold(true);

        service.disablePompeAVide();
        service.airElectroVanne();

        service.ascenseurAccelerateur(false);

        service.videElectroVanne();

        rs.getPaletsInTableauBleu().add(CouleurPalet.GOLD);
        this.couleur.put(side, null);

        return true;
    }

    @Override
    @Async
    public CompletableFuture<Boolean> deposeTable(ESide side) {
        log.info("Dépose table à {}", side);

        IRobotSide service = sideServices.get(side);

        if (couleur.get(side) == null) {
            log.info("Pas de palet à {}", side);
            return CompletableFuture.completedFuture(false);
        }

        rs.disableSerrage();

        service.pinceSerrageRepos(true);
        service.pivotVentouseTable(true);
        service.ascenseurTable(true);

        service.airElectroVanne();

        service.ascenseurDistributeur(true);

        service.videElectroVanne();

        rs.enableSerrage();

        this.couleur.put(side, null);

        return CompletableFuture.completedFuture(true);
    }

    private void servosHomeAndDisablePompeAndRelease(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.disablePompeAVide();
        service.releaseElectroVanne();

        service.pivotVentouseTable(false);
        service.ascenseurAccelerateur(true);

        couleur.put(side, null);
        releaseSide(side);
    }

    private boolean tentativeAspiration(IRobotSide side) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;
        while (!side.paletPrisDansVentouse() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (!side.paletPrisDansVentouse()) {
            log.warn("Impossible d'aspirer le palet");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void stockageCarousel(ESide side) {
        stockageCarousel(side, (IRobotSide service) -> {
            service.pivotVentouseTable(false);
            service.ascenseurAccelerateur(false);
        });
    }

    @Override
    public void stockageCarouselMaisResteEnHaut(ESide side) {
        stockageCarousel(side, (IRobotSide service) -> {
            service.pivotVentouseCarouselVertical(false);
            service.ascenseurAccelerateur(true);
        });
    }

    private void stockageCarousel(ESide side, Consumer<IRobotSide> finish) {
        log.info("Stockage carousel à {}", side);

        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse() || couleur.get(side) == null) {
            log.warn("Rien à stocker");
            return;
        }

        try {
            if (carousel.get(service.positionCarouselVentouse()) == null) {
                // le carousel est déjà en position
                carouselService.lock(service.positionCarouselVentouse(), TEMPS_MAX_AVAILABLE * 2);

            } else {
                // il faut tourner le carousel
                carouselService.fullLock(service.positionCarouselVentouse(), TEMPS_MAX_AVAILABLE * 2);

                if (!carouselService.tourner(service.positionCarouselVentouse(), null)) {
                    log.warn("Echec du carousel, pourtant il y avait une place ?");
                    throw new CarouselNotAvailableException();
                }
            }

            carousel.store(service.positionCarouselVentouse(), couleur.get(side));

            service.porteBarilletOuvert(true);
            service.ascenseurCarouselDepose(true);
            service.pivotVentouseCarouselVertical(true);

            service.disablePompeAVide();
            service.airElectroVanne();

            service.ascenseurAccelerateur(true);
            service.porteBarilletFerme(true);

            carouselService.release(service.positionCarouselVentouse());

            service.videElectroVanne();

            finish.accept(service);

        } catch (CarouselNotAvailableException e) {
            service.disablePompeAVide();
            service.releaseElectroVanne();

        } finally {
            releaseSide(side);
            couleur.put(side, null);
        }
    }

    @Override
    public void releaseSide(ESide side) {
        log.info("Release de la ventouse {}", side);
        working.get(side).set(false);
    }

}
