package org.arig.robot.filters;

/**
 * The Class QuadRampLog.
 * 
 * /!\ EXPERIMENTAL
 * 
 * Application du filtre "logarithmique".
 * 
 * @author mythril
 */
public class QuadRampLog extends QuadRamp {

    /** The ecart precedent. */
    private double ecartPrecedent;

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.filters.QuadRamp#reset()
     */
    @Override
    public void reset() {
        super.reset();

        ecartPrecedent = 0;
    }

    /**
     * Cette méthode est appelé depuis la sub routine d'asservissement
     * 
     * FIXME : ça merde lors de la phase de décéleration.
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
    @Override
    public double filter(final double vitesse, final double consigne,
            final double mesure, final boolean frein) {
        // Récupération de la version normal et ajout de l'écart précedent
        final double ecartTheorique = super.filter(vitesse, consigne, mesure,
                frein) + ecartPrecedent;
        ecartPrecedent = ecartTheorique - mesure;

        /*
         * TODO : Logger pour le CSV #ifdef DEBUG_MODE
         * //Serial.print(";FOutLog");Serial.print(ecartTheorique); #endif
         */
        return ecartTheorique;
    }
}
