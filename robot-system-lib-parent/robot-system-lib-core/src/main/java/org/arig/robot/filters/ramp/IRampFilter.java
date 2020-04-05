package org.arig.robot.filters.ramp;

import org.arig.robot.filters.IFilter;

import java.util.Map;

/**
 * The Interface IRampFilter.
 *
 * @author gdepuille
 */
public interface IRampFilter extends IFilter<Long, Long> {

    /**
     * Définition de la consigne de vitesse
     *
     * @param vitesse Vitesse de consigne
     */
    void setConsigneVitesse(double vitesse);

    void setRamps(double rampAcc, double rampDec);

    Map<String, Double> getRamps();

}
