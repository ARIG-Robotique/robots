package org.arig.robot.filters.pid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * The Class SimplePID.
 * 
 * @author mythril
 */
@Slf4j
public class SimplePID implements IPidFilter {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    /** The kp. */
    private double kp;

    /** The ki. */
    private double ki;

    /** The kd. */
    private double kd;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private double setPoint, input;

    /** The error sum. */
    @Getter
    private double errorSum = 0;

    /** The output */
    @Getter
    private double output = 0;

    /** The last error. */
    private double lastError = 0;

    private final String name;

    /**
     * Instantiates a new arig pid.
     */
    public SimplePID(String name) {
        this.name = name;
    }

    @Override
    public void setTunings(final double kp, final double ki, final double kd) {
        log.info("Configuration des paramètres PID ( Kp = {} ; Ki = {} ; Kd = {} )", kp, ki, kd);

        this.kp = kp;
        this.kd = kd;
        this.ki = ki;
    }

    @Override
    public void reset() {
        log.info("Reset des paramètres du PID");

        errorSum = 0;
        lastError = 0;
    }

    @Override
    public double compute(final double consigne, final double mesure) {
        setSetPoint(consigne);
        setInput(mesure);

        final double error = consigne - mesure;
        final double deltaError = error - lastError;
        errorSum += error;
        lastError = error;
        output = kp * error + ki * errorSum + kd * deltaError;
        sendMonitoring();
        return output;
    }

    @Override
    public double getError() {
        return lastError;
    }

    private void sendMonitoring() {
        // Construction du monitoring
        Point serie = Point.measurement(name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("setPoint", getSetPoint())
                .addField("input", getInput())
                .addField("error", getError())
                .addField("errorSum", getErrorSum())
                .addField("output", getOutput())
                .build();

        monitoringWrapper.write(serie);
    }
}
