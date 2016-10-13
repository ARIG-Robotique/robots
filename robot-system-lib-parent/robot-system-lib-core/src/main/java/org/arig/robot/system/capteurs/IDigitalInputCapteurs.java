package org.arig.robot.system.capteurs;

import java.util.List;

/**
 * The Interface ICapteurs.
 *
 * @author gdepuille
 */
public interface IDigitalInputCapteurs<P> {

    /**
     * Sets the pin for capteur.
     *
     * @param capteurId the capteur id
     * @param pin       the pin
     */
    void setInputPinForCapteur(final int capteurId, final P pin);

    /**
     * Sets the pin for capteur.
     *
     * @param capteurId the capteur id
     * @param pin       the pin
     * @param reverse   the reverse
     */
    void setInputPinForCapteur(final int capteurId, final P pin, final boolean reverse);

    /**
     * Sets the pin for capteur.
     *
     * @param capteurId the capteur id
     * @param pin       the pin
     * @param reverse   the reverse
     * @param pullUp    the pull up
     */
    void setInputPinForCapteur(final int capteurId, final P pin, final boolean reverse, final boolean pullUp);

    /**
     * Read capteur value.
     *
     * @param capteurId the capteur id
     *
     * @return true, if successful
     */
    boolean readCapteurValue(final int capteurId) throws IllegalArgumentException;

    /**
     * Renvoi la liste des id enregistré
     *
     * @return Liste des identifiant des capteurs enregistré
     */
    List<Integer> getIds();
}
