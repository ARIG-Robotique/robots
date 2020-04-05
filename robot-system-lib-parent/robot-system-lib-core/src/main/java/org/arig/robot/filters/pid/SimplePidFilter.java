package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;

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

}
