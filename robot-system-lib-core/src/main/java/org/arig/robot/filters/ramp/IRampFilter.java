package org.arig.robot.filters.ramp;

import java.util.concurrent.TimeUnit;

/**
 * The Interface IRampFilter.
 * 
 * @author mythril
 */
public interface IRampFilter {

    /**
     * Sets the sample time in ms.
     * 
     * @param value the new sample time ms
     */
    public void setSampleTime(final double value);

    /**
     * Sets the sample time with a specific unit.
     *
     * @param value
     * @param unit
     */
    public void setSampleTime(final double value, TimeUnit unit);

    /**
     * Sets the ramp acc.
     * 
     * @param value
     *            the new ramp acc
     */
    public void setRampAcc(final double value);

    /**
     * Sets the ramp dec.
     * 
     * @param value
     *            the new ramp dec
     */
    public void setRampDec(final double value);

    /**
     * Reset.
     */
    public void reset();

    /**
     * Filter.
     * 
     * @param vitesse
     *            the vitesse
     * @param consigne
     *            the consigne
     * @param mesure
     *            the mesure
     * @param frein
     *            the frein
     * @return the double
     */
    public double filter(final double vitesse, final double consigne,
            final double mesure, final boolean frein);
}
