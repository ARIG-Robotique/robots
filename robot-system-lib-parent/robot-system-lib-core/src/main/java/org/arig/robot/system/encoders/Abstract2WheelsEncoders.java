package org.arig.robot.system.encoders;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class Abstract2WheelsEncoders.
 * 
 * @author mythril
 */
@Slf4j
public abstract class Abstract2WheelsEncoders {

    /** The distance. */
    @Getter
    private double distance;

    /** The orientation. */
    @Getter
    private double orientation;

    @Getter
    private double gauche;

    @Getter
    private double droit;

    /** The coef gauche. */
    private double coefGauche;

    /** The coef droit. */
    private double coefDroit;

    /** The alternate. */
    private boolean alternate;

    /**
     * Instantiates a new abstract encoders.
     */
    protected Abstract2WheelsEncoders() {
        distance = orientation = 0;
        coefDroit = coefGauche = 1.0;
        alternate = false;
    }

    /**
     * Lecture valeurs.
     */
    public void lectureValeurs() {

        if (alternate) {
            gauche = lectureGauche() * coefGauche;
            droit = lectureDroit() * coefDroit;
        } else {
            droit = lectureDroit() * coefDroit;
            gauche = lectureGauche() * coefGauche;
        }
        alternate = !alternate;

        calculPolarValues();

//        if (csvCollector != null) {
//            CsvData c = csvCollector.getCurrent();
//            c.setCodeurGauche(gauche);
//            c.setCodeurDroit(droit);
//            c.setCodeurDistance(distance);
//            c.setCodeurOrient(orientation);
//        }
    }

    /**
     * Sets the coefs.
     * 
     * @param coefGauche
     *            the coef gauche
     * @param coefDroit
     *            the coef droit
     */
    public void setCoefs(final double coefGauche, final double coefDroit) {
        this.coefGauche = coefGauche;
        this.coefDroit = coefDroit;
    }

    /**
     * Reset.
     */
    public abstract void reset();

    /**
     * Lecture gauche.
     *
     * @return the double
     */
    protected abstract double lectureGauche();

    /**
     * Lecture droit.
     * 
     * @return the double
     */
    protected abstract double lectureDroit();

    /**
     * Calcul des valeurs polaires.
     */
    private void calculPolarValues() {
        distance = (droit + gauche) / 2;
        orientation = droit - gauche;
    }
}
