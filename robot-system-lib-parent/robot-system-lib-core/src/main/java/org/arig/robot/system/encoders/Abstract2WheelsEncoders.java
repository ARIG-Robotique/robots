package org.arig.robot.system.encoders;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * The Class Abstract2WheelsEncoders.
 *
 * @author gdepuille
 */
@Slf4j
public abstract class Abstract2WheelsEncoders {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    /**
     * The distance.
     */
    @Getter
    private double distance;

    /**
     * The orientation.
     */
    @Getter
    private double orientation;

    @Getter
    private double gauche;

    @Getter
    private double droit;

    /**
     * The coef gauche.
     */
    private double coefGauche;

    /**
     * The coef droit.
     */
    private double coefDroit;

    /**
     * The alternate.
     */
    private boolean alternate;

    private final String name;

    /**
     * Instantiates a new abstract encoders.
     */
    protected Abstract2WheelsEncoders(final String name) {
        this.name = name;
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
        sendMonitoring();
    }

    /**
     * Sets the coefs.
     *
     * @param coefGauche the coef gauche
     * @param coefDroit  the coef droit
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

    private void sendMonitoring() {
        // Construction du monitoring
        Point serie = Point.measurement(name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("gauche", getGauche())
                .addField("droit", getDroit())
                .addField("distance", getDistance())
                .addField("orientation", getOrientation())
                .build();

        monitoringWrapper.write(serie);
    }
}
