package org.arig.robot.system.encoders;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.csv.CsvData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Abstract2WheelsEncoders.
 * 
 * @author mythril
 */
@Slf4j
public abstract class Abstract2WheelsEncoders {

    /** Collector CSV */
    @Autowired(required = false)
    private CsvCollector csvCollector;

    /** The distance. */
    @Getter
    private double distance;

    /** The orientation. */
    @Getter
    private double orientation;

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
        final double gauche, droit;

        if (alternate) {
            gauche = lectureGauche() * coefGauche;
            droit = lectureDroit() * coefDroit;
        } else {
            droit = lectureDroit() * coefDroit;
            gauche = lectureGauche() * coefGauche;
        }
        alternate = !alternate;

        setValeursCodeurs(gauche, droit);

        if (csvCollector != null) {
            CsvData c = csvCollector.getCurrent();
            c.setCodeurGauche(gauche);
            c.setCodeurDroit(droit);
            c.setCodeurDistance(distance);
            c.setCodeurOrient(orientation);
        }
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
     * Sets the valeurs codeurs.
     * 
     * @param gauche
     *            the gauche
     * @param droit
     *            the droit
     */
    private void setValeursCodeurs(final double gauche, final double droit) {
        distance = (droit + gauche) / 2;
        orientation = droit - gauche;
    }
}
