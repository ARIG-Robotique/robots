package org.arig.robot.filters.pid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class SimplePidFilter.
 *
 * @author gdepuille
 */
@Slf4j
public class SimplePidFilter extends AbstractPidFilter {

    private double kp;
    private double ki;
    private double kd;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private double setPoint, input;

    @Getter
    private double errorSum = 0;
    @Getter
    private double output = 0;
    private double lastError = 0;


    public SimplePidFilter(String name) {
        super(name);
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

}
