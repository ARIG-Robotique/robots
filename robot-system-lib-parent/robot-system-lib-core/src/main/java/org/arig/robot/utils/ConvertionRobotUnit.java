package org.arig.robot.utils;

import lombok.Getter;

/**
 * The Class ConvertionRobotUnit.
 * <p>
 * Cette classe permet de réalisé les changements d'unité.
 * Elle réalie les convertions pulse <-> mm et pulse <-> °
 *
 * @author gdepuille
 */
public final class ConvertionRobotUnit {

    @Getter
    private final double countPerMm;

    @Getter
    private final double countPerDegree;

    @Getter
    private final double piPulse;

    @Getter
    private final double pi2Pulse;

    public ConvertionRobotUnit(final double countPerMm, final double countPerDegree) {
        this.countPerMm = countPerMm;
        this.countPerDegree = countPerDegree;

        piPulse = degToPulse(180);
        pi2Pulse = degToPulse(360);
    }

    public double mmToPulse(final double val) {
        return val * countPerMm;
    }
    public double pulseToMm(final double val) {
        return val / countPerMm;
    }
    public double degToPulse(final double val) {
        return val * countPerDegree;
    }
    public double pulseToDeg(final double val) {
        return val / countPerDegree;
    }
    public double pulseToRad(final double val) {
        return Math.toRadians(pulseToDeg(val));
    }
    public double radToPulse(final double val) {
        return degToPulse(Math.toDegrees(val));
    }
}
