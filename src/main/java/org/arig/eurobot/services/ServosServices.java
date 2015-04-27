package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by gdepuille on 27/04/15.
 */
@Slf4j
@Service
public class ServosServices {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IOServices ioServices;

    @Autowired
    private RobotStatus robotStatus;

    public void setHome() {
        log.info("Servos en position initiale");
        servos.printVersion();
        servos.setPositionAndSpeed(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HAUT, IConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME, IConstantesServos.SPEED_TAPIS);
        servos.setPositionAndSpeed(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_FERME, IConstantesServos.SPEED_TAPIS);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_DROIT, IConstantesServos.GOBELET_DROIT_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.GOBELET_GAUCHE, IConstantesServos.GOBELET_GAUCHE_FERME, IConstantesServos.SPEED_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_DROIT, IConstantesServos.MONTE_GB_DROIT_BAS, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.MONTE_GOBELET_GAUCHE, IConstantesServos.MONTE_GB_GAUCHE_BAS, IConstantesServos.SPEED_MONTE_GOBELET);
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT, IConstantesServos.SPEED_ASCENSEUR);
        servos.setPositionAndSpeed(IConstantesServos.PINCE, IConstantesServos.PINCE_FERME, IConstantesServos.SPEED_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME, IConstantesServos.SPEED_GUIDE);
        servos.setPositionAndSpeed(IConstantesServos.SONAR, IConstantesServos.SONAR_CENTRE, IConstantesServos.SPEED_SONAR);
    }


    public void checkBtnTapis() {
        if (ioServices.btnTapis()) {
            log.info("Préparation Nerell demandé");
            servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_OUVERT);
            servos.setPosition(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_OUVERT);
            servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_OUVERT);
        } else {
            servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME);
            servos.setPosition(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_FERME);
            servos.setPosition(IConstantesServos.GUIDE, IConstantesServos.GUIDE_FERME);
        }
    }

    public void checkAscenseur() throws InterruptedException {
        if (ioServices.piedCentre()) {
            log.info("Pied au centre");
            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
            servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
            Thread.currentThread();
            Thread.sleep(800);
            servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_FERME);
            Thread.sleep(300);
            servos.setPosition(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT);
            robotStatus.incNbPied();
            Thread.sleep(1500);
        }
    }

}
