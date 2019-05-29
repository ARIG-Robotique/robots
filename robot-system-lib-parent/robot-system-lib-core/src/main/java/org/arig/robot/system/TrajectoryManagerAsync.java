package org.arig.robot.system;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.springframework.scheduling.annotation.Async;

public class TrajectoryManagerAsync implements ITrajectoryManager {

    private ITrajectoryManager decorated;

    public TrajectoryManagerAsync(ITrajectoryManager decorated) {
        super();
        this.decorated = decorated;
    }

    @Override
    public void init() {
        decorated.init();
    }

    @Override
    public void resetEncodeurs() {
        decorated.resetEncodeurs();
    }

    @Override
    public void stop() {
        decorated.stop();
    }

    @Override
    public void process() {
        decorated.process();
    }

    @Override
    @Async
    public void pathTo(double x, double y) throws NoPathFoundException, AvoidingException {
        decorated.pathTo(x, y);
    }

    @Override
    @Async
    public void pathTo(double x, double y, boolean avecArret) throws NoPathFoundException, AvoidingException {
        decorated.pathTo(x, y, avecArret);
    }

    @Override
    @Async
    public void gotoPointMM(double x, double y) throws RefreshPathFindingException, AvoidingException {
        decorated.gotoPointMM(x, y);
    }

    @Override
    @Async
    public void gotoPointMM(double x, double y, boolean avecArret) throws RefreshPathFindingException, AvoidingException {
        decorated.gotoPointMM(x, y, avecArret);
    }

    @Override
    @Async
    public void gotoOrientationDeg(double angle) throws RefreshPathFindingException, AvoidingException {
        decorated.gotoOrientationDeg(angle);
    }

    @Override
    @Async
    public void gotoOrientationDeg(double angle, SensRotation sensRotation) throws RefreshPathFindingException, AvoidingException {
        decorated.gotoOrientationDeg(angle, sensRotation);
    }

    @Override
    @Async
    public void alignFrontTo(double x, double y) throws RefreshPathFindingException, AvoidingException {
        decorated.alignFrontTo(x, y);
    }

    @Override
    @Async
    public void alignFrontToAvecDecalage(double x, double y, double decalageDeg) throws RefreshPathFindingException, AvoidingException {
        decorated.alignFrontToAvecDecalage(x, y, decalageDeg);
    }

    @Override
    @Async
    public void alignBackTo(double x, double y) throws RefreshPathFindingException, AvoidingException {
        decorated.alignBackTo(x, y);
    }

    @Override
    @Async
    public void avanceMM(double distance) throws RefreshPathFindingException, AvoidingException {
        decorated.avanceMM(distance);
    }

    @Override
    @Async
    public void avanceMMSansAngle(double distance) throws RefreshPathFindingException, AvoidingException {
        decorated.avanceMMSansAngle(distance);
    }

    @Override
    @Async
    public void reculeMM(double distance) throws RefreshPathFindingException, AvoidingException {
        decorated.reculeMM(distance);
    }

    @Override
    @Async
    public void reculeMMSansAngle(double distance) throws RefreshPathFindingException, AvoidingException {
        decorated.reculeMMSansAngle(distance);
    }

    @Override
    @Async
    public void tourneDeg(double angle) throws RefreshPathFindingException, AvoidingException {
        decorated.tourneDeg(angle);
    }

    @Override
    @Async
    public void followLine(double x1, double y1, double x2, double y2) throws RefreshPathFindingException, AvoidingException {
        decorated.followLine(x1, y1, x2, y2);
    }

    @Override
    @Async
    public void turnAround(double x, double y, double r) throws RefreshPathFindingException, AvoidingException {
        decorated.turnAround(x, y, r);
    }

    @Override
    @Async
    public void setVitesse(long vDistance, long vOrientation) {
        decorated.setVitesse(vDistance, vOrientation);
    }

    @Override
    public void waitMouvement() throws RefreshPathFindingException, AvoidingException {
        decorated.waitMouvement();
    }

    @Override
    public boolean isTrajetAtteint() {
        return decorated.isTrajetAtteint();
    }

    @Override
    public boolean isTrajetEnApproche() {
        return decorated.isTrajetEnApproche();
    }

    @Override
    public AbstractMonitorMouvement getCurrentMouvement() {
        return decorated.getCurrentMouvement();
    }

    @Override
    public void obstacleFound() {
        decorated.obstacleFound();
    }

    @Override
    public void obstacleNotFound() {
        decorated.obstacleNotFound();
    }

    @Override
    public void calageBordureDone() {
        decorated.calageBordureDone();
    }

    @Override
    public void refreshPathFinding() {
        decorated.refreshPathFinding();
    }

    @Override
    public void cancelMouvement() {
        decorated.cancelMouvement();
    }
}
