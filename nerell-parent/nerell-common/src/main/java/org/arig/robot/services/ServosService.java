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

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        homes();
    }

    public void homes() {
        servos.setPositionAndSpeed(IConstantesServos.ASCENSEUR_AVANT, IConstantesServos.POS_ASCENSEUR_AVANT_HAUT, IConstantesServos.SPEED_ASCENSEUR_AVANT);
    }

    //*******************************************//
    //* Temporisations                          *//
    //*******************************************//

    public void waitAscenseurAvant() {
        ThreadUtils.sleep(IConstantesServos.WAIT_ASCENSEUR_AVANT);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isAscenseurAvantHaut() {
        return servos.getPosition(IConstantesServos.ASCENSEUR_AVANT) == IConstantesServos.POS_ASCENSEUR_AVANT_HAUT;
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

    public void ascenseurAvant(int position, boolean wait) {
        setPosition(IConstantesServos.ASCENSEUR_AVANT, position, wait);
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
