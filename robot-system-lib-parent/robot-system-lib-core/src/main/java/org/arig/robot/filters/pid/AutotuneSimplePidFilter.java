package org.arig.robot.filters.pid;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class AutotuneSimplePidFilter extends SimplePidFilter {

    public enum PidFilterType {
        PI, PD, PID
    }

    private PidFilterType filterType;

    @Getter
    @Setter
    private double noiseBand;

    @Getter
    @Setter
    private double outputStep;

    private boolean isMax = false;
    private boolean isMin = false;
    private boolean running = false;
    private double peak1;
    private double peak2;
    private int numberLoopBack;
    private int loopBackTimeMs;
    private int peakType;
    private double lastTimeMs;
    private double lastInputs[] = new double[101];
    private double peaks[] = new double[10];
    private int peakCount;
    private boolean justchanged;
    private boolean justevaled;
    private double absMax, absMin;
    private double outputStart;
    private double output;
    private double Ku, Pu;

    public AutotuneSimplePidFilter(String name, PidFilterType filterType) {
        this(name, filterType, false);
    }

    public AutotuneSimplePidFilter(String name, PidFilterType filterType, boolean integralLimit) {
        super(name, integralLimit);
        this.filterType = filterType;

        running = false;
        noiseBand = 0.5;
        outputStep = 30;
        lastTimeMs = Instant.now().toEpochMilli();
        setLoopBackSeconds(10);
    }

    @Override
    protected String pidImpl() {
        return "autotune-" + super.pidImpl();
    }

    public void setLoopBackSeconds(int value) {
        if (value < 1){
            value = 1;
        }

        if (value < 25) {
            numberLoopBack = value * 4;
            loopBackTimeMs = 250;
        } else {
            numberLoopBack = 100;
            loopBackTimeMs = value * 10;
        }
    }

    public int getLoopBackSeconds() {
        return numberLoopBack * loopBackTimeMs / 1000;
    }

    public void cancel() {
        running = false;
    }

    @Override
    protected Double filterImpl(Double input) {
        justevaled = false;
        if (peakCount > 9 && running) {
            running = false;
            finish();
        }
        long now = Instant.now().toEpochMilli();
        if (now - lastTimeMs < loopBackTimeMs) {
            return output;
        }

        lastTimeMs = now;
        double refVal = input;
        justevaled = true;
        if (!running) {
            // Initialize working variables
            peakType = 0;
            peakCount = 0;
            justchanged = false;
            absMax = refVal;
            absMin = refVal;
            consigne(refVal);
            running = true;
            outputStart = super.filterImpl(input);
            output = outputStart + outputStep;
        } else {
            if (refVal > absMax) {
                absMax = refVal;
            }
            if (refVal < absMin) {
                absMin = refVal;
            }
        }

        isMax = true;
        isMin = true;

        // id peaks
        for (int i = numberLoopBack - 1 ; i >= 0 ; i--) {
            double val = lastInputs[i];
            if (isMax) {
                isMax = refVal > val;
            }
            if (isMin) {
                isMin = refVal < val;
            }
            lastInputs[i + 1] = refVal;
        }

        lastInputs[0] = refVal;
        if (numberLoopBack < 9) {
            return output; // FIXME ???
        }

        if (isMax) {

        } else if (isMin) {

        }
        return super.filterImpl(input);
    }

    private void finish() {
        output = outputStart;

        // Tuning parameters
        Ku = 4 * (2 * outputStep) / ((absMax - absMin) * Math.PI);
        Pu = (peak1 - peak2) / 1000;
    }
}
