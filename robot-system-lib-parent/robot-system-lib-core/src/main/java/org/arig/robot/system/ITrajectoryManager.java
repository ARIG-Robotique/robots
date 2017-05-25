package org.arig.robot.system;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;

/**
 * Created by dsorel on 22/05/17.
 */
public interface ITrajectoryManager {
    void init();

    void resetEncodeurs();

    void stop();

    void process();

    void pathTo(double x, double y) throws NoPathFoundException, AvoidingException;

    void pathTo(double x, double y, boolean avecArret) throws NoPathFoundException, AvoidingException;

    void gotoPointMM(double x, double y) throws RefreshPathFindingException;

    void gotoPointMM(double x, double y, boolean avecArret, boolean disableMonitor) throws RefreshPathFindingException;

    void gotoOrientationDeg(double angle) throws RefreshPathFindingException;

    void gotoOrientationDeg(double angle, SensRotation sensRotation) throws RefreshPathFindingException;

    void alignFrontTo(double x, double y) throws RefreshPathFindingException;

    void alignFrontToAvecDecalage(double x, double y, double decalageDeg) throws RefreshPathFindingException;

    void alignBackTo(double x, double y) throws RefreshPathFindingException;

    void avanceMM(double distance) throws RefreshPathFindingException;

    void avanceMMSansAngle(double distance) throws RefreshPathFindingException;

    void reculeMM(double distance) throws RefreshPathFindingException;

    void reculeMMSansAngle(double distance) throws RefreshPathFindingException;

    void tourneDeg(double angle) throws RefreshPathFindingException;

    void followLine(double x1, double y1, double x2, double y2) throws RefreshPathFindingException;

    void turnAround(double x, double y, double r) throws RefreshPathFindingException;

    void setVitesse(long vDistance, long vOrientation);

    void waitMouvement() throws RefreshPathFindingException;

    boolean isTrajetAtteint();

    boolean isTrajetEnApproche();

    AbstractMonitorMouvement getCurrentMouvement();

    void obstacleFound();

    void obstacleNotFound();

    void calageBordureDone();

    void refreshPathFinding();
}
