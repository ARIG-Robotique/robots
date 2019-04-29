package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.PinceNotAvailableException;
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
public class PincesService implements InitializingBean {

    private static final int TEMPS_TENTATIVE_ASPIRATION = 500;
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

    private static Map<ESide, AtomicBoolean> working = new EnumMap<>(ESide.class);

    private static Map<ESide, CouleurPalet> couleurInPince = new EnumMap<>(ESide.class);

    @Override
    public void afterPropertiesSet() {
        working.put(ESide.GAUCHE, new AtomicBoolean(false));
        working.put(ESide.DROITE, new AtomicBoolean(false));

        couleurInPince.put(ESide.GAUCHE, null);
        couleurInPince.put(ESide.DROITE, null);
    }

    /**
     * Vérifie si une pince est occupée
     */
    public boolean isWorking(ESide side) {
        return working.get(side).get();
    }

    /**
     * Retourne la couleur qui est dans une pince
     */
    public CouleurPalet couleurInPince(ESide side) {
        return couleurInPince.get(side);
    }

    /**
     * Attends qu'une pince ce libère
     */
    public void waitAvailable(ESide side) throws PinceNotAvailableException {
        long remaining = TEMPS_MAX_AVAILABLE;
        while (isWorking(side) && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (isWorking(side)) {
            throw new PinceNotAvailableException();
        }

        working.get(side).set(true);
    }

    /**
     * Libère une pince
     */
    public void release(ESide side) {
        working.get(side).set(false);
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
        servosService.waitAscenseurVentouse();

        service.enablePompeAVide();

        if (!tentativeAspiration(service)) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            service.releaseElectroVanne();
            service.ascenseurAndVentouseHome();
            release(side);
            return false;
        }

        couleurInPince.put(side, couleur);

        service.pinceSerrageRepos();

        return true;
    }

    /**
     * Met la pince en position pour prendre dans le distributeur
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
        servosService.waitAscenseurAndPivotVentouse();

        return true;
    }

    /**
     * Prise de palet dans le distributeur
     */
    public boolean priseDistributeur(CouleurPalet couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        service.enablePompeAVide();

        boolean ok = tentativeAspiration(service);

        if (!ok) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            service.releaseElectroVanne();

        } else {
            couleurInPince.put(side, couleur);
        }

        return ok;
    }

    /**
     * Termine le cycle de prise dans le distributeur
     * A appeller après avoir reculé pour pas percuter le décor
     */
    public void finishPriseDistributeur(boolean ok, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!ok) {
            service.ascenseurAndVentouseHome();
            release(side);
        }
    }

    /**
     * Met la pince en position pour prendre le goldenium
     * A appeller avant d'avancer
     */
    public boolean preparePriseGoldenium(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.ascenseurAccelerateur();
        service.pivotVentouseFacade();
        servosService.waitAscenseurAndPivotVentouse();

        return true;
    }

    /**
     * Prise du goldenium
     */
    public boolean priseGoldenium(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.enablePompeAVide();

        if (!tentativeAspiration(service)) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            service.releaseElectroVanne();
            return false;
        }

        robotStatus.setGoldeniumPrit(true);
        couleurInPince.put(side, CouleurPalet.GOLD);

        return true;
    }

    /**
     * Termine le cycle de prise du goldenium
     * A appeller après avoir reculé pour pas percuter le décor
     */
    public void finishPriseGoldenium(boolean ok, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!ok) {
            service.ascenseurAndVentouseHome();
            release(side);

        } else {
            service.ascenseurAccelerateur();
            service.pivotVentouseTable();
            servosService.waitPivotVentouse();

            service.disablePompeAVide(); // désactive la pompe à vide (mais on ne release pas)
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
        servosService.waitAscenseurAndPivotVentouse();
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

        if (!carousel.has(couleur)) {
            log.warn("Le carousel est vide");
            return false;
        }

        carouselService.waitAvailable(TEMPS_MAX_AVAILABLE);
        carouselService.tourner(service.positionCarouselPince(), couleur);

        CouleurPalet couleurFinale = carousel.get(service.positionCarouselPince());

        service.ascenseurCarousel();
        service.pivotVentouseCarouselVertical();
        servosService.waitAscenseurAndPivotVentouse();

        service.porteBarilletOuvert();
        servosService.waitPorteBarillet();

        service.enablePompeAVide();

        if (!tentativeAspiration(service)) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            service.releaseElectroVanne();
            service.porteBarilletFerme();
            release(side);
            return false;
        }

        service.ascenseurCarouselDepose();
        servosService.waitAscenseurVentouse();

        service.pivotVentouseCarouselSortie();
        servosService.waitPivotVentouse();

        service.pivotVentouseFacade();
        servosService.waitAscenseurVentouse();

        service.porteBarilletFerme();

        carouselService.release();
        carousel.unstore(service.positionCarouselPince());

        service.ascenseurAccelerateur();
        servosService.waitAscenseurVentouse();

        service.disablePompeAVide();
        service.releaseElectroVanne();

        pousseAccelerateur(side);

        robotStatus.getPaletsInAccelerateur().add(couleurFinale);

        return true;
    }

    /**
     * Fin de dépose dans l'accelerateur
     * A faire après avoir reculé
     */
    public void finishDeposeAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurFerme();
        service.ascenseurAndVentouseHome();

        release(side);
    }

    /**
     * Première phase de la dépose balance
     * A faire avant d'avancer
     */
    public boolean deposeBalance1(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException {
        IRobotSide service = sideServices.get(side);

        if (couleur == CouleurPalet.GOLD) {
            if (couleurInPince.get(side) != CouleurPalet.GOLD) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne pince");
                return false;
            }

            service.enablePompeAVide(); // reactive la pompe pour pas risque de le perdre

            service.ascenseurAccelerateur();
            service.pivotVentouseFacade();
            servosService.waitAscenseurAndPivotVentouse();

            couleurInPince.put(side, CouleurPalet.GOLD);

        } else {
            if (isWorking(side)) {
                log.warn("Pince déjà utilisée");
                return false;
            }

            if (!carousel.has(couleur)) {
                log.warn("Le carousel est vide");
                return false;
            }

            carouselService.waitAvailable(TEMPS_MAX_AVAILABLE);

            carouselService.tourner(service.positionCarouselPince(), couleur);

            service.ascenseurCarousel();
            service.pivotVentouseCarouselVertical();
            servosService.waitAscenseurAndPivotVentouse();

            service.porteBarilletOuvert();
            servosService.waitPorteBarillet();

            service.enablePompeAVide();

            if (!tentativeAspiration(service)) {
                log.warn("Impossible d'aspirer le palet");
                service.disablePompeAVide();
                service.releaseElectroVanne();
                service.porteBarilletFerme();
                release(side);
                return false;
            }

            service.ascenseurCarouselDepose();
            servosService.waitAscenseurVentouse();

            service.pivotVentouseCarouselSortie();
            servosService.waitPivotVentouse();

            service.ascenseurAccelerateur();
            service.pivotVentouseFacade();
            servosService.waitAscenseurAndPivotVentouse();

            service.porteBarilletFerme();

            couleurInPince.put(side, carousel.get(service.positionCarouselPince()));
            carousel.unstore(service.positionCarouselPince());

            carouselService.release();
        }

        return true;
    }

    /**
     * Seconde phase de la dépose balance
     * A faire après avoir avancé
     */
    public boolean deposeBalance2(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleurInPince.get(side) == null) {
            log.warn("Rien dans la pince, impossile de déposer");
            return false;
        }

        service.disablePompeAVide();
        service.releaseElectroVanne();

        robotStatus.getPaletsInBalance().add(couleurInPince.get(side));
        couleurInPince.put(side, null);
        release(side);

        return true;
    }

    /**
     * Fin de dépose balance
     * A faire après avoir reculé
     */
    public void finishDepose(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.disablePompeAVide();
        service.releaseElectroVanne();
        service.ascenseurAndVentouseHome();
        release(side);
    }

    /**
     * Depose du goldenium sur la table
     */
    public boolean deposeGoldenimTable(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleurInPince.get(side) != CouleurPalet.GOLD) {
            log.warn("On a pas le goldenium, ou il n'est pas dans la bonne pince");
            return false;
        }

        service.pinceSerrageRepos();

        service.pivotVentouseTable();
        servosService.waitPivotVentouse();

        service.ascenseurTableGold();
        servosService.waitAscenseurVentouse();

        service.disablePompeAVide();
        service.releaseElectroVanne();

        service.ascenseurAccelerateur();

        robotStatus.getPaletsInTableauBleu().add(CouleurPalet.GOLD);
        couleurInPince.put(side, null);

        return true;
    }

    private boolean tentativeAspiration(IRobotSide side) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;
        while (!side.paletPrisDansVentouse() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        return side.paletPrisDansVentouse();
    }

    @Async
    public void stockageAsync(ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleurInPince.get(side) == null) {
            log.info("Rien à stocker");
            return;
        }

        try {
            carouselService.waitAvailable(TEMPS_MAX_AVAILABLE * 2);

            if (!carouselService.tourner(service.positionCarouselPince(), null)) {
                log.warn("Echec du carousel, pourtant il y avait une place ?");
                service.disablePompeAVide();
                service.releaseElectroVanne();
                service.ascenseurAndVentouseHome();
                release(side);
                return;
            }

            service.porteBarilletOuvert();
            servosService.waitPorteBarillet();

            service.ascenseurCarouselDepose();
            service.pivotVentouseCarouselSortie();
            servosService.waitAscenseurAndPivotVentouse();

            service.pivotVentouseCarouselVertical();
            servosService.waitPivotVentouse();

            service.ascenseurCarousel();
            servosService.waitAscenseurVentouse();

            service.disablePompeAVide();
            service.releaseElectroVanne();

            service.ascenseurDistributeur();
            servosService.waitAscenseurVentouse();

            service.porteBarilletFerme();
            servosService.waitPorteBarillet();

            service.pivotVentouseTable();

            carousel.store(service.positionCarouselPince(), couleurInPince.get(side));

            carouselService.release();

        } catch (CarouselNotAvailableException e) {
            log.warn("Temps d'attente trop long du carousel");
            service.disablePompeAVide();
            service.releaseElectroVanne();
            service.ascenseurAndVentouseHome();

        } finally {
            couleurInPince.put(side, null);
            release(side);
        }
    }

}
