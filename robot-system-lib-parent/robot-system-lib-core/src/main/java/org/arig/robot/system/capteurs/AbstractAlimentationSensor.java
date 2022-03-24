package org.arig.robot.system.capteurs;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.capteurs.AlimentationSensorValue;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class AbstractAlimentationSensor implements IAlimentationSensor {

    // Command pour la récupération de la version de la carte
    private static final byte COMMAND_GET_VERSION = 'v';

    // Commande pour la récupération des convertion Analogique / Numérique
    private static final byte COMMAND_GET_DATA = 'g';

    @Autowired
    protected I2CManager i2cManager;

    protected final String deviceName;

    protected final AlimentationSensorValue[] alimentations;

    protected AbstractAlimentationSensor(String deviceName, int nbChannels) {
        if (nbChannels > 8) {
            throw new IllegalArgumentException("Le nombre de canaux doit être inférieur ou égal à 8");
        }

        this.deviceName = deviceName;
        alimentations = new AlimentationSensorValue[nbChannels];
        for (int i = 0 ; i < nbChannels ; i++) {
            alimentations[i] = new AlimentationSensorValue();
        }
    }

    public void printVersion() throws I2CException {
        try {
            i2cManager.sendData(deviceName, COMMAND_GET_VERSION);
            final byte[] data = i2cManager.getData(deviceName, 10); // Format : YYYY.MM.DD
            final String version = new String(data, StandardCharsets.UTF_8);
            log.info("Carte {} version {}, {} channels", deviceName, version, alimentations.length);
        } catch (I2CException e) {
            String message = "Erreur lors de la récupération de la version de la carte " + deviceName;
            log.error(message);
            throw new I2CException(message, e);
        }
    }

    public AlimentationSensorValue get(byte channel) {
        if (channel < 1 || channel > alimentations.length + 1) {
            throw new IllegalArgumentException("Le canal doit être compris entre 1 et " + (alimentations.length));
        }
        return alimentations[channel - 1];
    }

    protected abstract void getData() throws I2CException;

    public void refresh() throws I2CException {
        if (log.isDebugEnabled()) {
            log.debug("Lecture des valeurs d'alimentation");
        }
        try {
            i2cManager.sendData(deviceName, COMMAND_GET_DATA);
        } catch (I2CException e) {
            String message = "Impossible de faire la lecture des valeurs de la carte " + deviceName;
            log.error(message);
            throw new I2CException(message, e);
        }

        getData();
    }
}
