package org.arig.robot.system;

import lombok.Getter;
import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissement;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Position;
import org.arig.robot.vo.enums.TypeConsigne;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class RobotManager.
 * 
 * @author mythril
 */
public class RobotManager implements InitializingBean {

    /** The obstacle detector. */
    @Autowired(required = false)
    private IObstacleDetector obstacleDetector;

    /** The odom. */
    @Autowired
    private IOdometrie odom;

    /** The asservPolaire. */
    @Autowired
    private IAsservissementPolaire asservPolaire;

    /** The encoders. */
    @Autowired
    private Abstract2WheelsEncoders encoders;

    /** The motors. */
    @Autowired
    private AbstractMotors motors;

    /** The conv. */
    @Autowired
    private ConvertionRobotUnit conv;

    /** The position. */
    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    /** Consigne du robot sur la table */
    @Autowired
    private CommandeRobot cmdRobot;

    /** The trajet atteint. */
    @Getter
    private boolean trajetAtteint = false;

    /** The trajet en approche. */
    @Getter
    private boolean trajetEnApproche = false;

    /** The avoidance in progress. */
    private boolean avoidanceInProgress = false;

    /** The fenetre arret distance. */
    private double fenetreArretDistance;

    /** The fenetre arret orientation. */
    private double fenetreArretOrientation;

    /** The start angle. */
    private double startAngle;

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

        // On stock les valeurs brut, le calcul sera fait sur le afterPropertiesSet.
        fenetreArretDistance = arretDistanceMm;
        fenetreArretOrientation = arretOrientDeg;
        startAngle = coefAngle;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fenetreArretDistance = conv.mmToPulse(fenetreArretDistance);
        fenetreArretOrientation = conv.degToPulse(fenetreArretOrientation);

        // Angle de départ pour les déplacement.
        // Si l'angle est supérieur en absolu, on annule la distance
        // afin de naviguer en priorité en marche avant.
        startAngle = startAngle * conv.getPiPulse();
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
            asservPolaire.reset(true);
            avoidanceInProgress = true;
        } else if (obstacleDetector != null && obstacleDetector.hasObstacle() && avoidanceInProgress) {
            // TODO : Trajectoire d'évittement. Comme le hasObstacle externaliser cette gestion au programme principal
        } else if (obstacleDetector != null && !obstacleDetector.hasObstacle() && avoidanceInProgress) {
            avoidanceInProgress = false;
        } else {
            // 3.4.1 Asservissement sur les consignes
            asservPolaire.process();

            // 3.4.3 Envoi aux moteurs
            motors.generateMouvement(cmdRobot.getMoteur().getGauche(), cmdRobot.getMoteur().getDroit());
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

        if (!trajetAtteint && cmdRobot.isType(TypeConsigne.XY)) {
            // Calcul en fonction de l'odométrie
            long dX = (long) (cmdRobot.getPosition().getPt().getX() - position.getPt().getX());
            long dY = (long) (cmdRobot.getPosition().getPt().getY() - position.getPt().getY());

            // Calcul des consignes
            long consDist = calculDistanceConsigne(dX, dY);
            long consOrient = calculAngleConsigne(dX, dY);

            // Calcul du coef d'annulation de la distance
            // Permet d'effectuer d'abord une rotation avant de lancer le déplacement.
            if (Math.abs(consOrient) > startAngle) {
                consDist = (long) (consDist * ((startAngle - Math.abs(consOrient)) / startAngle));
            }

            // Sauvegarde des consignes
            cmdRobot.getConsigne().setDistance(consDist);
            cmdRobot.getConsigne().setOrientation(consOrient);

        } else if (!trajetAtteint && cmdRobot.isType(TypeConsigne.LINE)) {
            // TODO : Consigne de suivi de ligne (gérer les clothoïde pour la liaisons)

        } else if (!trajetAtteint && cmdRobot.isType(TypeConsigne.CIRCLE)) {
            // TODO : Consigne de rotation autour d'un point.

        } else {
            // Calcul par différence vis a vis de la valeur codeur(asservissement de position "basique")
            if (cmdRobot.isType(TypeConsigne.DIST)) {
                cmdRobot.getConsigne().setDistance((long) (cmdRobot.getConsigne().getDistance() - encoders.getDistance()));
            }
            if (cmdRobot.isType(TypeConsigne.ANGLE)) {
                cmdRobot.getConsigne().setOrientation((long) (cmdRobot.getConsigne().getOrientation() - encoders.getOrientation()));
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
        double orient = alpha - position.getAngle();
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
        if (cmdRobot.isFrein()
                && Math.abs(cmdRobot.getConsigne().getDistance()) < fenetreArretDistance
                && Math.abs(cmdRobot.getConsigne().getOrientation()) < fenetreArretOrientation) {

            // Le trajet est atteint
            trajetAtteint = true;
        }

        if (Math.abs(cmdRobot.getConsigne().getDistance()) < asservPolaire.getFenetreApprocheDistance()
                && Math.abs(cmdRobot.getConsigne().getOrientation()) < asservPolaire.getFenetreApprocheOrientation()) {

            // Modification du type de consigne pour la stabilisation
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);

            // Notification que le point de passage est atteint
            if (!cmdRobot.isFrein()) {
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
        cmdRobot.getPosition().setAngle(0);
        cmdRobot.getPosition().getPt().setX(conv.mmToPulse(x));
        cmdRobot.getPosition().getPt().setY(conv.mmToPulse(y));
        cmdRobot.setFrein(frein);
        cmdRobot.setTypes(TypeConsigne.XY);

        prepareNextMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot sur un angle en fonction du repere
     * 
     * @param angle
     *            the angle
     */
    public void gotoOrientationDeg(final double angle) {
        double newOrient = angle - conv.pulseToDeg(position.getAngle());
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
    public void alignFrontTo(final double x, final double y) {
        long dX = (long) (conv.mmToPulse(x) - position.getPt().getX());
        long dY = (long) (conv.mmToPulse(y) - position.getPt().getY());

        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation(calculAngleConsigne(dX, dY));
        cmdRobot.setFrein(true);

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
        long dX = (long) (conv.mmToPulse(x) - position.getPt().getX());
        long dY = (long) (conv.mmToPulse(y) - position.getPt().getY());

        long consOrient = calculAngleConsigne(dX, dY);
        if (consOrient > 0) {
            consOrient -= conv.getPiPulse();
        } else {
            consOrient += conv.getPiPulse();
        }

        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation(consOrient);
        cmdRobot.setFrein(true);

        prepareNextMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en avant de distance fixe.
     * 
     * @param distance
     *            the distance
     */
    public void avanceMM(final double distance) {
        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance((long) conv.mmToPulse(distance));
        cmdRobot.getConsigne().setOrientation(0);
        cmdRobot.setFrein(true);

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
        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation((long) conv.degToPulse(angle));
        cmdRobot.setFrein(true);

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
        // Reset de l'erreur de l'asservPolaire sur le mouvement précédent lorsqu'il
        // s'agit d'un nouveau mouvement au départ vitesse presque nulle.
        if (trajetAtteint) {
            asservPolaire.reset();
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
        cmdRobot.getVitesse().setDistance(vDistance);
        cmdRobot.getVitesse().setOrientation(vOrientation);
    }
}
