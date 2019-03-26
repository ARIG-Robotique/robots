package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
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
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_BARILLET, 1500, (byte) 0);

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        servos.setPositionAndSpeed(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT, IConstantesServos.SPEED_SERRAGE_PALET);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_OUVERT, IConstantesServos.SPEED_SERRAGE_PALET);
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
        servos.setPositionAndSpeed(IConstantesServos.POUSSE_ACCELERATEUR_DROIT, IConstantesServos.POUSSE_ACCELERATEUR_DROIT_STANDBY, IConstantesServos.SPEED_POUSSE_ACCELERATEUR);
        servos.setPositionAndSpeed(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE, IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_STANDBY, IConstantesServos.SPEED_POUSSE_ACCELERATEUR);
    }

    public void homes() {
        // TODO
    }

    //*******************************************//
    //* Temporisations                          *//
    //*******************************************//

    public void waitPinceSerragePalet() {
        servos.waitTime(IConstantesServos.WAIT_PINCE_SERRAGE_PALET);
    }

    public void waitPivotVentouse() {
        servos.waitTime(IConstantesServos.WAIT_PIVOT_VENTOUSE);
    }

    public void waitAscenseurVentouse() {
        servos.waitTime(IConstantesServos.WAIT_ASCENSEUR_VENTOUSE);
    }

    public void waitPorteBarillet() {
        servos.waitTime(IConstantesServos.WAIT_PORTE_BARILLET);
    }

    public void waitTrappeMagasin() {
        servos.waitTime(IConstantesServos.WAIT_TRAPPE_MAGASIN);
    }

    public void waitEjectionMagasin() {
        servos.waitTime(IConstantesServos.WAIT_EJECTION_MAGASIN);
    }

    public void waitPousseAccelerateur() {
        servos.waitTime(IConstantesServos.WAIT_POUSSE_ACCELERATEUR);
    }

    public void waitAscenseurAndPivotVentouse() {
        servos.waitTime(Math.max(IConstantesServos.WAIT_PIVOT_VENTOUSE, IConstantesServos.WAIT_ASCENSEUR_VENTOUSE));
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isPinceSerragePaletDroitOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT;
    }

    public boolean isPinceSerragePaletDroitLock() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK;
    }

    public boolean isPinceSerragePaletDroitFerme() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_FERME;
    }

    public boolean isPinceSerragePaletGaucheOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_OUVERT;
    }

    public boolean isPinceSerragePaletGaucheLock() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK;
    }

    public boolean isPinceSerragePaletGaucheFerme() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_FERME;
    }

    public boolean isPivotVentouseDroitCarousel() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT) == IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL;
    }

    public boolean isPivotVentouseDroitFacade() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT) == IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE;
    }

    public boolean isPivotVentouseDroitTable() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT) == IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE;
    }

    public boolean isPivotVentouseGaucheCarousel() {
        return servos.getPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE) == IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL;
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

    public void pinceSerragePaletDroitOuvert() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT);
    }

    public void pinceSerragePaletDroitLock() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK);
    }

    public void pinceSerragePaletDroitFerme() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_FERME);
    }

    public void pinceSerragePaletGaucheOuvert() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_OUVERT);
    }

    public void pinceSerragePaletGaucheLock() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK);
    }

    public void pinceSerragePaletGaucheFerme() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_FERME);
    }

    public void pivotVentouseDroitCarousel() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL);
    }

    public void pivotVentouseDroitFacade() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE);
    }

    public void pivotVentouseDroitTable() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE);
    }

    public void pivotVentouseGaucheCarousel() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL);
    }

    public void pivotVentouseGaucheFacade() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_FACADE);
    }

    public void pivotVentouseGaucheTable() {
        servos.setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE);
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

    public void ascenseurDroitTable() {
        servos.setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, IConstantesServos.ASCENSEUR_DROIT_TABLE);
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
