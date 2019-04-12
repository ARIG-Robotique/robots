package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    @Qualifier("trajectoryManager")
    private ITrajectoryManager trajectoryManager;

    private Map<ESide, Boolean> working = new HashMap<>();

    private Map<ESide, Palet.Couleur> expected = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        working.put(ESide.GAUCHE, false);
        working.put(ESide.DROITE, false);
    }

    /**
     * Gestion automatique de la prise de palets par devant
     */
    public void process() {
        if (!isWorkingDroite()) {
            stockageTable(null, ESide.DROITE);
        }
        if (!isWorkingGauche()) {
            stockageTable(null, ESide.GAUCHE);
        }
    }

    public boolean isWorkingDroite() {
        return working.get(ESide.DROITE) || robotStatus.getGoldeniumInPince() == ESide.DROITE;
    }

    public boolean isWorkingGauche() {
        return working.get(ESide.GAUCHE) || robotStatus.getGoldeniumInPince() == ESide.GAUCHE;
    }

    public boolean isWorking(ESide side) {
        return working.get(side) || robotStatus.getGoldeniumInPince() == side;
    }

    public void setExpected(ESide side, Palet.Couleur couleur) {
        expected.put(side, couleur);
    }

    /**
     * Prise de palet au sol et stockage si possible
     */
    public boolean stockageTable(Palet.Couleur couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        if (!service.buteePalet() || !service.presencePalet()) {
            log.info("Pas de palet visible");
            return false;
        }

        if (!robotStatus.getCarousel().has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        if (couleur == null) {
            couleur = Palet.Couleur.INCONNU;
            if (expected.containsKey(side)) {
                couleur = expected.get(side);
                setExpected(side, null);
            }
        }

        working.put(side, true);

        service.ascenseurTable();
        service.pivotVentouseTable();
        servosService.waitAscenseurAndPivotVentouse();

        service.enablePompeAVide();

        if (!tentativeAspirationTable(NB_TENTATIVES_ASPIRATION, service)) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            service.ascenseurAndVentouseHome();
            working.put(side, false);
            return false;
        }

        service.pinceSerrageOuvert();

        boolean ok = stockage(couleur, side);

        working.put(side, false);

        return ok;
    }

    /**
     * Prise de palet dans le distributeur et stockage
     * ATTENTION déplacement intégré
     */
    public boolean stockageDistributeur(Palet.Couleur couleur, ESide side) throws RefreshPathFindingException {
        IRobotSide service = sideServices.get(side);

        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        if (!robotStatus.getCarousel().has(null)) {
            log.warn("Pas de place dans le carousel");
            return false;
        }

        working.put(side, true);

        service.ascenseurDistributeur();
        service.pivotVentouseFacade();
        servosService.waitAscenseurAndPivotVentouse();

        trajectoryManager.avanceMM(DISTANCE_DISTRIBUTEUR);

        service.enablePompeAVide();

        if (!tentativeAspirationFacade(NB_TENTATIVES_ASPIRATION, service)) {
            log.warn("Impossible d'aspirer le palet");
            trajectoryManager.reculeMM(DISTANCE_DISTRIBUTEUR);
            service.disablePompeAVide();
            service.ascenseurAndVentouseHome();
            working.put(side, false);
            return false;
        }

        trajectoryManager.reculeMM(DISTANCE_DISTRIBUTEUR);

        boolean ok = stockage(couleur, side);

        working.put(side, false);

        return ok;
    }

    private boolean stockage(Palet.Couleur couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (!carouselService.tourner(service.positionCarouselPince(), (Palet.Couleur) null)) {
            log.warn("Echec du carousel, pourtant il y avait une place ?");
            return false;
        }

        service.porteBarilletOuvert();
        service.ascenseurCarousel();
        service.pivotVentouseCarousel();
        servosService.waitAscenseurAndPivotVentouse();

        service.porteBarilletFerme();
        servosService.waitPorteBarillet();

        service.disablePompeAVide();
        service.ascenseurAndVentouseHome();

        robotStatus.getCarousel().store(service.positionCarouselPince(), new Palet().couleur(couleur));
        carouselService.lectureCouleur(service.positionCarouselPince());

        return true;
    }

    /**
     * Violet, côté droit | jaune, côté gauche
     * ATTENTION déplacement intégré
     */
    public boolean stockageGoldenium(ESide side) throws RefreshPathFindingException {
        IRobotSide service = sideServices.get(side);

        if (robotStatus.getGoldeniumInPince() != null) {
            log.warn("Le goldenium est déjà prit");
            return false;
        }

        if (isWorking(side)) {
            log.warn("Pince déjà utilisée");
            return false;
        }

        working.put(side, true);

        service.ascenseurAccelerateur();
        service.pivotVentouseFacade();
        servosService.waitAscenseurAndPivotVentouse();

        trajectoryManager.avanceMM(DISTANCE_DISTRIBUTEUR);

        service.enablePompeAVide();

        if (!tentativeAspirationFacade(NB_TENTATIVES_ASPIRATION, service)) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            working.put(side, false);
            trajectoryManager.reculeMM(DISTANCE_DISTRIBUTEUR);
            return false;
        }

        robotStatus.setGoldeniumInPince(side);
        robotStatus.setGoldeniumPrit(true);

        working.put(side, true);

        trajectoryManager.reculeMM(DISTANCE_DISTRIBUTEUR);

        return false;
    }

    /**
     * Mise en place pour dépose, à faire avant d'avancer
     */
    public void prepareDeposeAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.ascenseurAccelerateur();
        service.pivotVentouseFacade();
        service.pousseAccelerateurStandby();
        servosService.waitAscenseurAndPivotVentouse();
    }

    public void pousseAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurAction();
        servosService.waitPousseAccelerateur();

        service.pousseAccelerateurStandby();
        servosService.waitPousseAccelerateur();
    }

    /**
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    public boolean deposeAccelerateur(Palet.Couleur couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

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

        working.put(side, true);

        carouselService.tourner(service.positionCarouselPince(), couleur);

        Palet.Couleur couleurFinale = robotStatus.getCarousel().get(service.positionCarouselPince()).couleur();

        service.ascenseurCarousel();
        service.pivotVentouseCarousel();
        servosService.waitAscenseurAndPivotVentouse();

        service.enablePompeAVide();

        if (!tentativeAspirationCarousel(NB_TENTATIVES_ASPIRATION, service)) {
            log.warn("Impossible d'aspirer le palet");
            service.disablePompeAVide();
            working.put(side, false);
            return false;
        }

        service.porteBarilletOuvert();
        servosService.waitPorteBarillet();

        service.pivotPinceSortieCarousel();
        servosService.waitPivotVentouse();

        service.ascenseurPreAccelerateur();
        service.pivotVentouseFacade();
        servosService.waitAscenseurVentouse();

        service.ascenseurAccelerateur();
        servosService.waitAscenseurVentouse();

        service.porteBarilletFerme();

        robotStatus.getCarousel().unstore(service.positionCarouselPince());

        service.disablePompeAVide();

        pousseAccelerateur(side);

        robotStatus.getPaletsInAccelerateur().add(couleurFinale);

        working.put(side, false);

        return true;
    }

    /**
     * Fin de dépose dans l'accelerateur
     */
    public void finDeposeAccelerateur(ESide side) {
        IRobotSide service = sideServices.get(side);

        service.pousseAccelerateurFerme();
        service.ascenseurAndVentouseHome();
        servosService.waitAscenseurAndPivotVentouse();
    }

    /**
     * Balance violette, côté droit | balance jaune, côté gauche
     */
    public boolean deposeBalance(Palet.Couleur couleur, ESide side) throws RefreshPathFindingException {
        IRobotSide service = sideServices.get(side);

        if (robotStatus.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax) {
            log.warn("L'accelerateur est plein");
            return false;
        }

        if (couleur == Palet.Couleur.GOLD) {
            if (robotStatus.getGoldeniumInPince() != side) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne pince");
                return false;
            }

            working.put(side, true);

            service.ascenseurAccelerateur();
            servosService.waitAscenseurVentouse();

            trajectoryManager.avanceMM(DISTANCE_BALANCE);

            service.disablePompeAVide();

            trajectoryManager.reculeMM(DISTANCE_BALANCE);

            robotStatus.setGoldeniumInPince(null);
            robotStatus.getPaletsInBalance().add(couleur);

            working.put(side, false);

        } else {
            if (isWorking(side)) {
                log.warn("Pince déjà utilisée");
                return false;
            }

            if (!robotStatus.getCarousel().has(couleur)) {
                log.warn("Le carousel est vide");
                return false;
            }

            working.put(side, true);

            carouselService.tourner(service.positionCarouselPince(), couleur);

            Palet.Couleur couleurFinale = robotStatus.getCarousel().get(service.positionCarouselPince()).couleur();

            service.ascenseurCarousel();
            service.pivotVentouseCarousel();
            servosService.waitAscenseurAndPivotVentouse();

            service.enablePompeAVide();

            if (!tentativeAspirationCarousel(NB_TENTATIVES_ASPIRATION, service)) {
                log.warn("Impossible d'aspirer le palet");
                service.disablePompeAVide();
                working.put(side, false);
                return false;
            }

            service.porteBarilletOuvert();
            servosService.waitPorteBarillet();

            service.ascenseurAccelerateur();
            service.pivotVentouseFacade();
            servosService.waitAscenseurAndPivotVentouse();

            trajectoryManager.avanceMM(DISTANCE_BALANCE);

            service.porteBarilletFerme();

            robotStatus.getCarousel().unstore(service.positionCarouselPince());

            service.disablePompeAVide();

            trajectoryManager.reculeMM(DISTANCE_BALANCE);

            robotStatus.getPaletsInBalance().add(couleurFinale);

            working.put(side, false);
        }

        return true;
    }

    /**
     * Depose sur la table
     * Géré uniquement pour le goldenium pur le moment
     */
    public boolean deposeTable(Palet.Couleur couleur, ESide side) {
        IRobotSide service = sideServices.get(side);

        if (couleur == Palet.Couleur.GOLD) {
            if (robotStatus.getGoldeniumInPince() != side) {
                log.warn("On a pas le goldenium, ou il n'est pas dans la bonne pince");
                return false;
            }

            service.pinceSerrageOuvert();

            service.pivotVentouseTable();
            servosService.waitPivotVentouse();

            service.ascenseurTableGold();
            servosService.waitAscenseurVentouse();

            service.disablePompeAVide();

            service.ascenseurAccelerateur();

            robotStatus.setGoldeniumInPince(null);
            robotStatus.getPaletsInTableauBleu().add(Palet.Couleur.GOLD);

        } else {
            log.warn("Dépose sur la table non géré");
            return false;
        }

        return true;
    }

    private boolean tentativeAspirationFacade(int nb, IRobotSide side) throws RefreshPathFindingException {
        long remaining = TEMPS_TENTATIVE_ASPIRATION;
        while (!side.paletPrisDansVentouse() && remaining > 0) {
            remaining -= 100;
            ThreadUtils.sleep(100);
        }

        if (!side.paletPrisDansVentouse()) {
            if (nb > 0) {
                trajectoryManager.reculeMM(DISTANCE_DISTRIBUTEUR);
                trajectoryManager.avanceMM(DISTANCE_DISTRIBUTEUR);

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
