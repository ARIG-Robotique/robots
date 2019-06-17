package org.arig.robot.communication.bouchon;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.AbstractI2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.bouchon.BouchonI2CDevice;

import java.util.Set;

/**
 * @author gdepuille on 18/12/13.
 */
@Slf4j
public class BouchonI2CManager extends AbstractI2CManager<BouchonI2CDevice> {

    /**
     * Enregistrement d'un device I2C
     *
     * @param deviceName Nom du device
     * @param address    address du device.
     */
    public void registerDevice(final String deviceName, final int address) throws I2CException {
        final BouchonI2CDevice d = new BouchonI2CDevice().address(address).name(deviceName);
        super.registerDevice(deviceName, d);
    }

    /**
     * Execute un scan afin de detecter si tous les devices enregistré sont bien présent.
     *
     * @throws I2CException
     */
    @Override
    protected void scan() throws I2CException {
        // Contrôle que les devices enregistré sont bien présent.
        log.info("Verification des devices enregistrés");
        Set<String> deviceNames = getDeviceMap().keySet();
        for (String name : deviceNames) {
            BouchonI2CDevice device = getDevice(name);
            log.info("Scan {} [OK]", device.name());
        }
    }

    @Override
    public void sendData(String deviceName, byte... datas) throws I2CException { }

    @Override
    public byte getData(String deviceName) throws I2CException {
        return 0;
    }

    @Override
    public byte[] getDatas(String deviceName, int size) throws I2CException {
        byte[] result = new byte[size];
        for (int i = 0 ; i < size ; i++) {
            result[i] = 0;
        }
        return result;
    }
}
