package org.arig.robot.system.capteurs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.CouleurCarreFouille;
import org.arig.robot.model.CouleurEchantillon;

@Slf4j
@RequiredArgsConstructor
public class CarreFouilleReader {

    private final I2CManager i2cManager;
    private final String deviceName;

    private static final byte CMD_STATE_VENTOUSE = 'V';
    private static final byte CMD_STATE_STOCK = 'S';

    private enum ReadState {
        INCONNU,
        JAUNE,
        VIOLET,
        INTERDIT
    }

    public CouleurCarreFouille readCarreFouille() throws I2CException {
        final byte dataRead;
        try {
            dataRead = i2cManager.getData(deviceName);
        } catch (I2CException e) {
            String message = String.format("Impossible de lire la valeur du carre de fouille pour la carte %s", deviceName);
            log.error(message);
            throw new I2CException(message, e);
        }

        final ReadState readState = ReadState.values()[dataRead];
        final CouleurCarreFouille couleurCarreFouille;
        switch (readState) {
            case JAUNE: couleurCarreFouille = CouleurCarreFouille.JAUNE;break;
            case VIOLET: couleurCarreFouille = CouleurCarreFouille.VIOLET; break;
            case INTERDIT: couleurCarreFouille = CouleurCarreFouille.INTERDIT; break;
            default: couleurCarreFouille = CouleurCarreFouille.INCONNU;
        }
        log.info("Carre de fouille {} : {} -> {}", deviceName, readState, couleurCarreFouille);
        return couleurCarreFouille;
    }

    public void printStateVentouse(CouleurEchantillon ventouseBas, CouleurEchantillon ventouseHaut) throws I2CException {
        byte valueEchantillonBas = getValueStateEchantillon(ventouseBas);
        byte valueEchantillonHaut = getValueStateEchantillon(ventouseHaut);
        log.info("Changement status ventouses : Bas {} - Haut {}", ventouseBas, ventouseHaut);
        try {
            i2cManager.sendData(deviceName, CMD_STATE_VENTOUSE, valueEchantillonBas, valueEchantillonHaut);
        } catch (I2CException e) {
            String message = String.format("Impossible d'envoyer la commande de lecture de l'état de la ventouse pour la carte %s", deviceName);
            log.error(message);
            throw new I2CException(message, e);
        }
    }

    public void printStateStock(CouleurEchantillon stock1, CouleurEchantillon stock2, CouleurEchantillon stock3, CouleurEchantillon stock4,
                                CouleurEchantillon stock5, CouleurEchantillon stock6) throws I2CException {
        byte valueStock1 = getValueStateEchantillon(stock1);
        byte valueStock2 = getValueStateEchantillon(stock2);
        byte valueStock3 = getValueStateEchantillon(stock3);
        byte valueStock4 = getValueStateEchantillon(stock4);
        byte valueStock5 = getValueStateEchantillon(stock5);
        byte valueStock6 = getValueStateEchantillon(stock6);
        log.info("Changement status stocks : 1 {} - 2 {} - 3 {} - 4 {} - 5 {} - 6 {}", stock1, stock2, stock3, stock4, stock5, stock6);
        try {
            i2cManager.sendData(deviceName, CMD_STATE_STOCK, valueStock1, valueStock2, valueStock3, valueStock4, valueStock5, valueStock6);
        } catch (I2CException e) {
            String message = String.format("Impossible d'envoyer la commande de lecture de l'état de la ventouse pour la carte %s", deviceName);
            log.error(message);
            throw new I2CException(message, e);
        }
    }

    private byte getValueStateEchantillon(CouleurEchantillon echantillon) {
        if (echantillon == null) {
            return '0';
        }
        switch (echantillon) {
            case ROUGE: return 'R';
            case BLEU: return 'B';
            case VERT: return 'G';
            case ROCHER:
            case ROCHER_ROUGE:
            case ROCHER_VERT:
            case ROCHER_BLEU:
                return '?';
            default: return '0';
        }
    }
}
