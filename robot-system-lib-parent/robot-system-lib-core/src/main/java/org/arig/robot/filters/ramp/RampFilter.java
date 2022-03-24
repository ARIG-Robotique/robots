package org.arig.robot.filters.ramp;

import org.arig.robot.filters.Filter;

import java.util.Map;

/**
 * The Interface IRampFilter.
 *
 * @author gdepuille
 */
public interface RampFilter extends Filter<Long, Long> {

    void setConsigneVitesse(double vitesse);

    void setRamps(double rampAcc, double rampDec);

    Map<String, Double> getRamps();

}
