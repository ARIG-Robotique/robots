package org.arig.robot.system.servos;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractServos implements InitializingBean {

    @Getter
    private final int nbServos;
    private final Map<Byte, Integer> lastPositions;
    private final Map<Byte, Byte> lastSpeed;

    protected AbstractServos(int nbServos) {
        this.nbServos = nbServos;
        this.lastPositions = new HashMap<>(nbServos);
        this.lastSpeed = new HashMap<>(nbServos);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (byte i = 1; i <= nbServos; i++) {
            lastPositions.put(i, 1500);
            lastSpeed.put(i, (byte) 0);
        }
    }

    public abstract String deviceName();
    public abstract void printVersion() throws I2CException;
    protected abstract void setPositionImpl(final byte servoNb, final int position);
    protected abstract void setSpeedImpl(final byte servoNb, final byte speed);
    protected abstract void setPositionAndSpeedImpl(final byte servoNb, final int position, final byte speed);

    /**
     * Sets the position.
     *
     * @param servoNb  the servo nb
     * @param position the position
     */
    public final void setPosition(final byte servoNb, final int position) {
        if (!checkServo(servoNb)) {
            return;
        }

        if (getPosition(servoNb) == position) {
            return;
        }

        lastPositions.put(servoNb, position);
        setPositionImpl(servoNb, position);
    }

    /**
     * Sets the speed.
     *
     * @param servoNb the servo nb
     * @param speed   the speed
     */
    public final void setSpeed(final byte servoNb, final byte speed) {
        if (!checkServo(servoNb)) {
            return;
        }

        if (getSpeed(servoNb) == speed) {
            return;
        }

        lastSpeed.put(servoNb, speed);
        setSpeedImpl(servoNb, speed);
    }

    /**
     * Sets the position and speed.
     *
     * @param servoNb  the servo nb
     * @param speed    the speed
     * @param position the position
     */
    public final void setPositionAndSpeed(final byte servoNb, final int position, final byte speed) {
        if (!checkServo(servoNb)) {
            return;
        }

        if (getPosition(servoNb) == position && getSpeed(servoNb) == speed) {
            return;
        }

        lastPositions.put(servoNb, position);
        lastSpeed.put(servoNb, speed);
        setPositionAndSpeedImpl(servoNb, position, speed);
    }

    /**
     * Get the last position of servo
     *
     * @param servoNb Numero du servo
     * @return La dernière position du servo
     */
    public int getPosition(final byte servoNb) {
        if (!checkServo(servoNb)) {
            return -1;
        }

        return lastPositions.get(servoNb);
    }

    /**
     * Get the last speed of servo
     *
     * @param servoNb Numero du servo
     * @return La dernière vitesse du servo
     */
    public int getSpeed(final byte servoNb) {
        if (!checkServo(servoNb)) {
            return -1;
        }

        return lastSpeed.get(servoNb);
    }

    /**
     * Check servo.
     *
     * @param servoNb the servo nb
     * @return true, if servo number are between 1 and 21. False otherwise
     */
    private boolean checkServo(final byte servoNb) {
        final boolean result = servoNb >= 1 && servoNb <= nbServos;
        if (!result) {
            log.warn("Numéro de servo moteur invalide : {}", servoNb);
        }
        return result;
    }
}
