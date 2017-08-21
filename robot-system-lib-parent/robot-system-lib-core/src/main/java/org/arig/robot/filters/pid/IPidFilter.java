package org.arig.robot.filters.pid;

import org.arig.robot.filters.IFilter;

/**
 * The Interface IPidFilter.
 *
 * @author gdepuille
 */
public interface IPidFilter extends IFilter<Double, Double>{

    /**
     * Sets the tunings.
     *
     * @param kp the kp
     * @param ki the ki
     * @param kd the kd
     */
    void setTunings(double kp, double ki, double kd);

    /**
     * Consigne
     *
     * @param consigne La consigne a atteindre
     */
    void setConsigne(double consigne);
}
