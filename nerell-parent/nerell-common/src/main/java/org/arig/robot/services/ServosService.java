package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.constants.IConstantesUtiles;
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

        homes();
    }

    public void homes() {
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

    //*******************************************//
    //* Temporisations                          *//
    //*******************************************//

    public void waitPinceSerragePalet() {
        ThreadUtils.sleep(IConstantesServos.WAIT_PINCE_SERRAGE_PALET);
    }

    public void waitPivotVentouse() {
        ThreadUtils.sleep(IConstantesServos.WAIT_PIVOT_VENTOUSE);
    }

    public void waitAscenseurVentouse() {
        ThreadUtils.sleep(IConstantesServos.WAIT_ASCENSEUR_VENTOUSE);
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

    public boolean isPinceSerragePaletGaucheOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS;
    }

    public boolean isPinceSerragePaletGaucheLock() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE) == IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK;
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

    private void setPosition(byte servo, int position, boolean wait) {
        if (!wait) {
            servos.setPosition(servo, position);
        } else {
            int currentPosition = servos.getPosition(servo);
            if (currentPosition != position) {
                servos.setPosition(servo, position);
                ThreadUtils.sleep(computeWaitTime(servo, currentPosition, position));
            }
        }
    }

    private int computeWaitTime(byte servo, int currentPosition, int position) {
        Triple<Integer, Integer, Integer> config = IConstantesServos.MIN_TIME_MAX.get(servo);

        int min = config.getLeft();
        int time = config.getMiddle();
        int max = config.getRight();

        double wait = time * Math.abs(position - currentPosition) / (max * 1. - min);
        return (int) Math.round(wait);
    }

    public void ascenseurDroit(int position, boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_DROIT, position, wait);
    }

    public void ascenseurGauche(int position, boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_VENTOUSE_GAUCHE, position, wait);
    }

    public void pinceSerragePaletDroit(int position, boolean wait) {
        setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, position, wait);
    }

    public void pinceSerragePaletGauche(int position, boolean wait) {
        setPosition(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, position, wait);
    }

    public void pivotVentouseDroit(int position, boolean wait) {
        setPosition(IConstantesServos.PIVOT_VENTOUSE_DROIT, position, wait);
    }

    public void pivotVentouseGauche(int position, boolean wait) {
        setPosition(IConstantesServos.PIVOT_VENTOUSE_GAUCHE, position, wait);
    }

    public void porteBarilletDroit(int position, boolean wait) {
        setPosition(IConstantesServos.PORTE_BARILLET_DROIT, position, wait);
    }

    public void porteBarilletGauche(int position, boolean wait) {
        setPosition(IConstantesServos.PORTE_BARILLET_GAUCHE, position, wait);
    }

    public void trappeMagasinDroit(int position, boolean wait) {
        setPosition(IConstantesServos.TRAPPE_MAGASIN_DROIT, position, wait);
    }

    public void trappeMagasinGauche(int position, boolean wait) {
        setPosition(IConstantesServos.TRAPPE_MAGASIN_GAUCHE, position, wait);
    }

    public void ejectionMagasinDroit(int position, boolean wait) {
        setPosition(IConstantesServos.EJECTION_MAGASIN_DROIT, position, wait);
    }

    public void ejectionMagasinGauche(int position, boolean wait) {
        setPosition(IConstantesServos.EJECTION_MAGASIN_GAUCHE, position, wait);
    }

    public void pousseAccelerateurDroit(int position, boolean wait) {
        setPosition(IConstantesServos.POUSSE_ACCELERATEUR_DROIT, position, wait);
    }

    public void pousseAccelerateurGauche(int position, boolean wait) {
        setPosition(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE, position, wait);
    }

    public void controlBatteryVolts() {
        if (robotStatus.isMatchEnabled()) {
            final double tension = getTension();
            if (tension < IConstantesUtiles.SEUIL_BATTERY_VOLTS && tension > 0) {
                log.warn("La tension de la carte sd21 a dépassé le seuil avec une valeur {}", tension);
                ioService.disableAlim12VPuissance();
                ioService.disableAlim5VPuissance();
            }
        }
    }

    public double getTension() {
        return servos.getTension();
    }

}
