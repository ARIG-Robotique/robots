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

    void gotoPointMM(double x, double y) throws RefreshPathFindingException, AvoidingException;

    void gotoPointMM(double x, double y, boolean avecArret, boolean disableMonitor) throws RefreshPathFindingException, AvoidingException;

    void gotoOrientationDeg(double angle) throws RefreshPathFindingException, AvoidingException;

    void gotoOrientationDeg(double angle, SensRotation sensRotation) throws RefreshPathFindingException, AvoidingException;

    void alignFrontTo(double x, double y) throws RefreshPathFindingException, AvoidingException;

    void alignFrontToAvecDecalage(double x, double y, double decalageDeg) throws RefreshPathFindingException, AvoidingException;

    void alignBackTo(double x, double y) throws RefreshPathFindingException, AvoidingException;

    void avanceMM(double distance) throws RefreshPathFindingException, AvoidingException;

    void avanceMMSansAngle(double distance) throws RefreshPathFindingException, AvoidingException;

    void reculeMM(double distance) throws RefreshPathFindingException, AvoidingException;

    void reculeMMSansAngle(double distance) throws RefreshPathFindingException, AvoidingException;

    void tourneDeg(double angle) throws RefreshPathFindingException, AvoidingException;

    void followLine(double x1, double y1, double x2, double y2) throws RefreshPathFindingException, AvoidingException;

    void turnAround(double x, double y, double r) throws RefreshPathFindingException, AvoidingException;

    void setVitesse(long vDistance, long vOrientation);

    void waitMouvement() throws RefreshPathFindingException, AvoidingException;

    boolean isTrajetAtteint();

    boolean isTrajetEnApproche();

    AbstractMonitorMouvement getCurrentMouvement();

    void obstacleFound();

    void obstacleNotFound();

    void calageBordureDone();

    void refreshPathFinding();

    void cancelMouvement();
}
