package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestion des pinces et des ventouses
 * <p>
 * Gère en automatique le stockage du sol vers la carousel quand il y a de la place
 * <p>
 * Méthodes pour stocker depuis les distributeurs et déposer dans l'accelerateur
 * /!\ Attendre `!isWorkingDroit()` ou `!isWorkingGauche()`
 */
@Slf4j
@Service
public class PincesService implements InitializingBean {

    private static final int NB_TENTATIVES_ASPIRATION = 2;
    private static final int TEMPS_TENTATIVE_ASPIRATION = 500;
    private static final int DISTANCE_DISTRIBUTEUR = 50; // distance à laquelle on se place du distributeur avant l'action
    private static final int DISTANCE_BALANCE = 50; // distance à laquelle on se place de balance avant l'action

    @Autowired
    private ServosService servosService;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private RobotStatus robotStatus;

    @Autowired
    private RightSideService rightSideService;

    @Autowired
    private LeftSideService leftSideService;

    @Autowired
    @Qualifier("trajectoryManager")
    private ITrajectoryManager trajectoryManager;

    private Map<Integer, Boolean> working = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        working.put(rightSideService.id(), false);
        working.put(leftSideService.id(), false);
    }

    /**
     * Gestion automatique de la prise de palets par devant
     */
    public void process() {
        if (!isWorkingDroite()) {
            stockageTable(Palet.Couleur.INCONNU, rightSideService);
        }
        if (!isWorkingGauche()) {
            stockageTable(Palet.Couleur.INCONNU, leftSideService);
        }
    }

    public boolean isWorkingDroite() {
        return working.get(rightSideService.id()) || robotStatus.getGoldeniumInPince() == rightSideService.id();
    }

    public boolean isWorkingGauche() {
        return working.get(leftSideService.id()) || robotStatus.getGoldeniumInPince() == leftSideService.id();
    }

    public boolean isWorking(IRobotSide side) {
        return working.get(side.id()) || robotStatus.getGoldeniumInPince() == side.id();
    }

    /**
     * Prise de palet au sol et stockage si possible
     */
    public boolean stockageTable(Palet.Couleur couleur, IRobotSide side) {
        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        if (!side.buteePalet() || !side.presencePalet()) {
            log.info("Pas de palet visible");
            return false;
        }

        if (!robotStatus.getCarousel().has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        working.put(side.id(), true);

        side.ascenseurTable();
        side.pivotVentouseTable();
        servosService.waitAscenseurAndPivotVentouse();

        side.enablePompeAVide();

        if (!tentativeAspirationTable(NB_TENTATIVES_ASPIRATION, side)) {
            log.warn("Impossible d'aspirer le palet");
            side.disablePompeAVide();
            side.ascenseurAndVentouseHome();
            working.put(side.id(), false);
            return false;
        }

        side.pinceSerrageOuvert();

        boolean ok = stockage(couleur, side);

        working.put(side.id(), false);

        return ok;
    }

    /**
     * Prise de palet dans le distributeur et stockage
     */
    public boolean stockageDistributeur(Palet.Couleur couleur, IRobotSide side) {
        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        if (!robotStatus.getCarousel().has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        working.put(side.id(), true);

        side.ascenseurDistributeur();
        side.pivotVentouseFacade();
        servosService.waitAscenseurAndPivotVentouse();

        trajectoryManager.avanceMMSansAngle(DISTANCE_DISTRIBUTEUR);

        side.enablePompeAVide();

        if (!tentativeAspirationFacade(NB_TENTATIVES_ASPIRATION, side)) {
            log.warn("Impossible d'aspirer le palet");
            trajectoryManager.reculeMMSansAngle(DISTANCE_DISTRIBUTEUR);
            side.disablePompeAVide();
            side.ascenseurAndVentouseHome();
            working.put(side.id(), false);
            return false;
        }

        trajectoryManager.reculeMMSansAngle(DISTANCE_DISTRIBUTEUR);

        boolean ok = stockage(couleur, side);

        working.put(side.id(), false);

        return ok;
    }

    private boolean stockage(Palet.Couleur couleur, IRobotSide side) {
        if (!carouselService.tourner(side.positionCarouselPince(), (Palet.Couleur) null)) {
            log.warn("Echec du carousel, pourtant il y avait une place ?");
            return false;
        }

        side.porteBarilletOuvert();
        side.ascenseurCarousel();
        side.pivotVentouseCarousel();
        servosService.waitAscenseurAndPivotVentouse();

        side.porteBarilletFerme();
        servosService.waitPorteBarillet();

        side.disablePompeAVide();
        side.ascenseurAndVentouseHome();

        robotStatus.getCarousel().store(side.positionCarouselPince(), new Palet().couleur(couleur));
        carouselService.lectureCouleur(side.positionCarouselPince());

        return true;
    }

    /**
     * Violet, côté droit | jaune, côté gauche
     */
    public boolean stockageGoldenium(IRobotSide side) {
        if (robotStatus.getGoldeniumInPince() != 0) {
            log.warn("Le goldenium est déjà prit");
            return false;
        }

        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        working.put(side.id(), true);

        side.ascenseurAccelerateur();
        side.pivotVentouseFacade();
        servosService.waitAscenseurAndPivotVentouse();

        side.enablePompeAVide();

        if (!tentativeAspirationFacade(NB_TENTATIVES_ASPIRATION, side)) {
            log.warn("Impossible d'aspirer le palet");
            side.disablePompeAVide();
            working.put(side.id(), false);
            return false;
        }

        robotStatus.setGoldeniumInPince(side.id());

        working.put(side.id(), true);

        return false;
    }

    /**
     * Mise en place pour dépose, à faire avant d'avancer
     */
    public void prepareDeposeAccelerateur(IRobotSide side) {
        side.ascenseurAccelerateur();
        side.pivotVentouseFacade();
        side.pousseAccelerateurStandby();
        servosService.waitAscenseurAndPivotVentouse();
    }

    public void pousseAccelerateur(IRobotSide side) {
        side.pousseAccelerateurAction();
        servosService.waitPousseAccelerateur();

        side.pousseAccelerateurStandby();
        servosService.waitPousseAccelerateur();
    }

    /**
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    public boolean deposeAccelerateur(Palet.Couleur couleur, IRobotSide side) {
        if (robotStatus.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax) {
            log.warn("L'accelerateur est plein");
            return false;
        }

        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        if (!robotStatus.getCarousel().has(couleur)) {
            log.warn("Le carousel est vide");
            return false;
        }

        working.put(side.id(), true);

        carouselService.tourner(side.positionCarouselPince(), couleur);

        Palet.Couleur couleurFinale = robotStatus.getCarousel().get(side.positionCarouselPince()).couleur();

        side.ascenseurCarousel();
        side.pivotVentouseCarousel();
        servosService.waitAscenseurAndPivotVentouse();

        side.enablePompeAVide();

        if (!tentativeAspirationCarousel(NB_TENTATIVES_ASPIRATION, side)) {
            log.warn("Impossible d'aspirer le palet");
            side.disablePompeAVide();
            working.put(side.id(), false);
            return false;
        }

        side.porteBarilletOuvert();
        servosService.waitPorteBarillet();

        side.ascenseurAccelerateur();
        side.pivotVentouseFacade();
        servosService.waitAscenseurAndPivotVentouse();

        side.porteBarilletFerme();

        robotStatus.getCarousel().unstore(side.positionCarouselPince());

        side.disablePompeAVide();

        pousseAccelerateur(side);

        robotStatus.getPaletsInAccelerateur().add(couleurFinale);

        working.put(side.id(), false);

        return true;
    }

    /**
     * Fin de dépose dans l'accelerateur
     */
    public void finDeposeAccelerateur(IRobotSide side) {
        side.pousseAccelerateurFerme();
        side.ascenseurAndVentouseHome();
        servosService.waitAscenseurAndPivotVentouse();
    }

    /**
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    public boolean deposeBalance(Palet.Couleur couleur, IRobotSide side) {
        if (robotStatus.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax) {
            log.warn("L'accelerateur est plein");
            return false;
        }

        if (couleur == Palet.Couleur.GOLD) {
            if (robotStatus.getGoldeniumInPince() != side.id()) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne pince");
                return false;
            }

            working.put(side.id(), true);

            side.ascenseurAccelerateur();
            servosService.waitAscenseurVentouse();

            trajectoryManager.avanceMMSansAngle(DISTANCE_BALANCE);

            side.disablePompeAVide();

            trajectoryManager.reculeMMSansAngle(DISTANCE_BALANCE);

            robotStatus.setGoldeniumInPince(0);
            robotStatus.getPaletsInBalance().add(couleur);

            working.put(side.id(), false);

        } else {
            if (isWorking(side)) {
                log.warn("Pince déjà utilisée");
                return false;
            }

            if (!robotStatus.getCarousel().has(couleur)) {
                log.warn("Le carousel est vide");
                return false;
            }

            working.put(side.id(), true);

            carouselService.tourner(side.positionCarouselPince(), couleur);

            Palet.Couleur couleurFinale = robotStatus.getCarousel().get(side.positionCarouselPince()).couleur();

            side.ascenseurCarousel();
            side.pivotVentouseCarousel();
            servosService.waitAscenseurAndPivotVentouse();

            side.enablePompeAVide();

            if (!tentativeAspirationCarousel(NB_TENTATIVES_ASPIRATION, side)) {
                log.warn("Impossible d'aspirer le palet");
                side.disablePompeAVide();
                working.put(side.id(), false);
                return false;
            }

            side.porteBarilletOuvert();
            servosService.waitPorteBarillet();

            side.ascenseurAccelerateur();
            side.pivotVentouseFacade();
            servosService.waitAscenseurAndPivotVentouse();

            trajectoryManager.avanceMMSansAngle(DISTANCE_BALANCE);

            side.porteBarilletFerme();

            robotStatus.getCarousel().unstore(side.positionCarouselPince());

            side.disablePompeAVide();

            trajectoryManager.reculeMMSansAngle(DISTANCE_BALANCE);

            robotStatus.getPaletsInBalance().add(couleurFinale);

            working.put(side.id(), false);
        }

        return true;
    }

    private boolean tentativeAspirationFacade(int nb, IRobotSide side) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;
        while (!side.paletPrisDansVentouse() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (!side.paletPrisDansVentouse()) {
            if (nb > 0) {
                trajectoryManager.reculeMMSansAngle(DISTANCE_DISTRIBUTEUR);
                trajectoryManager.avanceMMSansAngle(DISTANCE_DISTRIBUTEUR);

                return tentativeAspirationFacade(nb - 1, side);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean tentativeAspirationTable(int nb, IRobotSide side) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;
        while (!side.paletPrisDansVentouse() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (!side.paletPrisDansVentouse()) {
            if (nb > 0) {
                side.ascenseurDistributeur();
                servosService.waitAscenseurVentouse();

                side.ascenseurTable();
                servosService.waitAscenseurVentouse();

                return tentativeAspirationTable(nb - 1, side);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean tentativeAspirationCarousel(int nb, IRobotSide side) {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;
        while (!side.paletPrisDansVentouse() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (!side.paletPrisDansVentouse()) {
            if (nb > 0) {
                side.ascenseurAccelerateur();
                servosService.waitAscenseurVentouse();

                side.ascenseurCarousel();
                servosService.waitAscenseurVentouse();

                return tentativeAspirationCarousel(nb - 1, side);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
