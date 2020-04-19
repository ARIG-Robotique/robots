package org.arig.robot.filters.pid;

import org.arig.robot.filters.IFilter;

import java.util.Map;

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

    Map<String, Double> getTunings();

    /**
     * Consigne
     *
     * @param consigne La consigne a atteindre
     */
    void consigne(Double consigne);

    /**
     * Récupérer de l'erreur sum de pid
     * @return
     */
    Double getErrorSum();
}
