package org.arig.robot.system.capteurs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.CouleurCarreFouille;

@Slf4j
@RequiredArgsConstructor
public class CarreFouilleReader {

    private final I2CManager i2cManager;
    private final String deviceName;

    private enum ReadState {
        EN_L_AIR,
        INCONNU,
        JAUNE,
        VIOLET,
        INTERDIT
    }

    public CouleurCarreFouille read() throws I2CException {
        try {
            i2cManager.sendData(deviceName);
        } catch (I2CException e) {
            String message = String.format("Impossible de lire la valeur du carre de fouille pour la carte %s", deviceName);
            log.error(message);
            throw new I2CException(message, e);
        }

        final byte data = i2cManager.getData(deviceName);
        final ReadState readState = ReadState.values()[data];
        final CouleurCarreFouille couleurCarreFouille;
        switch (readState) {
            case JAUNE: couleurCarreFouille = CouleurCarreFouille.JAUNE;break;
            case VIOLET: couleurCarreFouille = CouleurCarreFouille.VIOLET; break;
            case INTERDIT: couleurCarreFouille = CouleurCarreFouille.INTERDIT; break;

            case EN_L_AIR:
            case INCONNU:
            default: couleurCarreFouille = CouleurCarreFouille.INCONNU;
        }
        log.info("Carre de fouille {} : {} -> {}", deviceName, readState, couleurCarreFouille);
        return couleurCarreFouille;
    }
}
