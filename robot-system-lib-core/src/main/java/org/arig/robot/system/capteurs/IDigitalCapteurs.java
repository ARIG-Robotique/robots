package org.arig.robot.system.capteurs;

/**
 * The Interface ICapteurs.
 * 
 * @author mythril
 */
public interface IDigitalCapteurs<P> {

    /**
     * Sets the pin for capteur.
     * 
     * @param capteurId
     *            the capteur id
     * @param pin
     *            the pin
     */
    void setPinForCapteur(final int capteurId, final P pin);

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
    void setPinForCapteur(final int capteurId, final P pin, final boolean reverse);

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
    void setPinForCapteur(final int capteurId, final P pin, final boolean reverse, final boolean pullUp);

    /**
     * Read capteur value.
     * 
     * @param capteurId
     *            the capteur id
     * @return true, if successful
     */
    boolean readCapteurValue(final int capteurId) throws IllegalArgumentException;
}
