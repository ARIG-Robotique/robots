package org.arig.robot.system;

import lombok.Getter;
import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.RobotConsigne;
import org.arig.robot.vo.RobotPosition;
import org.arig.robot.vo.enums.TypeConsigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class RobotManager.
 * 
 * @author mythril
 */
public class RobotManager {

    /** The obstacle detector. */
    @Autowired(required = false)
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

    /** Consigne du robot sur la table */
    @Autowired
    private RobotConsigne consigne;

    /** The trajet atteint. */
    @Getter
    private boolean trajetAtteint = false;

    /** The trajet en approche. */
    @Getter
    private boolean trajetEnApproche = false;

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
        if (obstacleDetector != null && obstacleDetector.hasObstacle() && !avoidanceInProgress) {
            stop();
            asserv.reset(true);
            avoidanceInProgress = true;
        } else if (obstacleDetector != null && obstacleDetector.hasObstacle() && avoidanceInProgress) {
            // TODO : Trajectoire d'évittement. Comme le hasObstacle externaliser cette gestion au programme principal
        } else if (obstacleDetector != null && !obstacleDetector.hasObstacle() && avoidanceInProgress) {
            avoidanceInProgress = false;
        } else {
            // 3.4.1 Asservissement sur les consignes
            asserv.process();

            // 3.4.3 Envoi aux moteurs
            motors.generateMouvement(consigne.getCmdGauche(), consigne.getCmdDroit());
        }

        // 4. Gestion des flags pour le séquencement du calcul de la position
        gestionFlags();
    }

    /**
     * Calcul des consignes d'asservissement
     * -> a : Gestion en fonction de l'odométrie
     * -> b : Si dans fenêtre d'approche : consigne(n) = consigne(n - 1) - d(position)
     */
    private void calculConsigne() {

        if (!trajetAtteint && consigne.isType(TypeConsigne.XY)) {
            // Calcul en fonction de l'odométrie
            long dX = (long) (consigne.getX() - odom.getPosition().getX());
            long dY = (long) (consigne.getY() - odom.getPosition().getY());

            // Calcul des consignes
            long consDist = calculDistanceConsigne(dX, dY);
            long consOrient = calculAngleConsigne(dX, dY);

            // Calcul du coef d'annulation de la distance
            // Permet d'effectuer d'abord une rotation avant de lancer le déplacement.
            if (Math.abs(consOrient) > startAngle) {
                consDist = (long) (consDist * ((startAngle - Math.abs(consOrient)) / startAngle));
            }

            // Sauvegarde des consignes
            consigne.setConsigneDistance(consDist);
            consigne.setConsigneOrientation(consOrient);

        } else if (!trajetAtteint && consigne.isType(TypeConsigne.LINE)) {
            // TODO : Consigne de suivi de ligne (géré les clothoïde pour la liaisons)

        } else if (!trajetAtteint && consigne.isType(TypeConsigne.CIRCLE)) {
            // TODO : Consigne de rotation autour d'un point.

        } else {
            // Calcul par différence vis a vis de la valeur codeur(asservissement de position "basique")

            if (consigne.isType(TypeConsigne.DIST)) {
                consigne.setConsigneDistance((long) (consigne.getConsigneDistance() - encoders.getDistance()));
            }
            if (consigne.isType(TypeConsigne.ANGLE)) {
                consigne.setConsigneOrientation((long) (consigne.getConsigneOrientation() - encoders.getOrientation()));
            }
        }
    }

    /**
     * Méthode de calcul de la consigne d'angle en fonction de dX et dY.
     *
     * @param dX
     * @param dY
     * @return
     */
    private long calculAngleConsigne(long dX, long dY) {
        double alpha = conv.radToPulse(Math.atan2(conv.pulseToRad(dY), conv.pulseToRad(dX)));

        // Ajustement a PI
        double orient = alpha - odom.getPosition().getAngle();
        if (orient > conv.getPiPulse()) {
            orient = orient - conv.getPi2Pulse();
        } else if (orient < -conv.getPiPulse()) {
            orient = orient + conv.getPi2Pulse();
        }

        return (long) orient;
    }

    /**
     * Méthode de calcul de la consigne de distance en fonction de dX et dY.
     *
     * @param dX
     * @param dY
     * @return
     */
    private long calculDistanceConsigne(long dX, long dY) {
        return (long) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    }

    private void gestionFlags() {
        // TODO : Voir si il ne serait pas judicieux de traiter le cas des consignes XY avec un rayon sur le point a atteindre
        if (consigne.isFrein()
                && Math.abs(consigne.getConsigneDistance()) < fenetreArretDistance
                && Math.abs(consigne.getConsigneOrientation()) < fenetreArretOrientation) {

            // Le trajet est atteint
            trajetAtteint = true;
        }

        if (Math.abs(consigne.getConsigneDistance()) < asserv.getFenetreApprocheDistance()
                && Math.abs(consigne.getConsigneOrientation()) < asserv.getFenetreApprocheOrientation()) {

            // Modification du type de consigne pour la stabilisation
            consigne.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);

            // Notification que le point de passage est atteint
            if (!consigne.isFrein()) {
                trajetEnApproche = true;
            }
        }
    }

    /**
     * Méthode permettant de donner une consigne de position sur un point
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param frein
     *            the frein
     */
    public void gotoPointMM(final double x, final double y, final boolean frein) {
        consigne.setAngle(0);
        consigne.setX(conv.mmToPulse(x));
        consigne.setY(conv.mmToPulse(y));
        consigne.setFrein(frein);
        consigne.setTypes(TypeConsigne.XY);

        prepareNextMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot sur un angle en fonction du repere
     * 
     * @param angle
     *            the angle
     */
    public void gotoOrientationDeg(final double angle) {
        double newOrient = angle - conv.pulseToDeg(odom.getPosition().getAngle());
        tourneDeg(newOrient);
    }

    /**
     * Méthode permettant d'aligner le robot face a un point
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void alignFrontTo(final long x, final long y) {
        long dX = (long) (conv.mmToPulse(x) - odom.getPosition().getX());
        long dY = (long) (conv.mmToPulse(y) - odom.getPosition().getY());

        consigne.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        consigne.setConsigneDistance(0);
        consigne.setConsigneOrientation(calculAngleConsigne(dX, dY));
        consigne.setFrein(true);

        prepareNextMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot dos a un point
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void alignBackTo(final double x, final double y) {
        long dX = (long) (conv.mmToPulse(x) - odom.getPosition().getX());
        long dY = (long) (conv.mmToPulse(y) - odom.getPosition().getY());

        long consOrient = calculAngleConsigne(dX, dY);
        if (consOrient > 0) {
            consOrient -= conv.getPiPulse();
        } else {
            consOrient += conv.getPiPulse();
        }

        consigne.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        consigne.setConsigneDistance(0);
        consigne.setConsigneOrientation(consOrient);
        consigne.setFrein(true);

        prepareNextMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en avant de distance fixe.
     * 
     * @param distance
     *            the distance
     */
    public void avanceMM(final double distance) {
        consigne.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        consigne.setConsigneDistance((long) conv.mmToPulse(distance));
        consigne.setConsigneOrientation(0);
        consigne.setFrein(true);

        prepareNextMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en arriere de distance fixe
     * 
     * @param distance
     *            the distance
     */
    public void reculeMM(final double distance) {
        avanceMM(-distance);
    }

    /**
     * Méthode permettant d'effectuer une rotation d'angle fixe
     * 
     * @param angle
     *            the angle
     */
    public void tourneDeg(final double angle) {
        consigne.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        consigne.setConsigneDistance(0);
        consigne.setConsigneOrientation((long) conv.degToPulse(angle));
        consigne.setFrein(true);

        prepareNextMouvement();
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
        // TODO : A implémenter la commande
        throw new NotYetImplementedException();
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
        // TODO : A implémenter la commande
        throw new NotYetImplementedException();
    }

    /**
     * Méthode pour préparer le prochain mouvement.
     */
    private void prepareNextMouvement() {
        // Reset de l'erreur de l'asserv sur le mouvement précédent lorsqu'il
        // s'agit d'un nouveau mouvement au départ vitesse presque nulle.
        if (trajetAtteint) {
            asserv.reset();
        }

        // Réinitialisation des infos de trajet.
        trajetAtteint = false;
        trajetEnApproche = false;
    }

    /**
     * Définition des vitesses de déplacement sur les deux axes du robot.
     *
     * @param vDistance
     * @param vOrientation
     */
    public void setVitesse(long vDistance, long vOrientation) {
        consigne.setVitesseDistance(vDistance);
        consigne.setVitesseOrientation(vOrientation);
    }
}
