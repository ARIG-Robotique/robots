package org.arig.robot.filters;

/**
 * The Interface IRampFilter.
 * 
 * @author mythril
 */
public interface IRampFilter {

    /**
     * Sets the sample time ms.
     * 
     * @param value
     *            the new sample time ms
     */
    public void setSampleTimeMs(final double value);

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
