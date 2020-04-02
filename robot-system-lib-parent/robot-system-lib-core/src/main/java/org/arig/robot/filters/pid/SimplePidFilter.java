package org.arig.robot.filters.pid;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * The Class SimplePidFilter.
 *
 * @author gdepuille
 */
@Slf4j
public class SimplePidFilter extends AbstractPidFilter {

    public SimplePidFilter(String name) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public SimplePidFilter(String name, double min, double max) {
        super(name, min, max);
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

    @Override
    public Map<String, Double> getTunings() {
        return ImmutableMap.of(
          "kp", propP.getGain(),
          "ki", propI.getGain(),
          "kd", propD.getGain()
        );
    }
}
