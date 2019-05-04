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
    private RobotStatus robotStatus;

    @Autowired
    private ServosService servosService;

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
    public boolean priseTable(CouleurPalet couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        service.pivotVentouseTable();
        servosService.waitPivotVentouse();

        service.ascenseurTable();
        servosService.waitAscenseurVentouseShort();

        if (!service.presencePaletVentouse()) {
            log.warn("Pas de palet visible");
            return false;
        }

        service.enablePompeAVide();

        if (!tentativeAspiration(service)) {
            log.warn("Impossible d'aspirer le palet");
            return false;
        }

        this.couleur.put(side, couleur);

        service.pinceSerrageRepos();
        service.disablePompeAVide();

        return true;
    }

    /**
     * Met la ventouse en position pour prendre dans le distributeur
     * A appeller avant d'avancer
     */
    public boolean preparePriseDistributeur(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!carousel.has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        service.ascenseurDistributeur();
        service.pivotVentouseFacade();
        servosService.waitAscenseurVentouseLong();

        return true;
    }

    /**
     * Prise de palet dans le distributeur
     */
    public boolean priseDistributeur(CouleurPalet couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

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
     * Termine le cycle de prise dans le distributeur
     * A appeller après avoir reculé pour pas percuter le décor
     */
    @Async
    public void finishPriseDistributeurAsync(boolean ok, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!ok) {
            servosHomeAndDisablePompeAndRelease(side);
        } else {
            stockageAsync(side);
        }
    }

    /**
     * Met la ventouse en position pour prendre le goldenium
     * A appeller avant d'avancer
     */
    public boolean preparePriseGoldenium(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.ascenseurAccelerateur();
        service.pivotVentouseFacade();
        servosService.waitAscenseurVentouseShort();

        return true;
    }

    /**
     * Prise du goldenium
     */
    public boolean priseGoldenium(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (ok) {
            robotStatus.setGoldeniumPrit(true);
            couleur.put(side, CouleurPalet.GOLD);
        }

        return ok;
    }

    /**
     * Termine le cycle de prise du goldenium
     * A appeller après avoir reculé pour pas percuter le décor
     */
    @Async
    public void finishPriseGoldeniumAsync(boolean ok, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!ok) {
            servosHomeAndDisablePompeAndRelease(side);

        } else {
            service.ascenseurAccelerateur();
            service.pivotVentouseTable();
        }
    }

    /**
     * Mise en place pour dépose dans l'accélérateur
     * A faire avant d'avancer
     */
    public void prepareDeposeAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.ascenseurAccelerateur();
        service.pivotVentouseCarouselVertical();
        service.pousseAccelerateurStandby();
        servosService.waitAscenseurVentouseLong();
    }

    /**
     * Active le poussage !
     */
    public void pousseAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurAction();
        servosService.waitPousseAccelerateur();

        service.pousseAccelerateurStandby();
        servosService.waitPousseAccelerateur();
    }

    /**
     * Dépose un palet dans l'accélérateur
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    public boolean deposeAccelerateur(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        IRobotSide service = sideServices.get(side);

        if (robotStatus.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax) {
            log.warn("L'accelerateur est plein");
            return false;
        }

        CouleurPalet couleurFinale = priseCarousel(couleur, service);

        if (couleurFinale == null) {
            return false;
        }

        service.releaseElectroVanne();

        pousseAccelerateur(side);

        robotStatus.getPaletsInAccelerateur().add(couleurFinale);

        return true;
    }

    private CouleurPalet priseCarousel(CouleurPalet couleur, IRobotSide service) throws CarouselNotAvailableException {
        if (!carousel.has(couleur)) {
            log.warn("Le carousel est vide");
            return null;
        }

        carouselService.waitAvailable(TEMPS_MAX_AVAILABLE);
        carouselService.tourner(service.positionCarouselVentouse(), couleur);

        service.pivotVentouseCarouselVertical();
        servosService.waitPivotVentouse();

        service.ascenseurCarousel();
        servosService.waitAscenseurVentouseShort();

        service.porteBarilletOuvert();
        servosService.waitPorteBarillet();

        service.enablePompeAVide();
        boolean ok = tentativeAspiration(service);
        service.disablePompeAVide();

        if (!ok) {
            service.porteBarilletFerme();
            return null;
        }

        service.ascenseurCarouselDepose();
        servosService.waitAscenseurVentouseShort();

        service.pivotVentouseFacade();
        servosService.waitPivotVentouse();

        service.ascenseurAccelerateur();
        servosService.waitAscenseurVentouseLong();

        service.porteBarilletFerme();

        CouleurPalet couleurFinale = carousel.get(service.positionCarouselVentouse());

        carouselService.release();
        carousel.unstore(service.positionCarouselVentouse());

        return couleurFinale;
    }

    /**
     * Fin de dépose dans l'accelerateur
     * A faire après avoir reculé
     */
    @Async
    public void finishDeposeAccelerateurAsync(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurFerme();
        servosHomeAndDisablePompeAndRelease(side);
    }

    /**
     * Première phase de la dépose balance
     * A faire avant d'avancer
     */
    public boolean deposeBalance1(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        IRobotSide service = sideServices.get(side);

        if (couleur == CouleurPalet.GOLD) {
            if (this.couleur.get(side) != CouleurPalet.GOLD) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne ventouse");
                return false;
            }

            service.pivotVentouseFacade();
            servosService.waitPivotVentouse();

        } else {
            if (isWorking(side)) {
                log.warn("Ventouse déjà utilisée");
                return false;
            }

            CouleurPalet couleurFinale = priseCarousel(couleur, service);

            if (couleurFinale == null) {
                return false;
            }

            this.couleur.put(side, couleurFinale);
        }

        return true;
    }

    /**
     * Seconde phase de la dépose balance
     * A faire après avoir avancé
     */
    public boolean deposeBalance2(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleur.get(side) == null) {
            log.warn("Rien dans la ventouse, impossible de déposer");
            return false;
        }

        service.disablePompeAVide();
        service.releaseElectroVanne();

        robotStatus.getPaletsInBalance().add(couleur.get(side));
        couleur.put(side, null);

        return true;
    }

    /**
     * Fin de dépose balance
     * A faire après avoir reculé
     */
    @Async
    public void finishDeposeAsync(ESide side) {
        IRobotSide service = sideServices.get(side);

        servosHomeAndDisablePompeAndRelease(side);
    }

    /**
     * Depose du goldenium sur la table
     */
    public boolean deposeGoldenimTable(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleur.get(side) != CouleurPalet.GOLD) {
            log.warn("On a pas le goldenium, ou il n'est pas dans la bonne ventouse");
            return false;
        }

        service.pinceSerrageRepos();

        service.ascenseurTableGold();
        servosService.waitAscenseurVentouseLong();

        service.disablePompeAVide();
        service.releaseElectroVanne();

        service.ascenseurAccelerateur();

        robotStatus.getPaletsInTableauBleu().add(CouleurPalet.GOLD);
        couleur.put(side, null);

        return true;
    }

    private void servosHomeAndDisablePompeAndRelease(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.disablePompeAVide();
        service.releaseElectroVanne();

        service.ascenseurAccelerateur();
        servosService.waitAscenseurVentouseLong();

        service.pivotVentouseTable();
        servosService.waitPivotVentouse();

        couleur.put(side, null);
        working.get(side).set(false);
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

    @Async
    public void stockageAsync(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!service.presencePaletVentouse() || couleur.get(side) == null) {
            log.info("Rien à stocker");
            return;
        }

        try {
            carouselService.waitAvailable(TEMPS_MAX_AVAILABLE * 2);

            if (!carouselService.tourner(service.positionCarouselVentouse(), null)) {
                log.warn("Echec du carousel, pourtant il y avait une place ?");
                throw new CarouselNotAvailableException();
            }

            service.porteBarilletOuvert();
            servosService.waitPorteBarillet();

            service.ascenseurCarouselDepose();
            service.pivotVentouseCarouselSortie();
            servosService.waitAscenseurVentouseLong();

            service.pivotVentouseCarouselVertical();
            servosService.waitPivotVentouse();

            service.ascenseurCarousel();
            servosService.waitAscenseurVentouseLong();

            service.disablePompeAVide();
            service.releaseElectroVanne();

            service.ascenseurAccelerateur();
            servosService.waitAscenseurVentouseLong();

            service.porteBarilletFerme();
            servosService.waitPorteBarillet();

            service.pivotVentouseTable();

            carousel.store(service.positionCarouselVentouse(), couleur.get(side));

            carouselService.release();

        } catch (CarouselNotAvailableException e) {

        } finally {
            servosHomeAndDisablePompeAndRelease(side);
            couleur.put(side, null);
            working.get(side).set(false);
        }
    }

}