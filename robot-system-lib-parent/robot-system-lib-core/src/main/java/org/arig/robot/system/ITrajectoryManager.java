package org.arig.robot.system;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;

public interface ITrajectoryManager {
    void init();

    void resetEncodeurs();

    void stop();

    void process();

    void pathTo(double x, double y) throws NoPathFoundException, AvoidingException;

    void pathTo(double x, double y, boolean avecArret) throws NoPathFoundException, AvoidingException;

    void gotoPointMM(double x, double y, boolean avecOrientation) throws AvoidingException;

    void gotoPointMM(double x, double y, boolean avecOrientation, boolean avecArret) throws AvoidingException;

    void gotoOrientationDeg(double angle) throws AvoidingException;

    void gotoOrientationDeg(double angle, SensRotation sensRotation) throws AvoidingException;

    void alignFrontTo(double x, double y) throws AvoidingException;

    void alignFrontToAvecDecalage(double x, double y, double decalageDeg) throws AvoidingException;

    void alignBackTo(double x, double y) throws AvoidingException;

    void avanceMM(double distance) throws AvoidingException;

    void avanceMMSansAngle(double distance) throws AvoidingException;

    void reculeMM(double distance) throws AvoidingException;

    void reculeMMSansAngle(double distance) throws AvoidingException;

    void tourneDeg(double angle) throws AvoidingException;

    void followLine(double x1, double y1, double x2, double y2) throws AvoidingException;

    void turnAround(double x, double y, double r) throws AvoidingException;

    void setVitesse(long vDistance, long vOrientation);

    void waitMouvement() throws AvoidingException;

    boolean isTrajetAtteint();

    boolean isTrajetEnApproche();

    AbstractMonitorMouvement getCurrentMouvement();

    /**
     * Informe le manager qu'il y a un obstacle et que les moteurs doivent être stoppés
     */
    void obstacleFound();

    /**
     * Informe le manager qu'il n'y a plus d'obstacle
     */
    void obstacleNotFound();

    /**
     * Informe le manager que le callage bordure est terminé
     */
    void calageBordureDone();

    /**
     * Informe le manager que le path doit être recalculé
     * Entraine une AvoidingException dans le thread d'action après trop de tentatives
     */
    void refreshPathFinding();

    /**
     * Informe le manager que le mouvement en cours doit être annulé
     * Entraine une AvoidingException dans le thread d'action
     */
    void cancelMouvement();
}
