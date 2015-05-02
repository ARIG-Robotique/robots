package org.arig.robot.system;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.csv.CsvData;
import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.Position;
import org.arig.robot.vo.enums.TypeConsigne;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class MouvementManager.
 * 
 * @author mythril
 */
@Slf4j
public class MouvementManager implements InitializingBean {

    /** Collector CSV */
    @Autowired(required = false)
    private CsvCollector csvCollector;

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
    private AbstractPropulsionsMotors motors;

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
    private boolean trajetAtteint, trajetEnApproche = false;

    /* Fenetre d'arret / approche distance */
    private double fenetreApprocheDistance;
    private double fenetreArretDistance;
    private final double arretDistanceMm;
    private final double approcheDistanceMm;

    /* Fenetre d'arret / approche orientation */
    private double fenetreApprocheOrientation;
    private double fenetreArretOrientation;
    private final double arretOrientDeg;
    private final double approcheOrientDeg;

    /** The start angle. */
    private final double coefAngle;
    private long startAngle;

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
    public MouvementManager(final double arretDistanceMm, final double approcheDistanceMm,
                            final double arretOrientDeg, final double approcheOrientDeg,
                            final double coefAngle) {
        super();

        // On stock les valeurs brut, le calcul sera fait sur le afterPropertiesSet.
        this.arretDistanceMm = arretDistanceMm;
        this.approcheDistanceMm = approcheDistanceMm;
        this.arretOrientDeg = arretOrientDeg;
        this.approcheOrientDeg = approcheOrientDeg;
        this.coefAngle = coefAngle;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fenetreArretDistance = conv.mmToPulse(arretDistanceMm);
        fenetreApprocheDistance = conv.mmToPulse(approcheDistanceMm);

        fenetreArretOrientation = conv.degToPulse(arretOrientDeg);
        fenetreApprocheOrientation = conv.degToPulse(approcheOrientDeg);

        // Angle de départ pour les déplacement.
        // Si l'angle est supérieur en absolu, on annule la distance
        // afin de naviguer en priorité en marche avant.
        startAngle = (long) (coefAngle * conv.getPiPulse());
        log.info("Angle pour le demi tour {}°", conv.pulseToDeg(startAngle));
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
        asservPolaire.reset(true);
    }

    /**
     * Process. Cette méthode permet de réaliser les fonctions lié aux déplacements.
     */
    public void process() {
        if (csvCollector != null) {
            csvCollector.addNewItem();
        }

        // 1. Calcul de la position du robot
        encoders.lectureValeurs();
        odom.calculPosition();

        // 2. Calcul des consignes
        calculConsigne();

        // 3. Asservissement sur les consignes
        asservPolaire.process();

        // 4. Envoi aux moteurs
        motors.generateMouvement(cmdRobot.getMoteur().getGauche(), cmdRobot.getMoteur().getDroit());

        // 5. Gestion des flags pour indiquer l'approche et l'atteinte sur l'objectif
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
            // Permet d'effectuer un demi tour en 3 temps.
            if (Math.abs(consOrient) > startAngle) {
                consDist = (consDist * ((startAngle - Math.abs(consOrient)) / startAngle));
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

        if (csvCollector != null) {
            CsvData c = csvCollector.getCurrent();
            c.setConsigneDistance(cmdRobot.getConsigne().getDistance());
            c.setConsigneOrient(cmdRobot.getConsigne().getOrientation());
            c.setVitesseDistance(cmdRobot.getVitesse().getDistance());
            c.setVitesseOrient(cmdRobot.getVitesse().getOrientation());
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
        return (long) ajusteAngle(alpha - position.getAngle());
    }

    /**
     * Méthode permettant d'ajuster l'angle en fonction du bornage +Pi .. -Pi
     *
     * @param angle
     * @return
     */
    private double ajusteAngle(double angle) {
        if (angle > conv.getPiPulse()) {
            return ajusteAngle(angle - conv.getPi2Pulse());
        } else if (angle < -conv.getPiPulse()) {
            return ajusteAngle(angle + conv.getPi2Pulse());
        }

        // L'angle est dans les borne.
        return angle;
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
        boolean distAtteint, orientAtteint, distApproche, orientApproche;

        // ------------------------------------------------------------------------------- //
        // Calcul du trajet atteints en mode freinage (toujours en DIST,ANGLE ici normale) //
        // ------------------------------------------------------------------------------- //

        distAtteint = Math.abs(cmdRobot.getConsigne().getDistance()) < fenetreArretDistance;
        orientAtteint = Math.abs(cmdRobot.getConsigne().getOrientation()) < fenetreArretOrientation;
        trajetAtteint = cmdRobot.isFrein() && distAtteint && orientAtteint;

        // -------------------------------------------------------------------------- //
        // Calcul des fenetres d'approche pour le passage au point suivant sans arret //
        // -------------------------------------------------------------------------- //

        // Si on est en mode déplacement XY, seul la distance d'approche du point est importante.
        if (cmdRobot.isType(TypeConsigne.XY)) {
            // Calcul en fonction de l'odométrie
            long dX = (long) (cmdRobot.getPosition().getPt().getX() - position.getPt().getX());
            long dY = (long) (cmdRobot.getPosition().getPt().getY() - position.getPt().getY());

            // On recalcul car la consigne de distance est altéré par le coeficient pour le demi tour
            distApproche = Math.abs(calculDistanceConsigne(dX, dY)) < fenetreArretDistance;
            orientApproche = true;
        } else {
            distApproche = Math.abs(cmdRobot.getConsigne().getDistance()) < fenetreApprocheDistance;
            orientApproche = Math.abs(cmdRobot.getConsigne().getOrientation()) < fenetreApprocheOrientation;
        }

        // Lorsque l'on est dans la fenetre d'approche on bascule l'asserve en mode basique
        // Si on ne fait pas ça on obtient une spirale sur le point d'arrivé qui est jolie mais pas très pratique
        if (distApproche && orientApproche) {
            // Modification du type de consigne pour la stabilisation
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);

            // Notification que le point de passage est atteint uniquement lors d'un enchainement sans arret
            if (!cmdRobot.isFrein()) {
                trajetEnApproche = true;
            }
        }

        if (csvCollector != null) {
            CsvData c = csvCollector.getCurrent();
            c.setModeAsserv(cmdRobot.typeAsserv());
            c.setTrajetAtteint(trajetAtteint);
            c.setTrajetEnApproche(trajetEnApproche);
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
