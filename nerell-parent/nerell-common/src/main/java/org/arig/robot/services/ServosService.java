package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 27/04/15.
 */
@Slf4j
@Service
public class ServosService {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IIOService ioService;

    @Autowired
    private RobotStatus robotStatus;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void cyclePreparation() {
        log.info("Servos en position initiale");
        servos.printVersion();

        // Moteurs
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_DROIT, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_GAUCHE, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_CAROUSEL, 1500, (byte) 0);

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        servos.setPositionAndSpeed(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_REPOS, IConstantesServos.SPEED_SERRAGE_PALET);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS, IConstantesServos.SPEED_SERRAGE_PALET);
        servos.setPositionAndSpeed(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE, IConstantesServos.SPEED_PIVOT_VENTOUSE);
        servos.setPositionAndSpeed(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE, IConstantesServos.SPEED_PIVOT_VENTOUSE);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_DISTRIBUTEUR, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_DISTRIBUTEUR, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.PORTE_BARILLET_DROIT, IConstantesServos.PORTE_BARILLET_DROIT_FERME, IConstantesServos.SPEED_PORTE_BARILLET);
        servos.setPositionAndSpeed(IConstantesServos.PORTE_BARILLET_GAUCHE, IConstantesServos.PORTE_BARILLET_GAUCHE_FERME, IConstantesServos.SPEED_PORTE_BARILLET);
        servos.setPositionAndSpeed(IConstantesServos.TRAPPE_MAGASIN_DROIT, IConstantesServos.TRAPPE_MAGASIN_DROIT_FERME, IConstantesServos.SPEED_TRAPPE_MAGASIN);
        servos.setPositionAndSpeed(IConstantesServos.TRAPPE_MAGASIN_GAUCHE, IConstantesServos.TRAPPE_MAGASIN_GAUCHE_FERME, IConstantesServos.SPEED_TRAPPE_MAGASIN);
        servos.setPositionAndSpeed(IConstantesServos.EJECTION_MAGASIN_DROIT, IConstantesServos.EJECTION_MAGASIN_DROIT_FERME, IConstantesServos.SPEED_EJECTION_MAGASIN);
        servos.setPositionAndSpeed(IConstantesServos.EJECTION_MAGASIN_GAUCHE, IConstantesServos.EJECTION_MAGASIN_GAUCHE_FERME, IConstantesServos.SPEED_EJECTION_MAGASIN);
        servos.setPositionAndSpeed(IConstantesServos.POUSSE_ACCELERATEUR_DROIT, IConstantesServos.POUSSE_ACCELERATEUR_DROIT_FERME, IConstantesServos.SPEED_POUSSE_ACCELERATEUR);
        servos.setPositionAndSpeed(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE, IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_FERME, IConstantesServos.SPEED_POUSSE_ACCELERATEUR);
    }

    public void homes() {
        // TODO
    }

    //*******************************************//
    //* Temporisations                          *//
    //*******************************************//

    public void waitPinceSerragePalet() {
        ThreadUtils.sleep(IConstantesServos.WAIT_PINCE_SERRAGE_PALET);
    }

    public void waitPivotVentouse() {
        ThreadUtils.sleep(IConstantesServos.WAIT_PIVOT_VENTOUSE);
    }

    public void waitAscenseurVentouseLong() {
        ThreadUtils.sleep(IConstantesServos.WAIT_ASCENSEUR_VENTOUSE_LONG);
    }

    public void waitAscenseurVentouseShort() {
        ThreadUtils.sleep(IConstantesServos.WAIT_ASCENSEUR_VENTOUSE_SHORT);
    }

    public void waitPorteBarillet() {
        ThreadUtils.sleep(IConstantesServos.WAIT_PORTE_BARILLET);
    }

    public void waitTrappeMagasin() {
        ThreadUtils.sleep(IConstantesServos.WAIT_TRAPPE_MAGASIN);
    }

    public void waitEjectionMagasin() {
        ThreadUtils.sleep(IConstantesServos.WAIT_EJECTION_MAGASIN);
    }

    public void waitPousseAccelerateur() {
        ThreadUtils.sleep(IConstantesServos.WAIT_POUSSE_ACCELERATEUR);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isPinceSerragePaletDroitOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_REPOS;
    }

    public boolean isPinceSerragePaletDroitLock() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK;
    }

    public boolean isPinceSerragePaletDroitFerme() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_STANDBY;
    }

    public boolean isPinceSerragePaletGaucheOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS;
    }

    public boolean isPinceSerragePaletGaucheLock() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK;
    }

    public boolean isPinceSerragePaletGaucheFerme() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_STANDBY;
    }

    public boolean isPivotVentouseDroitCarousel() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT) == IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_VERTICAL;
    }

    public boolean isPivotVentouseDroitFacade() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT) == IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE;
    }

    public boolean isPivotVentouseDroitTable() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT) == IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE;
    }

    public boolean isPivotVentouseGaucheCarousel() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE) == IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_VERTICAL;
    }

    public boolean isPivotVentouseGaucheFacade() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE) == IConstantesServos.PIVOT_VENTOUSE_GAUCHE_FACADE;
    }

    public boolean isPivotVentouseGaucheTable() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE) == IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE;
    }

    public boolean isAscenseurDroitCarousel() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT) == IConstantesServos.ASCENSEUR_DROIT_CAROUSEL;
    }

    public boolean isAscenseurDroitDistributeur() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT) == IConstantesServos.ASCENSEUR_DROIT_DISTRIBUTEUR;
    }

    public boolean isAscenseurDroitAccelerateur() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT) == IConstantesServos.ASCENSEUR_DROIT_ACCELERATEUR;
    }

    public boolean isAscenseurDroitTable() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT) == IConstantesServos.ASCENSEUR_DROIT_TABLE;
    }

    public boolean isAscenseurGaucheCarousel() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE) == IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL;
    }

    public boolean isAscenseurGaucheDistributeur() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE) == IConstantesServos.ASCENSEUR_GAUCHE_DISTRIBUTEUR;
    }

    public boolean isAscenseurGaucheAccelerateur() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE) == IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR;
    }

    public boolean isAscenseurGaucheTable() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE) == IConstantesServos.ASCENSEUR_GAUCHE_TABLE;
    }

    public boolean isPorteBarilletDroitOuvert() {
        return servos.getPosition(IConstantesServos.PORTE_BARILLET_DROIT) == IConstantesServos.PORTE_BARILLET_DROIT_OUVERT;
    }

    public boolean isPorteBarilletDroitFerme() {
        return servos.getPosition(IConstantesServos.PORTE_BARILLET_DROIT) == IConstantesServos.PORTE_BARILLET_DROIT_FERME;
    }

    public boolean isPorteBarilletGaucheOuvert() {
        return servos.getPosition(IConstantesServos.PORTE_BARILLET_GAUCHE) == IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT;
    }

    public boolean isPorteBarilletGaucheFerme() {
        return servos.getPosition(IConstantesServos.PORTE_BARILLET_GAUCHE) == IConstantesServos.PORTE_BARILLET_GAUCHE_FERME;
    }

    public boolean isTrappeMagasinDroitOuvert() {
        return servos.getPosition(IConstantesServos.TRAPPE_MAGASIN_DROIT) == IConstantesServos.TRAPPE_MAGASIN_DROIT_OUVERT;
    }

    public boolean isTrappeMagasinDroitFerme() {
        return servos.getPosition(IConstantesServos.TRAPPE_MAGASIN_DROIT) == IConstantesServos.TRAPPE_MAGASIN_DROIT_FERME;
    }

    public boolean isTrappeMagasinGaucheOuvert() {
        return servos.getPosition(IConstantesServos.TRAPPE_MAGASIN_GAUCHE) == IConstantesServos.TRAPPE_MAGASIN_GAUCHE_OUVERT;
    }

    public boolean isTrappeMagasinGaucheFerme() {
        return servos.getPosition(IConstantesServos.TRAPPE_MAGASIN_GAUCHE) == IConstantesServos.TRAPPE_MAGASIN_GAUCHE_FERME;
    }

    public boolean isEjectionMagasinDroitOuvert() {
        return servos.getPosition(IConstantesServos.EJECTION_MAGASIN_DROIT) == IConstantesServos.EJECTION_MAGASIN_DROIT_OUVERT;
    }

    public boolean isEjectionMagasinDroitFerme() {
        return servos.getPosition(IConstantesServos.EJECTION_MAGASIN_DROIT) == IConstantesServos.EJECTION_MAGASIN_DROIT_FERME;
    }

    public boolean isEjectionMagasinGaucheOuvert() {
        return servos.getPosition(IConstantesServos.EJECTION_MAGASIN_GAUCHE) == IConstantesServos.EJECTION_MAGASIN_GAUCHE_OUVERT;
    }

    public boolean isEjectionMagasinGaucheFerme() {
        return servos.getPosition(IConstantesServos.EJECTION_MAGASIN_GAUCHE) == IConstantesServos.EJECTION_MAGASIN_GAUCHE_FERME;
    }

    public boolean isPousseAccelerateurDroitAction() {
        return servos.getPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT) == IConstantesServos.POUSSE_ACCELERATEUR_DROIT_ACTION;
    }

    public boolean isPousseAccelerateurDroitStandby() {
        return servos.getPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT) == IConstantesServos.POUSSE_ACCELERATEUR_DROIT_STANDBY;
    }

    public boolean isPousseAccelerateurDroitFerme() {
        return servos.getPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT) == IConstantesServos.POUSSE_ACCELERATEUR_DROIT_FERME;
    }

    public boolean isPousseAccelerateurGaucheAction() {
        return servos.getPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE) == IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_ACTION;
    }

    public boolean isPousseAccelerateurGaucheStandby() {
        return servos.getPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE) == IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_STANDBY;
    }

    public boolean isPousseAccelerateurGaucheFerme() {
        return servos.getPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE) == IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_FERME;
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void pinceSerragePaletDroitRepos() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_REPOS);
    }

    public void pinceSerragePaletDroitLock() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK);
    }

    public void pinceSerragePaletDroitStandby() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_STANDBY);
    }

    public void pinceSerragePaletGaucheRepos() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS);
    }

    public void pinceSerragePaletGaucheLock() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK);
    }

    public void pinceSerragePaletGaucheStandby() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_STANDBY);
    }

    public void pivotVentouseDroitCarouselVertical() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_VERTICAL);
    }

    public void pivotVentouseDroitCarouselSortie() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_SORTIE);
    }

    public void pivotVentouseDroitFacade() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE);
    }

    public void pivotVentouseDroitTable() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE);
    }

    public void pivotVentouseGaucheCarouselVertical() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_VERTICAL);
    }

    public void pivotVentouseGaucheCarouselSortie() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_SORTIE);
    }

    public void pivotVentouseGaucheFacade() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_FACADE);
    }

    public void pivotVentouseGaucheTable() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE);
    }

    public void ascenseurDroitCarouselDepose() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_CAROUSEL_DEPOSE);
    }

    public void ascenseurDroitCarousel() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_CAROUSEL);
    }

    public void ascenseurDroitDistributeur() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_DISTRIBUTEUR);
    }

    public void ascenseurDroitAccelerateur() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_ACCELERATEUR);
    }

    public void ascenseurDroitTableGold() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_TABLE_GOLD);
    }

    public void ascenseurDroitTable() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_TABLE);
    }

    public void ascenseurGaucheCarouselDepose() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL_DEPOSE);
    }

    public void ascenseurGaucheCarousel() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL);
    }

    public void ascenseurGaucheDistributeur() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_DISTRIBUTEUR);
    }

    public void ascenseurGaucheAccelerateur() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR);
    }

    public void ascenseurGaucheTableGold() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_TABLE_GOLD);
    }

    public void ascenseurGaucheTable() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, IConstantesServos.ASCENSEUR_GAUCHE_TABLE);
    }

    public void porteBarilletDroitOuvert() {
        servos.setPosition(IConstantesServos.PORTE_BARILLET_DROIT, IConstantesServos.PORTE_BARILLET_DROIT_OUVERT);
    }

    public void porteBarilletDroitFerme() {
        servos.setPosition(IConstantesServos.PORTE_BARILLET_DROIT, IConstantesServos.PORTE_BARILLET_DROIT_FERME);
    }

    public void porteBarilletGaucheOuvert() {
        servos.setPosition(IConstantesServos.PORTE_BARILLET_GAUCHE, IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT);
    }

    public void porteBarilletGaucheFerme() {
        servos.setPosition(IConstantesServos.PORTE_BARILLET_GAUCHE, IConstantesServos.PORTE_BARILLET_GAUCHE_FERME);
    }

    public void trappeMagasinDroitOuvert() {
        servos.setPosition(IConstantesServos.TRAPPE_MAGASIN_DROIT, IConstantesServos.TRAPPE_MAGASIN_DROIT_OUVERT);
    }

    public void trappeMagasinDroitFerme() {
        servos.setPosition(IConstantesServos.TRAPPE_MAGASIN_DROIT, IConstantesServos.TRAPPE_MAGASIN_DROIT_FERME);
    }

    public void trappeMagasinGaucheOuvert() {
        servos.setPosition(IConstantesServos.TRAPPE_MAGASIN_GAUCHE, IConstantesServos.TRAPPE_MAGASIN_GAUCHE_OUVERT);
    }

    public void trappeMagasinGaucheFerme() {
        servos.setPosition(IConstantesServos.TRAPPE_MAGASIN_GAUCHE, IConstantesServos.TRAPPE_MAGASIN_GAUCHE_FERME);
    }

    public void ejectionMagasinDroitOuvert() {
        servos.setPosition(IConstantesServos.EJECTION_MAGASIN_DROIT, IConstantesServos.EJECTION_MAGASIN_DROIT_OUVERT);
    }

    public void ejectionMagasinDroitFerme() {
        servos.setPosition(IConstantesServos.EJECTION_MAGASIN_DROIT, IConstantesServos.EJECTION_MAGASIN_DROIT_FERME);
    }

    public void ejectionMagasinGaucheOuvert() {
        servos.setPosition(IConstantesServos.EJECTION_MAGASIN_GAUCHE, IConstantesServos.EJECTION_MAGASIN_GAUCHE_OUVERT);
    }

    public void ejectionMagasinGaucheFerme() {
        servos.setPosition(IConstantesServos.EJECTION_MAGASIN_GAUCHE, IConstantesServos.EJECTION_MAGASIN_GAUCHE_FERME);
    }

    public void pousseAccelerateurDroitAction() {
        servos.setPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT, IConstantesServos.POUSSE_ACCELERATEUR_DROIT_ACTION);
    }

    public void pousseAccelerateurDroitStandby() {
        servos.setPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT, IConstantesServos.POUSSE_ACCELERATEUR_DROIT_STANDBY);
    }

    public void pousseAccelerateurDroitFerme() {
        servos.setPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT, IConstantesServos.POUSSE_ACCELERATEUR_DROIT_FERME);
    }

    public void pousseAccelerateurGaucheAction() {
        servos.setPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE, IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_ACTION);
    }

    public void pousseAccelerateurGaucheStandby() {
        servos.setPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE, IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_STANDBY);
    }

    public void pousseAccelerateurGaucheFerme() {
        servos.setPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE, IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_FERME);
    }

}
