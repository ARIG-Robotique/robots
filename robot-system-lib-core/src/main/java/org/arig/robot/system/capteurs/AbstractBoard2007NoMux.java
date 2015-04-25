package org.arig.robot.system.capteurs;

import com.google.common.base.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class AbstractBoard2007NoMux.
 * 
 * @param <P>
 *            the generic type
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractBoard2007NoMux<P> implements IDigitalInputCapteurs<P> {

    /** The Constant NB_CAPTEUR. */
    protected static final int NB_CAPTEUR = 23;

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum CapteursDefinition {

        GP2D_01(0x00, "Capteur GP2D 1"), // 0
        GP2D_02(0x01, "Capteur GP2D 2"),
        GP2D_03(0x02, "Capteur GP2D 3"),
        GP2D_04(0x03, "Capteur GP2D 4"),
        GP2D_05(0x04, "Capteur GP2D 5"),
        GP2D_06(0x05, "Capteur GP2D 6"),
        GP2D_07(0x06, "Capteur GP2D 7"),
        GP2D_08(0x07, "Capteur GP2D 8"),
        GP2D_09(0x08, "Capteur GP2D 9"),
        GP2D_10(0x09, "Capteur GP2D 10"),

        SWITCH_01(0x0A, "Capteur micro switch 1"),
        SWITCH_02(0x0B, "Capteur micro switch 2"),
        SWITCH_03(0x0C, "Capteur micro switch 3"),
        SWITCH_04(0x0D, "Capteur micro switch 4"),
        SWITCH_05(0x0E, "Capteur micro switch 5"),
        SWITCH_06(0x0F, "Capteur micro switch 6"),

        LUM_01(0x10, "Capteur barriere lumineuse 1"),
        LUM_02(0x11, "Capteur barriere lumineuse 2"),
        LUM_03(0x12, "Capteur barriere lumineuse 3"),
        LUM_04(0x13, "Capteur barriere lumineuse 4"),

        IND_01(0x14, "Capteur inductid 1"),
        IND_02(0x15, "Capteur inductif 2"),

        EQUIPE(0x16, "Capteur couleur équipe"); // 22

        @Getter
        private final Integer id;

        @Getter
        private final String description;

    }

    private Function<Integer, CapteursDefinition> convertCapteurId = (input) -> {
        for (CapteursDefinition c : CapteursDefinition.values()) {
            if (c.getId().equals(input)) {
                return c;
            }
        }

        // Fallback
        return null;
    };

    /** The capteur pins. */
    protected final Map<Integer, P> capteurPins = new HashMap<>();

    /** The capteur reverse. */
    protected final Map<Integer, Boolean> capteurReverse = new HashMap<>();

    /**
     * Instantiates a new board2007 no mux.
     */
    public AbstractBoard2007NoMux() {
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
    public void setInputPinForCapteur(final int capteurId, final P pin) {
        setInputPinForCapteur(capteurId, pin, false, false);
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
    public void setInputPinForCapteur(final int capteurId, final P pin, final boolean reverse) {
        setInputPinForCapteur(capteurId, pin, reverse, false);
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
    public void setInputPinForCapteur(final int capteurId, final P pin, final boolean reverse, final boolean pullUp) {
        if (check(capteurId)) {
            registerInputCapteur(capteurId, pin, pullUp);
            capteurPins.put(capteurId, pin);
            capteurReverse.put(capteurId, reverse);

            log.info("Configuration capteur {} sur la pin {}", capteurId, pin.toString());
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
            boolean result = readCapteur(capteurId, capteurPins.get(capteurId));
            if (capteurReverse.get(capteurId)) {
                result = !result;
            }

            return result;
        }

        throw new IllegalArgumentException("ID Capteur invalid : " + capteurId);
    }

    @Override
    public List<Integer> getIds() {
        List<Integer> res = new ArrayList<>();
        res.addAll(capteurPins.keySet());
        return res;
    }

    public CapteursDefinition getDefinitionById(int capteurId) {
        return convertCapteurId.apply(capteurId);
    }

    /**
     * Register capteur.
     * 
     * @param pin
     *            the pin
     * @param pullUp
     *            the pull up
     */
    protected abstract void registerInputCapteur(final int capteurId, final P pin, final boolean pullUp);

    /**
     * Lecture de la valeur d'un capteur
     *
     * @param pin
     *
     * @return Valeur booléen représentative de l'état du capteur.
     */
    protected abstract boolean readCapteur(final int capteurId, final P pin);

    /**
     * Check. Contrôle que l'ID du capteur est bien dans les bornes pour eviter une erreur de lecture du tableau des
     * pins des capteurs
     * 
     * @param capteurId
     *            the capteur id
     * @return true, if successful
     */
    protected boolean check(final int capteurId) {
        return capteurId >= 0 && capteurId < AbstractBoard2007NoMux.NB_CAPTEUR;
    }
}
