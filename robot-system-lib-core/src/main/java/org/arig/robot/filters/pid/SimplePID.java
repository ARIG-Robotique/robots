package org.arig.robot.filters.pid;

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

    /** The error sum. */
    private double errorSum = 0;

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
        SimplePID.log.info(String.format("Configuration des paramètres PID ( Kp = %s ; Ki = %s ; Kd = %s )", kp, ki, kd));

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
        SimplePID.log.info("Reset des paramètres du PID");

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
        final double error = consigne - mesure;
        final double deltaError = error - lastError;
        errorSum += error;
        lastError = error;
        final double result = kp * error + ki * errorSum + kd * deltaError;

        return result;
    }
}
