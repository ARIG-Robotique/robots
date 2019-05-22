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

@Slf4j
@Service
public class VentousesService implements InitializingBean {

    private static final int TEMPS_TENTATIVE_ASPIRATION = 1000;
    private static final int TEMPS_MAX_AVAILABLE = 3000;

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
    public boolean isWorking(ESide side) {
        return working.get(side).get();
    }

    /**
     * Retourne la couleur qui est dans une ventouse
     */
    public CouleurPalet getCouleur(ESide side) {
        return couleur.get(side);
    }

    /**
     * Attends qu'une ventouse ce libère
     */
    public void waitAvailable(ESide side) throws VentouseNotAvailableException {
        long remaining = TEMPS_MAX_AVAILABLE;
        while (isWorking(side) && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (isWorking(side)) {
            throw new VentouseNotAvailableException();
        }

        working.get(side).set(true);
    }

    /**
     * Prise de palet au sol
     */
    @Async
    public CompletableFuture<Boolean> priseTable(CouleurPalet couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return CompletableFuture.completedFuture(false);
        }

        service.pivotVentouseTable(true);
        service.ascenseurTable(true);

        if (!service.presencePaletVentouse()) {
            log.warn("Pas de palet visible");
            return CompletableFuture.completedFuture(false);
        }

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            this.couleur.put(side, couleur);
        }

        return CompletableFuture.completedFuture(ok);
    }

    /**
     * Met la ventouse en position pour prendre dans le distributeur
     * A appeller avant d'avancer
     */
    @Async
    public CompletableFuture<Boolean> preparePriseDistributeur(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return CompletableFuture.completedFuture(false);
        }

        service.pivotVentouseFacade(false);
        service.ascenseurDistributeur(true);

        return CompletableFuture.completedFuture(true);
    }

    /**
     * Prise de palet dans le distributeur
     */
    @Async
    public CompletableFuture<Boolean> priseDistributeur(CouleurPalet couleur, ESide side) {
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
        }

        return CompletableFuture.completedFuture(ok);
    }

    /**
     * Termine le cycle de prise dans le distributeur
     * A appeller après avoir reculé pour pas percuter le décor
     */
    @Async
    public CompletableFuture<Void> finishPriseDistributeur(boolean ok, ESide side) {
        if (!ok) {
            return servosHomeAndDisablePompeAndRelease(side);
        } else {
            return stockageCarousel(side);
        }
    }

    /**
     * Met la ventouse en position pour prendre le goldenium
     * A appeller avant d'avancer
     */
    @Async
    public CompletableFuture<Void> preparePriseGoldenium(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pivotVentouseFacade(false);
        service.ascenseurGold(true);

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Prise du goldenium
     */
    @Async
    public CompletableFuture<Boolean> priseGoldenium(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            service.ascenseurCarousel(true);

            rs.setGoldeniumPrit(true);
            couleur.put(side, CouleurPalet.GOLD);
        }

        return CompletableFuture.completedFuture(ok);
    }

    /**
     * Termine le cycle de prise du goldenium
     * A appeller après avoir reculé pour pas percuter le décor
     */
    @Async
    public CompletableFuture<Void> finishPriseGoldenium(boolean ok, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!ok) {
            return servosHomeAndDisablePompeAndRelease(side);

        } else {
            service.pivotVentouseTable(false);
            service.ascenseurAccelerateur(false);

            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Mise en place pour dépose dans l'accélérateur
     * A faire avant d'avancer
     */
    @Async
    public CompletableFuture<Void> prepareDeposeAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (rs.isCarouselEnabled()) {
            service.pivotVentouseCarouselVertical(false);
            service.ascenseurAccelerateur(true);

        } else if (this.couleur.get(side) != null) {
            service.porteBarilletOuvert(true);

            service.ascenseurCarouselDepose(false);
            service.pivotVentouseCarouselSortie(true);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Mise en place pour la prise sur l'accélérateur
     * A faire avant d'avancer
     */
    @Async
    public CompletableFuture<Boolean> preparePriseAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return CompletableFuture.completedFuture(false);
        }

        service.pivotVentouseFacade(false);
        service.ascenseurAccelerateur(true);

        return CompletableFuture.completedFuture(true);
    }

    /**
     * Active le poussage !
     */
    @Async
    public CompletableFuture<Void> pousseAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurAction(true);
        service.pousseAccelerateurFerme(true);

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Prise du palet dans le distributeur
     */
    @Async
    public CompletableFuture<Boolean> priseAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse()) {
            log.warn("Pas de palet visible");
            return CompletableFuture.completedFuture(false);
        }

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            this.couleur.put(side, CouleurPalet.BLEU);
        }

        return CompletableFuture.completedFuture(ok);
    }

    /**
     * Dépose un palet dans l'accélérateur
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    @Async
    public CompletableFuture<Boolean> deposeAccelerateur(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        IRobotSide service = sideServices.get(side);

        if (rs.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax) {
            log.warn("L'accelerateur est plein");
            return CompletableFuture.completedFuture(false);
        }

        if (rs.isCarouselEnabled()) {
            priseCarousel(couleur, service);
        }

        if (this.couleur.get(side) == null) {
            return CompletableFuture.completedFuture(false);
        }

        if (!rs.isCarouselEnabled()) {
            service.pivotVentouseFacade(true);
            service.ascenseurAccelerateur(true);

            service.porteBarilletFerme(false);
        }

        service.airElectroVanne();

        service.pivotVentouseCarouselVertical(true);

        pousseAccelerateur(side);

        service.videElectroVanne();

        rs.getPaletsInAccelerateur().add(this.couleur.get(side));

        this.couleur.put(side, null);

        return CompletableFuture.completedFuture(true);
    }

    private void priseCarousel(CouleurPalet couleur, IRobotSide service) throws CarouselNotAvailableException {
        if (!carousel.has(couleur)) {
            log.warn("Le carousel est vide");
            return;
        }

        carouselService.waitAvailable(TEMPS_MAX_AVAILABLE);
        carouselService.tourner(service.positionCarouselVentouse(), couleur);

        service.pivotVentouseCarouselVertical(true);
        service.ascenseurCarousel(true);
        service.porteBarilletOuvert(true);

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (!ok) {
            service.porteBarilletFerme(false);
            return;
        }

        service.ascenseurCarouselDepose(true);
        service.pivotVentouseFacade(true);
        service.ascenseurAccelerateur(true);
        service.porteBarilletFerme(false);

        this.couleur.put(service.id(), carousel.get(service.positionCarouselVentouse()));

        carouselService.release();
        carousel.unstore(service.positionCarouselVentouse());
    }

    /**
     * Fin de dépose dans l'accelerateur
     * A faire après avoir reculé
     */
    @Async
    public CompletableFuture<Void> finishDeposeAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurFerme(false);

        return servosHomeAndDisablePompeAndRelease(side);
    }

    /**
     * Première phase de la dépose balance
     * A faire avant d'avancer
     */
    @Async
    public CompletableFuture<Boolean> deposeBalance1(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        IRobotSide service = sideServices.get(side);

        if (couleur == CouleurPalet.GOLD) {
            if (this.couleur.get(side) != CouleurPalet.GOLD) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne ventouse");
                return CompletableFuture.completedFuture(false);
            }

            service.ascenseurCarousel(false);
            service.pivotVentouseFacade(true);

        } else {
            if (rs.isCarouselEnabled()) {
                priseCarousel(couleur, service);
            }

            if (this.couleur.get(side) == null) {
                return CompletableFuture.completedFuture(false);
            }

            if (!rs.isCarouselEnabled()) {
                service.pivotVentouseFacade(false);
                service.ascenseurAccelerateur(true);
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    /**
     * Seconde phase de la dépose balance
     * A faire après avoir avancé
     */
    @Async
    public CompletableFuture<Boolean> deposeBalance2(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleur.get(side) == null) {
            log.warn("Rien dans la ventouse, impossible de déposer");
            return CompletableFuture.completedFuture(false);
        }

        service.disablePompeAVide();
        service.airElectroVanne();

        rs.getPaletsInBalance().add(this.couleur.get(side));
        this.couleur.put(side, null);

        ThreadUtils.sleep(500);

        return CompletableFuture.completedFuture(true);
    }

    /**
     * Fin de dépose balance
     * A faire après avoir reculé
     */
    @Async
    public CompletableFuture<Void> finishDepose(ESide side) {
        rs.enableSerrage();
        return servosHomeAndDisablePompeAndRelease(side);
    }

    /**
     * Depose du goldenium sur la table
     */
    @Async
    public CompletableFuture<Boolean> deposeGoldeniumTable(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleur.get(side) != CouleurPalet.GOLD) {
            log.warn("On a pas le goldenium, ou il n'est pas dans la bonne ventouse");
            return CompletableFuture.completedFuture(false);
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

        return CompletableFuture.completedFuture(true);
    }

    @Async
    public CompletableFuture<Boolean> deposeTable(ESide side) {
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

    private CompletableFuture<Void> servosHomeAndDisablePompeAndRelease(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.disablePompeAVide();
        service.releaseElectroVanne();

        service.pivotVentouseTable(false);
        service.ascenseurAccelerateur(true);

        couleur.put(side, null);
        working.get(side).set(false);

        return CompletableFuture.completedFuture(null);
    }

    private boolean tentativeAspiration(IRobotSide side) {
        ThreadUtils.sleep(TEMPS_TENTATIVE_ASPIRATION / 2);
        return true;

        // FIXME lecture vacuostat
//        long remaining = TEMPS_TENTATIVE_ASPIRATION;
//        while (!side.paletPrisDansVentouse() && remaining > 0) {
//            remaining -= 100;
//            ThreadUtils.sleep(100);
//        }
//
//        if (!side.paletPrisDansVentouse()) {
//            log.warn("Impossible d'aspirer le palet");
//            return false;
//        } else {
//            return true;
//        }
    }

    @Async
    public CompletableFuture<Void> stockageCarousel(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse() || couleur.get(side) == null) {
            log.info("Rien à stocker");
            return CompletableFuture.completedFuture(null);
        }

        try {
            if (rs.isCarouselEnabled()) {
                carouselService.waitAvailable(TEMPS_MAX_AVAILABLE * 2);

                if (!carouselService.tourner(service.positionCarouselVentouse(), null)) {
                    log.warn("Echec du carousel, pourtant il y avait une place ?");
                    throw new CarouselNotAvailableException();
                }

                service.porteBarilletOuvert(true);
                service.ascenseurCarouselDepose(true);

                service.pivotVentouseCarouselVertical(true);

                service.disablePompeAVide();
                service.airElectroVanne();

                service.ascenseurAccelerateur(true);
                service.porteBarilletFerme(true);

                service.videElectroVanne();

                service.pivotVentouseTable(false);
                carousel.store(service.positionCarouselVentouse(), couleur.get(side));

                carouselService.release();

            }

            service.pivotVentouseTable(false);
            service.ascenseurAccelerateur(true);

        } catch (CarouselNotAvailableException e) {
            service.disablePompeAVide();
            service.releaseElectroVanne();

        } finally {
            working.get(side).set(false);
            couleur.put(side, null);
        }

        return CompletableFuture.completedFuture(null);
    }

    // TODO factoriser ça
    @Async
    public CompletableFuture<Void> stockageCarouselMaisResteEnHaut(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse() || couleur.get(side) == null) {
            log.info("Rien à stocker");
            return CompletableFuture.completedFuture(null);
        }

        try {
            carouselService.waitAvailable(TEMPS_MAX_AVAILABLE * 2);

            if (!carouselService.tourner(service.positionCarouselVentouse(), null)) {
                log.warn("Echec du carousel, pourtant il y avait une place ?");
                throw new CarouselNotAvailableException();
            }

            service.porteBarilletOuvert(true);
            service.ascenseurCarousel(true);
            service.pivotVentouseCarouselVertical(true);

            service.disablePompeAVide();
            service.airElectroVanne();

            service.ascenseurAccelerateur(true);

            service.videElectroVanne();

            service.porteBarilletFerme(true);

            carousel.store(service.positionCarouselVentouse(), couleur.get(side));

            carouselService.release();

            service.pivotVentouseCarouselVertical(false);
            service.ascenseurAccelerateur(true);

        } catch (CarouselNotAvailableException e) {
            service.disablePompeAVide();
            service.releaseElectroVanne();

        } finally {
            couleur.put(side, null);
            working.get(side).set(false);
        }

        return CompletableFuture.completedFuture(null);
    }

}
