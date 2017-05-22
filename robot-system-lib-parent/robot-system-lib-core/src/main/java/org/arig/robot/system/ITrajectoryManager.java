package org.arig.robot.system;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.CollisionFoundException;
import org.arig.robot.exception.NoPathFoundException;

/**
 * Created by dsorel on 22/05/17.
 */
public interface ITrajectoryManager {
    void init();

    void resetEncodeurs();

    void stop();

    void process();

    void pathTo(double x, double y) throws NoPathFoundException, AvoidingException;

    void gotoPointMM(double x, double y) throws CollisionFoundException;

    void gotoPointMM(double x, double y, boolean avecArret) throws CollisionFoundException;

    void gotoOrientationDeg(double angle) throws CollisionFoundException;

    void alignFrontTo(double x, double y) throws CollisionFoundException;

    void alignFrontToAvecDecalage(double x, double y, double decalageDeg) throws CollisionFoundException;

    void alignBackTo(double x, double y) throws CollisionFoundException;

    void avanceMM(double distance) throws CollisionFoundException;

    void avanceMMSansAngle(double distance) throws CollisionFoundException;

    void reculeMM(double distance) throws CollisionFoundException;

    void reculeMMSansAngle(double distance) throws CollisionFoundException;

    void tourneDeg(double angle) throws CollisionFoundException;

    void followLine(double x1, double y1, double x2, double y2) throws CollisionFoundException;

    void turnAround(double x, double y, double r) throws CollisionFoundException;

    void setVitesse(long vDistance, long vOrientation);

    void waitMouvement() throws CollisionFoundException;

    boolean isTrajetAtteint();

    boolean isTrajetEnApproche();

    void setObstacleFound(boolean obstacleFound);

    void setCollisionDetected(boolean collisionDetected);
}
