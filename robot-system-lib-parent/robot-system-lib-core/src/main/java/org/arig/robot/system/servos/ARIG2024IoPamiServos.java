package org.arig.robot.system.servos;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ARIG2024IoPamiServos extends AbstractServos {

    private static final byte RELEASE_REGISTER = 'D';
    private static final byte VERSION_REGISTER = 'V';
    private static final int NB_SERVOS = 2;

    @Getter
    @Accessors(fluent = true)
    private final String deviceName;

    @Autowired
    private I2CManager i2cManager;

    public ARIG2024IoPamiServos() {
        this("ARIG2024IoPamiServos");
    }

    public ARIG2024IoPamiServos(final String deviceName) {
        super(NB_SERVOS);
        this.deviceName = deviceName;
    }

    @Override
    protected void setPositionImpl(byte servoNb, int position) {
        try {
            if (log.isDebugEnabled()) {
                log.debug(String.format("Comande du servo %d (Position = %d)", servoNb, position));
            }
            i2cManager.sendData(deviceName, (byte) (0x30 + servoNb), (byte) (position & 0xFF), (byte) (position >> 8));
        } catch (I2CException e) {
            log.error("Erreur lors de la définition de la vitesse et de la position");
        }
    }

    @Override
    protected void setSpeedImpl(byte servoNb, byte speed) {
        log.warn("La vitesse n'est pas gérée par la carte ARIG2024IoPamiServos");
    }

    @Override
    protected void setPositionAndSpeedImpl(byte servoNb, int position, byte speed) {
        setPositionImpl(servoNb, position);
        setSpeedImpl(servoNb, speed);
    }

    public void releaseAsservServos() throws I2CException {
        try {
            i2cManager.sendData(deviceName, RELEASE_REGISTER);
            log.info("Libération des asservissements servos effectuée");
        } catch (I2CException e) {
            log.error("Erreur lors de la libération des asservissements des servos");
        }
    }

    @Override
    public void printVersion() throws I2CException {
        try {
            i2cManager.sendData(deviceName, VERSION_REGISTER);
            final byte[] data = i2cManager.getData(deviceName, 19); // Format : YYYY.MM.DD-hhhhhhhh
            final String version = new String(data, StandardCharsets.UTF_8);
            log.info("Carte {} version {} ({} servos)", deviceName, version, NB_SERVOS);
        } catch (I2CException e) {
            String message = "Erreur lors de la récupération de la version de la carte " + deviceName;
            log.error(message);
            throw new I2CException(message, e);
        }
    }
}
