package org.arig.robot.system.servos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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

    @Setter
    @Accessors(fluent = true)
    private int offsetNumServos = 0;

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
        byte finalServoNb = (byte) (servoNb - offsetNumServos);
        if (!checkServo(finalServoNb)) {
            return;
        }

        if (getPosition(finalServoNb) == position) {
            return;
        }

        lastPositions.put(finalServoNb, position);
        setPositionImpl(finalServoNb, position);
    }

    /**
     * Sets the speed.
     *
     * @param servoNb the servo nb
     * @param speed   the speed
     */
    public final void setSpeed(final byte servoNb, final byte speed) {
        byte finalServoNb = (byte) (servoNb - offsetNumServos);
        if (!checkServo(finalServoNb)) {
            return;
        }

        if (getSpeed(finalServoNb) == speed) {
            return;
        }

        lastSpeed.put(finalServoNb, speed);
        setSpeedImpl(finalServoNb, speed);
    }

    /**
     * Sets the position and speed.
     *
     * @param servoNb  the servo nb
     * @param speed    the speed
     * @param position the position
     */
    public final void setPositionAndSpeed(final byte servoNb, final int position, final byte speed) {
        byte finalServoNb = (byte) (servoNb - offsetNumServos);
        if (!checkServo(finalServoNb)) {
            return;
        }

        if (getPosition(finalServoNb) == position && getSpeed(finalServoNb) == speed) {
            return;
        }

        lastPositions.put(finalServoNb, position);
        lastSpeed.put(finalServoNb, speed);
        setPositionAndSpeedImpl(finalServoNb, position, speed);
    }

    /**
     * Get the last position of servo
     *
     * @param servoNb Numero du servo
     * @return La dernière position du servo
     */
    public int getPosition(final byte servoNb) {
        byte finalServoNb = (byte) (servoNb - offsetNumServos);
        if (!checkServo(finalServoNb)) {
            return -1;
        }

        return lastPositions.get(finalServoNb);
    }

    /**
     * Get the last speed of servo
     *
     * @param servoNb Numero du servo
     * @return La dernière vitesse du servo
     */
    public int getSpeed(final byte servoNb) {
        byte finalServoNb = (byte) (servoNb - offsetNumServos);
        if (!checkServo(finalServoNb)) {
            return -1;
        }

        return lastSpeed.get(finalServoNb);
    }

    /**
     * Check servo.
     *
     * @param servoNb the servo nb
     * @return true, if servo number are between 1 and 21. False otherwise
     */
    private boolean checkServo(final byte servoNb) {
        byte finalServoNb = (byte) (servoNb - offsetNumServos);
        final boolean result = finalServoNb >= 1 && finalServoNb <= nbServos;
        if (!result) {
            log.warn("Numéro de servo moteur invalide : {} (real check {})", finalServoNb, servoNb);
        }
        return result;
    }
}
