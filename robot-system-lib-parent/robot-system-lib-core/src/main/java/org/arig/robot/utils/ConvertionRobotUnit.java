package org.arig.robot.utils;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Cette classe permet de réalisé les changements d'unité.
 * Elle réalie les convertions pulse vers mm et pulse vers °
 */
@Accessors(fluent = true)
public final class ConvertionRobotUnit {

    @Getter
    private double countPerMm;

    @Getter
    private double countPerDegree;

    @Getter
    private double entraxe = 0;

    @Getter
    private double piPulse;

    @Getter
    private double pi2Pulse;

    public ConvertionRobotUnit(final double countPerMm, final double countPerDegree) {
        this(countPerMm, countPerDegree, false);
    }

    public ConvertionRobotUnit(final double countPerMm, final double orientationValue, final boolean isEntraxe) {
        countPerMm(countPerMm);
        if (isEntraxe) {
            entraxe(orientationValue);
        } else {
            countPerDegree(orientationValue);
        }
    }

    public void countPerMm(final double countPerMm) {
        this.countPerMm = countPerMm;
        if (entraxe != 0) {
            this.entraxe(entraxe);
        }
    }

    public void countPerDegree(final double countPerDegree) {
        this.countPerDegree = countPerDegree;

        piPulse = degToPulse(180);
        pi2Pulse = degToPulse(360);
    }

    public void entraxe(final double entraxeMm) {
        this.entraxe = entraxeMm;
        double countPerDeg = Math.PI * entraxeMm * countPerMm / 180;
        countPerDegree(countPerDeg);
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
