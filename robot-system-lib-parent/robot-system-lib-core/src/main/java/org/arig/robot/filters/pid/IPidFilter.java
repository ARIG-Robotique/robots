package org.arig.robot.filters.pid;

/**
 * The Interface IPidFilter.
 *
 * @author gdepuille
 */
public interface IPidFilter {

    enum PidMode {
        AUTOMATIC,
        MANUEL
    }

    enum PidType {
        DIRECT,
        REVERSE
    }

    /**
     * Sets the tunings.
     *
     * @param kp the kp
     * @param ki the ki
     * @param kd the kd
     */
    void setTunings(final double kp, final double ki, final double kd);

    double getKp();
    double getKi();
    double getKd();

    /**
     * Reset.
     */
    void reset();

    /**
     * Compute.
     *
     * @param consigne the consigne
     * @param mesure   the mesure
     *
     * @return the double
     */
    double compute(final double consigne, final double mesure);

    /**
     * Valeur de retour mesuré par rapport à la sortie
     *
     * @return la mesure d'entrée
     */
    double getMesure();

    /**
     * Valeur de la consigne
     *
     * @return la consigne
     */
    double getConsigne();

    /**
     * Sortie en fonction de la consigne, de la mesure et de la configuration
     *
     * @return la valeur de sortie du PID
     */
    double getOutput();

    /**
     * Accesseur sur la somme de l'erreur
     *
     * @return la somme de l'erreur
     */
    double getErrorSum();

    /**
     * Accesseur sur la dernière valeur de l'erreur
     *
     * @return La valeur instantané de l'erreur
     */
    double getError();

}
