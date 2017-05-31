package org.arig.robot.filters.ramp;

import java.util.concurrent.TimeUnit;

/**
 * The Interface IRampFilter.
 *
 * @author gdepuille
 */
public interface IRampFilter {

    /**
     * Sets the sample time in ms.
     *
     * @param value the new sample time ms
     */
    void setSampleTime(final double value);

    /**
     * Sets the sample time with a specific unit.
     *
     * @param value the value
     * @param unit value unit
     */
    void setSampleTime(final double value, TimeUnit unit);

    double getSampleTimeS();

    /**
     * Sets the ramp acc.
     *
     * @param value the new ramp acc
     */
    void setRampAcc(final double value);

    /**
     * Sets the ramp dec.
     *
     * @param value the new ramp dec
     */
    void setRampDec(final double value);

    /**
     * Reset.
     */
    void reset();

    /**
     * Filter.
     *
     * @param vitesseDemande   the vitesse
     * @param distanceRestante the consigne
     * @param frein            the frein
     *
     * @return the double
     */
    double filter(final double vitesseDemande, final double distanceRestante, final boolean frein);

    /**
     * Filter.
     *
     * @param vitesseDemande   the vitesse
     * @param distanceRestante the consigne
     * @param frein            the frein
     * @param bypass           the bypass
     *
     * @return the double
     */
    double filter(final double vitesseDemande, final double distanceRestante, final boolean frein, final boolean bypass);
}
