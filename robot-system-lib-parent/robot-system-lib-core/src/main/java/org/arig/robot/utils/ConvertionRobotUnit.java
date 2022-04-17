package org.arig.robot.utils;

import lombok.Getter;

/**
 * Cette classe permet de réalisé les changements d'unité.
 * Elle réalie les convertions pulse vers mm et pulse vers °
 */
public final class ConvertionRobotUnit {

    @Getter
    private double countPerMm;

    @Getter
    private double countPerDegree;

    @Getter
    private double piPulse;

    @Getter
    private double pi2Pulse;

    public ConvertionRobotUnit(final double countPerMm, final double countPerDegree) {
        setCoefs(countPerMm, countPerDegree);
    }

    public void setCoefs(final double countPerMm, final double countPerDegree) {
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
