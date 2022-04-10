package org.arig.robot.system.capteurs;

import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.CouleurCarreFouille;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Team;

public class CarreFouilleReaderBouchon extends CarreFouilleReader {

    private final EurobotStatus status;

    public CarreFouilleReaderBouchon(final I2CManager i2cManager, final String deviceName, final EurobotStatus status) {
        super(i2cManager, deviceName);
        this.status = status;
    }

    @Override
    public CouleurCarreFouille readCarreFouille() throws I2CException {
        if (status.team() == null) {
            return CouleurCarreFouille.INCONNU;
        }
        return status.team() == Team.JAUNE ? CouleurCarreFouille.JAUNE : CouleurCarreFouille.VIOLET;
    }
}
