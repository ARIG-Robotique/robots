package org.arig.robot.system;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.CollisionFoundException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.monitor.AbstractMonitor;
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
    public void gotoPointMM(double x, double y) throws CollisionFoundException {
        decorated.gotoPointMM(x, y);
    }

    @Override
    @Async
    public void gotoPointMM(double x, double y, boolean avecArret, boolean disableMonitor) throws CollisionFoundException {
        decorated.gotoPointMM(x, y, avecArret, disableMonitor);
    }

    @Override
    @Async
    public void gotoOrientationDeg(double angle) throws CollisionFoundException {
        decorated.gotoOrientationDeg(angle);
    }

    @Override
    @Async
    public void gotoOrientationDeg(double angle, SensRotation sensRotation) throws CollisionFoundException {
        decorated.gotoOrientationDeg(angle, sensRotation);
    }

    @Override
    @Async
    public void alignFrontTo(double x, double y) throws CollisionFoundException {
        decorated.alignFrontTo(x, y);
    }

    @Override
    @Async
    public void alignFrontToAvecDecalage(double x, double y, double decalageDeg) throws CollisionFoundException {
        decorated.alignFrontToAvecDecalage(x, y, decalageDeg);
    }

    @Override
    @Async
    public void alignBackTo(double x, double y) throws CollisionFoundException {
        decorated.alignBackTo(x, y);
    }

    @Override
    @Async
    public void avanceMM(double distance) throws CollisionFoundException {
        decorated.avanceMM(distance);
    }

    @Override
    @Async
    public void avanceMMSansAngle(double distance) throws CollisionFoundException {
        decorated.avanceMMSansAngle(distance);
    }

    @Override
    @Async
    public void reculeMM(double distance) throws CollisionFoundException {
        decorated.reculeMM(distance);
    }

    @Override
    @Async
    public void reculeMMSansAngle(double distance) throws CollisionFoundException {
        decorated.reculeMMSansAngle(distance);
    }

    @Override
    @Async
    public void tourneDeg(double angle) throws CollisionFoundException {
        decorated.tourneDeg(angle);
    }

    @Override
    @Async
    public void followLine(double x1, double y1, double x2, double y2) throws CollisionFoundException {
        decorated.followLine(x1, y1, x2, y2);
    }

    @Override
    @Async
    public void turnAround(double x, double y, double r) throws CollisionFoundException {
        decorated.turnAround(x, y, r);
    }

    @Override
    @Async
    public void setVitesse(long vDistance, long vOrientation) {
        decorated.setVitesse(vDistance, vOrientation);
    }

    @Override
    public void waitMouvement() throws CollisionFoundException {
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
    public void setObstacleFound(boolean obstacleFound) {
        decorated.setObstacleFound(obstacleFound);
    }

    @Override
    public void setCollisionDetected(boolean collisionDetected) {
        decorated.setCollisionDetected(collisionDetected);
    }
}
