package org.arig.robot.system.capteurs;

import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.CouleurCarreFouille;

public class CarreFouilleReaderBouchon extends CarreFouilleReader {

    public CarreFouilleReaderBouchon(I2CManager i2cManager, String deviceName) {
        super(i2cManager, deviceName);
    }

    @Override
    public CouleurCarreFouille readCarreFouille() throws I2CException {
        return CouleurCarreFouille.INCONNU;
    }
}
