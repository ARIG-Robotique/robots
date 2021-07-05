package org.arig.robot.system.encoders;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Abstract2WheelsEncoders.
 *
 * @author gdepuille
 */
@Slf4j
public abstract class Abstract2WheelsEncoders {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Getter
    private double distance;

    @Getter
    private double orientation;

    @Getter
    private double gauche;

    @Getter
    private double droit;

    @Getter
    private double coefGauche;

    @Getter
    private double coefDroit;

    private boolean alternate;
    private final String name;

    protected Abstract2WheelsEncoders(final String name) {
        this.name = name;
        distance = orientation = 0;
        coefDroit = coefGauche = 1.0;
        alternate = false;
    }

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

    public void setCoefs(final double coefGauche, final double coefDroit) {
        this.coefGauche = coefGauche;
        this.coefDroit = coefDroit;
    }

    public abstract void reset();

    protected abstract double lectureGauche();

    protected abstract double lectureDroit();

    private void calculPolarValues() {
        distance = (droit + gauche) / 2;
        orientation = droit - gauche;
    }

    private void sendMonitoring() {
        // Construction du monitoring
        MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("encodeurs")
                .addTag(MonitorTimeSerie.TAG_NAME, name)
                .addField("gauche", getGauche())
                .addField("droit", getDroit())
                .addField("distance", getDistance())
                .addField("orientation", getOrientation());

        monitoringWrapper.addTimeSeriePoint(serie);
    }
}
