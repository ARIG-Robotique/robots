package org.arig.robot.filters.pid;

import org.arig.robot.filters.Filter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The Interface IPidFilter.
 *
 * @author gdepuille
 */
public interface PidFilter extends Filter<Double, Double> {

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

    void setSampleTimeMs(double value);

    void setSampleTime(double value, TimeUnit unit);

    /**
     * Récupérer de l'erreur sum de pid
     * @return
     */
    Double getErrorSum();
}
