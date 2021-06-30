package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractServosService {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IIOService ioService;

    @Autowired
    private RobotConfig robotConfig;

    protected void logPositionServo(final String servoName, final String positionName, final boolean wait) {
        log.info("{} -> {}{}", servoName, positionName, wait ? " avec attente" : StringUtils.EMPTY);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void cyclePreparation() {
        log.info("Servos en position initiale");
        servos.printVersion();

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        homes();
    }

    public abstract void homes();

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    protected void setPosition(byte servo, int position, boolean wait) {
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

    protected void setPositionAndSpeed(byte servo, int position, byte speed, boolean wait) {
        if (!wait) {
            servos.setPositionAndSpeed(servo, position, speed);
        } else {
            int currentPosition = servos.getPosition(servo);
            if (currentPosition != position) {
                servos.setPositionAndSpeed(servo, position, speed);
                ThreadUtils.sleep(computeWaitTime(servo, currentPosition, position));
            }
        }
    }

    protected void setPositionBatch(byte groupId, byte posId, boolean wait) {
        int[][] servosPos = robotConfig.servosBatch().get(groupId).get(posId);
        int currentPosition = servos.getPosition((byte) servosPos[0][0]);

        for (int[] servoPos : servosPos) {
            servos.setPosition((byte) servoPos[0], servoPos[1]);
        }

        if (wait && currentPosition != servosPos[0][1]) {
            ThreadUtils.sleep(computeWaitTime((byte) servosPos[0][0], currentPosition, servosPos[0][1]));
        }
    }

    protected void setPositionBatchAndSpeed(byte groupId, byte posId, byte speed, boolean wait) {
        int[][] servosPos = robotConfig.servosBatch().get(groupId).get(posId);
        int currentPosition = servos.getPosition((byte) servosPos[0][0]);

        for (int[] servoPos : servosPos) {
            servos.setPositionAndSpeed((byte) servoPos[0], servoPos[1], speed);
        }

        if (wait && currentPosition != servosPos[0][1]) {
            ThreadUtils.sleep(computeWaitTime((byte) servosPos[0][0], currentPosition, servosPos[0][1]));
        }
    }

    protected void setSinglePositionBatch(byte groupId, byte posId, int index, boolean wait) {
        int[] conf = robotConfig.servosBatch().get(groupId).get(posId)[index];
        setPosition((byte) conf[0], conf[1], wait);
    }

    protected int computeWaitTime(byte servo, int currentPosition, int position) {
        Triple<Integer, Integer, Integer> conf = robotConfig.servosMinTimeMax().get(servo);

        int min = conf.getLeft();
        int time = conf.getMiddle();
        int max = conf.getRight();

        // au cas ou la constante n'a pas été triée
        if (max < min) {
            min = conf.getRight();
            max = conf.getLeft();
        }

        double wait = time * Math.abs(position - currentPosition) / (max * 1. - min);
        return (int) Math.round(wait);
    }

    //*******************************************//
    //* Autres                                  *//
    //*******************************************//

    @Deprecated
    public void controlBatteryVolts() {
        final double tension = getTension();
        if (tension < robotConfig.seuilAlimentationServos() && tension > 0) {
            log.warn("La tension de la carte sd21 est en dessous du seuil : {} < {}", tension, robotConfig.seuilAlimentationServos());
            ioService.disableAlim12VPuissance();
            ioService.disableAlim5VPuissance();
        }
    }

    @Deprecated
    public double getTension() {
        return servos.getTension();
    }
}
