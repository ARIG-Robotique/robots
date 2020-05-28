package org.arig.robot.filters.ramp;

import org.arig.robot.filters.IFilter;

import java.util.Map;

/**
 * The Interface IRampFilter.
 *
 * @author gdepuille
 */
public interface IRampFilter extends IFilter<Long, Long> {

    void setConsigneVitesse(double vitesse);

    void setRamps(double rampAcc, double rampDec);

    Map<String, Double> getRamps();

}
