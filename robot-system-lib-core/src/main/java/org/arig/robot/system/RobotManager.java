package org.arig.robot.system;

import lombok.Getter;

import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class RobotManager.
 * 
 * @author mythril
 */
public class RobotManager {

    /** The obstacle detector. */
    @Autowired
    private IObstacleDetector obstacleDetector;

    /** The odom. */
    @Autowired
    private IOdometrie odom;

    /** The asserv. */
    @Autowired
    private IAsservissement asserv;

    /** The encoders. */
    @Autowired
    private Abstract2WheelsEncoders encoders;

    /** The motors. */
    @Autowired
    private AbstractMotors motors;

    /** The conv. */
    @Autowired
    private ConvertionRobotUnit conv;

    /** The trajet atteint. */
    @Getter
    private final boolean trajetAtteint = false;

    /** The trajet en approche. */
    @Getter
    private final boolean trajetEnApproche = false;

    /** The avoidance in progress. */
    private boolean avoidanceInProgress = false;

    /** The fenetre arret distance. */
    private final double fenetreArretDistance;

    /** The fenetre arret orientation. */
    private final double fenetreArretOrientation;

    /** The start angle. */
    private final double startAngle;

    /**
     * Instantiates a new robot manager.
     * 
     * @param arretDistanceMm
     *            the arret distance mm
     * @param arretOrientDeg
     *            the arret orient deg
     * @param coefAngle
     *            the coef angle
     */
    public RobotManager(final double arretDistanceMm, final double arretOrientDeg, final double coefAngle) {
        super();
        fenetreArretDistance = conv.mmToPulse(arretDistanceMm);
        fenetreArretOrientation = conv.degToPulse(arretOrientDeg);

        // Angle de départ pour les déplacement.
        // Si l'angle est supérieur en absolu, on annule la distance
        // afin de naviguer en priorité en marche avant.
        startAngle = coefAngle * conv.getPiPulse();
    }

    /**
     * Fonction permettant d'initialiser les composants externe pour le fonctionnement
     */
    public void init() {
        // Initialisation des cartes codeurs
        resetEncodeurs();

        // Initialisation du contrôle moteurs
        motors.init();
        motors.printVersion();

        // Arret
        stop();
    }

    /**
     * Reset encodeurs.
     */
    public void resetEncodeurs() {
        encoders.reset();
    }

    /**
     * Stop.
     */
    public void stop() {
        motors.stopDroit();
        motors.stopGauche();
    }

    /**
     * Process. Cette méthode permet de réaliser les fonctions lié aux déplacements.
     */
    public void process() {
        // 1. Calcul de la position du robot
        encoders.lectureValeurs();
        odom.calculPosition();

        // 2. Calcul des consignes
        calculConsigne();

        // 3. Gestion de l'evittement, de la reprise, et du cycle continue
        if (obstacleDetector.hasObstacle() && !avoidanceInProgress) {
            stop();
            asserv.reset(true);
            avoidanceInProgress = true;
        } else if (obstacleDetector.hasObstacle() && avoidanceInProgress) {
            // TODO : Trajectoire d'évittement. Comme le hasObstacle externalisé cette gestion au programme principale
        } else if (!obstacleDetector.hasObstacle() && avoidanceInProgress) {
            avoidanceInProgress = false;
        } else {
            // 3.4.1 Asservissement sur les consignes
            asserv.process();

            // 3.4.3 Envoi aux moteurs
            motors.generateMouvement(gauche, droit);
        }

        // 4. Gestion des flags pour le séquencement du calcul de la position
        gestionFlags();
    }

    /**
     * Goto point mm.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param frein
     *            the frein
     */
    public void gotoPointMM(final double x, final double y, final boolean frein) {

    }

    /**
     * Goto orientation deg.
     * 
     * @param angle
     *            the angle
     */
    public void gotoOrientationDeg(final double angle) {

    }

    /**
     * Align front to.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void alignFrontTo(final double x, final double y) {

    }

    /**
     * Align back to.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void alignBackTo(final double x, final double y) {

    }

    /**
     * Avance mm.
     * 
     * @param distance
     *            the distance
     */
    public void avanceMM(final double distance) {

    }

    /**
     * Recule mm.
     * 
     * @param distance
     *            the distance
     */
    public void reculeMM(final double distance) {

    }

    /**
     * Tourne deg.
     * 
     * @param angle
     *            the angle
     */
    public void tourneDeg(final double angle) {

    }

    /**
     * Follow line.
     * 
     * @param x1
     *            the x1
     * @param y1
     *            the y1
     * @param x2
     *            the x2
     * @param y2
     *            the y2
     */
    public void followLine(final double x1, final double y1, final double x2, final double y2) {

    }

    /**
     * Turn around.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param r
     *            the r
     */
    public void turnAround(final double x, final double y, final double r) {

    }
}
