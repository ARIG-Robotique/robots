package org.arig.robot.system.capteurs;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class Board2007NoMux.
 * 
 * @param <P>
 *            the generic type
 * 
 * @author mythril
 */
@Slf4j
public abstract class Board2007NoMux<P> implements IDigitalCapteurs<P> {

    /** The Constant NB_CAPTEUR. */
    protected static final int NB_CAPTEUR = 23;

    /** The Constant GP2D_01. */
    public static final int GP2D_01 = 0x00; // 0

    /** The Constant GP2D_02. */
    public static final int GP2D_02 = 0x01;

    /** The Constant GP2D_03. */
    public static final int GP2D_03 = 0x02;

    /** The Constant GP2D_04. */
    public static final int GP2D_04 = 0x03;

    /** The Constant GP2D_05. */
    public static final int GP2D_05 = 0x04;

    /** The Constant GP2D_06. */
    public static final int GP2D_06 = 0x05;

    /** The Constant GP2D_07. */
    public static final int GP2D_07 = 0x06;

    /** The Constant GP2D_08. */
    public static final int GP2D_08 = 0x07;

    /** The Constant GP2D_09. */
    public static final int GP2D_09 = 0x08;

    /** The Constant GP2D_10. */
    public static final int GP2D_10 = 0x09;

    /** The Constant SWITCH_01. */
    public static final int SWITCH_01 = 0x0A;

    /** The Constant SWITCH_02. */
    public static final int SWITCH_02 = 0x0B;

    /** The Constant SWITCH_03. */
    public static final int SWITCH_03 = 0x0C;

    /** The Constant SWITCH_04. */
    public static final int SWITCH_04 = 0x0D;

    /** The Constant SWITCH_05. */
    public static final int SWITCH_05 = 0x0E;

    /** The Constant SWITCH_06. */
    public static final int SWITCH_06 = 0x0F;

    /** The Constant LUM_01. */
    public static final int LUM_01 = 0x10;

    /** The Constant LUM_02. */
    public static final int LUM_02 = 0x11;

    /** The Constant LUM_03. */
    public static final int LUM_03 = 0x12;

    /** The Constant LUM_04. */
    public static final int LUM_04 = 0x13;

    /** The Constant IND_01. */
    public static final int IND_01 = 0x14;

    /** The Constant IND_02. */
    public static final int IND_02 = 0x15;

    /** The Constant EQUIPE. */
    public static final int EQUIPE = 0x16; // 22

    /** The capteur pins. */
    protected final Map<Integer, P> capteurPins = new HashMap<>();

    /** The capteur reverse. */
    protected final Map<Integer, Boolean> capteurReverse = new HashMap<>();

    /**
     * Instantiates a new board2007 no mux.
     */
    public Board2007NoMux() {
    }

    /**
     * Sets the pin for capteur.
     * 
     * @param capteurId
     *            the capteur id
     * @param pin
     *            the pin
     */
    @Override
    public void setPinForCapteur(final int capteurId, final P pin) {
        setPinForCapteur(capteurId, pin, false, false);
    }

    /**
     * Sets the pin for capteur.
     * 
     * @param capteurId
     *            the capteur id
     * @param pin
     *            the pin
     * @param reverse
     *            the reverse
     */
    @Override
    public void setPinForCapteur(final int capteurId, final P pin, final boolean reverse) {
        setPinForCapteur(capteurId, pin, reverse, false);
    }

    /**
     * Sets the pin for capteur.
     * 
     * @param capteurId
     *            the capteur id
     * @param pin
     *            the pin
     * @param reverse
     *            the reverse
     * @param pullUp
     *            the pull up
     */
    @Override
    public void setPinForCapteur(final int capteurId, final P pin, final boolean reverse, final boolean pullUp) {
        if (check(capteurId)) {
            registerCapteur(pin, pullUp);
            capteurPins.put(capteurId, pin);
            capteurReverse.put(capteurId, reverse);

            Board2007NoMux.log.info(String.format("Configuration capteur %s sur la pin %s", capteurId, pin.toString()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.capteurs.ICapteurs#readCapteurValue(int)
     */
    @Override
    public boolean readCapteurValue(final int capteurId) {
        if (check(capteurId) && capteurPins.containsKey(capteurId)) {
            boolean result = readCapteur(capteurPins.get(capteurId));
            if (capteurReverse.get(capteurId)) {
                result = !result;
            }

            return result;
        }

        throw new IllegalArgumentException("ID Capteur invalid : " + capteurId);
    }

    /**
     * Register capteur.
     * 
     * @param pin
     *            the pin
     * @param pullUp
     *            the pull up
     */
    protected abstract void registerCapteur(final P pin, final boolean pullUp);

    protected abstract boolean readCapteur(final P pin);

    /**
     * Check. Contrôle que l'ID du capteur est bien dans les bornes pour eviter une erreur de lecture du tableau des
     * pins des capteurs
     * 
     * @param capteurId
     *            the capteur id
     * @return true, if successful
     */
    protected boolean check(final int capteurId) {
        return capteurId >= 0 && capteurId < Board2007NoMux.NB_CAPTEUR;
    }
}
