package org.arig.robot.filters.pid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class SimplePID.
 * 
 * @author mythril
 */
@Slf4j
public class SimplePID implements IPidFilter {

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

    /**
     * Instantiates a new arig pid.
     */
    public SimplePID() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.filters.pid.IPidFilter#setTunings(double, double, double)
     */
    @Override
    public void setTunings(final double kp, final double ki, final double kd) {
        log.info("Configuration des paramètres PID ( Kp = {} ; Ki = {} ; Kd = {} )", kp, ki, kd);

        this.kp = kp;
        this.kd = kd;
        this.ki = ki;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.filters.pid.IPidFilter#reset()
     */
    @Override
    public void reset() {
        log.info("Reset des paramètres du PID");

        errorSum = 0;
        lastError = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.filters.pid.IPidFilter#compute(double, double)
     */
    @Override
    public double compute(final double consigne, final double mesure) {
        setSetPoint(consigne);
        setInput(mesure);

        final double error = consigne - mesure;
        final double deltaError = error - lastError;
        errorSum += error;
        lastError = error;
        output = kp * error + ki * errorSum + kd * deltaError;
        return output;
    }

    @Override
    public double getError() {
        return lastError;
    }
}
