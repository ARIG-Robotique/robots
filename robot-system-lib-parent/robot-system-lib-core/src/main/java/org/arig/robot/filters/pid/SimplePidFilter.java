package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class SimplePidFilter.
 *
 * @author gdepuille
 */
@Slf4j
public class SimplePidFilter extends AbstractPidFilter {

    public SimplePidFilter(String name) {
        super(name);
    }

    @Override
    protected String pidImpl() {
        return "simple";
    }

    @Override
    public void setTunings(final double kp, final double ki, final double kd) {
        log.info("Configuration des paramètres PID ( Kp = {} ; Ki = {} ; Kd = {} )", kp, ki, kd);

        propP.setGain(kp);
        propI.setGain(ki);
        propD.setGain(kd);
    }
}
