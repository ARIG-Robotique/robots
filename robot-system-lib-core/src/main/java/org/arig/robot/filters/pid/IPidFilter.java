package org.arig.robot.filters.pid;

/**
 * The Interface IPidFilter.
 * 
 * @author mythril
 */
public interface IPidFilter {

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
}
