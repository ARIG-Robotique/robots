package org.arig.robot.filters.pid;

import org.influxdb.dto.Point;

/**
 * The Interface IPidFilter.
 * 
 * @author mythril
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
     * @param kp
     *            the kp
     * @param ki
     *            the ki
     * @param kd
     *            the kd
     */
    void setTunings(final double kp, final double ki, final double kd);

    /**
     * Reset.
     */
    void reset();

    /**
     * Compute.
     * 
     * @param consigne
     *            the consigne
     * @param mesure
     *            the mesure
     * @return the double
     */
    double compute(final double consigne, final double mesure);

    /**
     * Valeur de retour mesuré par rapport à la sortie
     *
     * @return la mesure d'entrée
     */
    double getInput();

    /**
     * Valeur de la consigne
     *
     * @return la consigne
     */
    double getSetPoint();

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
