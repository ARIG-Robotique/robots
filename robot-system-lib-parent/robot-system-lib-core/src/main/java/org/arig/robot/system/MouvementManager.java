package org.arig.robot.system;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.Chemin;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class MouvementManager.
 *
 * @author gdepuille
 */
@Slf4j
public class MouvementManager implements InitializingBean {

    @Autowired
    private IOdometrie odom;

    @Autowired
    private IAsservissementPolaire asservPolaire;

    @Autowired
    private Abstract2WheelsEncoders encoders;

    @Autowired
    private AbstractPropulsionsMotors propulsionsMotors;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private IPathFinder pathFinder;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private CommandeRobot cmdRobot;

    @Getter
    private boolean trajetAtteint, trajetEnApproche = false;

    /** Boolean si un obstacle est rencontré (stop le robot sur place) **/
    @Setter
    private boolean obstacleFound = false;

    /** Boolean pour relancer après un obstacle (gestion de l'évittement) */
    @Setter
    private boolean restartAfterObstacle = false;

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

    private final double coefAngle;
    private long startAngle;

    /**
     * Instantiates a new robot manager.
     *
     * @param arretDistanceMm the arret distance mm
     * @param arretOrientDeg  the arret orient deg
     * @param coefAngle       the coef angle
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

        // Angle de départ pour les déplacements.
        // Si l'angle est supérieur en absolu, on annule la distance
        // afin de naviguer en priorité en marche avant.
        // Cela a pour effet de tourner sur place en reculant avant de partir en avant.
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
        propulsionsMotors.init();
        propulsionsMotors.printVersion();

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
        propulsionsMotors.stopDroit();
        propulsionsMotors.stopGauche();
        asservPolaire.reset(true);
    }

    /**
     * Process. Cette méthode permet de réaliser les fonctions lié aux déplacements.
     */
    public void process() {
        // 1. Calcul de la position du robot
        encoders.lectureValeurs();
        odom.calculPosition();

        // 2. Gestion de l'evittement
        if (obstacleFound) {
            // Obstacle détecté, on stop les moteurs
            stop();

            // Commande moteur null
            cmdRobot.getMoteur().setDroit(0);
            cmdRobot.getMoteur().setGauche(0);
        } else {
            // C. Pas d'obstacle, asservissement koi
            // C.1. Calcul des consignes
            calculConsigne();

            // C.2. Asservissement sur les consignes
            asservPolaire.process();
        }

        // 3. Envoi aux moteurs
        propulsionsMotors.generateMouvement(cmdRobot.getMoteur().getGauche(), cmdRobot.getMoteur().getDroit());

        // 4. Gestion des flags pour indiquer l'approche et l'atteinte sur l'objectif
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
            // TODO : Consigne de suivi de ligne (gérer les clothoïde pour la liaison)

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
     * @param dX delta X
     * @param dY delta Y
     *
     * @return valeur en pulse de l'angle ajusté à PI
     */
    private long calculAngleConsigne(long dX, long dY) {
        double alpha = conv.radToPulse(Math.atan2(conv.pulseToRad(dY), conv.pulseToRad(dX)));

        // Ajustement a PI
        return (long) ajusteAngle(alpha - position.getAngle());
    }

    /**
     * Méthode permettant d'ajuster l'angle en fonction du bornage +Pi .. -Pi
     *
     * @param angle angle a ajusté
     *
     * @return Angle ajusté dans les borne -Pi .. +Pi
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
     * @param dX delta X
     * @param dY delta Y
     *
     * @return valeur de la consigne de distance
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
            distApproche = Math.abs(calculDistanceConsigne(dX, dY)) < fenetreApprocheDistance;
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
    }

    /**
     * Génération d'un déplacement avec le Path Finding
     *
     * @param x position sur l'axe X
     * @param y position sur l'axe Y
     *
     * @throws NoPathFoundException
     */
    public void pathTo(final double x, final double y) throws NoPathFoundException, AvoidingException {
        boolean trajetOk = false;

        mainBoucle: do {
            Point ptFrom = new Point(conv.pulseToMm(position.getPt().getX()) / 10, conv.pulseToMm(position.getPt().getY()) / 10);
            Point ptTo = new Point(x / 10, y / 10);
            try {
                log.info("Demande de chemin vers X = {}mm ; Y = {}mm", x, y);
                Chemin c = pathFinder.findPath(ptFrom, ptTo);
                while (c.hasNext()) {
                    Point p = c.next();

                    // Processing du path
                    gotoPointMM(p.getX() * 10, p.getY() * 10, !c.hasNext());

                    // Mise à jour de la position From après le mouvement
                    ptFrom.setX(conv.pulseToMm(position.getPt().getX()) / 10);
                    ptFrom.setY(conv.pulseToMm(position.getPt().getY()) / 10);
                }

                // Condition de sortie de la boucle.
                trajetOk = true;
            } catch (ObstacleFoundException e) {
                log.info("Obstacle trouvé, on tente un autre chemin");
                for (Point echappementPoint : rs.echappementPointsCm()) {
                    try {
                        ptFrom.setX(conv.pulseToMm(position.getPt().getX()) / 10);
                        ptFrom.setY(conv.pulseToMm(position.getPt().getY()) / 10);
                        Chemin c = pathFinder.findPath(ptFrom, echappementPoint);
                        Point p = c.next();
                        alignFrontTo(p.getX() * 10, p.getY() * 10);

                        // Une fois aligné et normalement plus en face de l'obstacle, on réactive
                        // l'évittement pour réaliser le chemin d'évittement
                        rs.enableAvoidance();
                        while (c.hasNext()) {
                            p = c.next();

                            // Processing du path
                            gotoPointMM(p.getX() * 10, p.getY() * 10);

                            // Mise à jour de la position From après le mouvement
                            ptFrom.setX(conv.pulseToMm(position.getPt().getX()) / 10);
                            ptFrom.setY(conv.pulseToMm(position.getPt().getY()) / 10);
                        }

                        continue mainBoucle;

                    } catch (ObstacleFoundException ofe) {
                        log.warn("Point de passage impossible. On passe au suivant");
                    } finally {
                        rs.enableAvoidance();
                    }
                }

                log.error("Erreur lors de l'évittement on tente autre chose.");
                throw new AvoidingException();
            } finally {
                // Restauration de
                rs.enableAvoidance();
            }
        } while (!trajetOk);
    }

    /**
     * Méthode permettant de donner une consigne de position sur un point avec arret sur celui-ci.
     *
     * @param x position sur l'axe X
     * @param y position sur l'axe Y
     */
    public void gotoPointMM(final double x, final double y) throws ObstacleFoundException {
        gotoPointMM(x, y, true);
    }

    /**
     * Méthode permettant de donner une consigne de position sur un point
     *
     * @param x position sur l'axe X
     * @param y position sur l'axe Y
     * @param avecArret demande d'arret sur le point
     */
    public void gotoPointMM(final double x, final double y, final boolean avecArret) throws ObstacleFoundException {
        log.info("Va au point X = {}mm ; Y = {}mm {}", x, y, avecArret ? "et arrete toi" : "sans arret");
        cmdRobot.getPosition().setAngle(0);
        cmdRobot.getPosition().getPt().setX(conv.mmToPulse(x));
        cmdRobot.getPosition().getPt().setY(conv.mmToPulse(y));
        cmdRobot.setFrein(avecArret);
        cmdRobot.setTypes(TypeConsigne.XY);

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot sur un angle en fonction du repere
     *
     * @param angle the angle
     */
    public void gotoOrientationDeg(final double angle) throws ObstacleFoundException {
        log.info("Aligne toi sur l'angle {}° du repère", angle);
        double newOrient = angle - conv.pulseToDeg(position.getAngle());
        tourneDeg(newOrient);
    }

    /**
     * Méthode permettant d'aligner le robot face a un point
     *
     * @param x the x
     * @param y the y
     */
    public void alignFrontTo(final double x, final double y) throws ObstacleFoundException {
        log.info("Aligne ton avant sur le point X = {}mm ; Y = {}mm", x, y);
        alignFrontToAvecDecalage(x, y, 0);
    }

    /**
     * Alignement sur un point avec un décalage en degré (dans le sens trigo)
     *
     * @param x position sur l'axe X
     * @param y position sur l'axe Y
     * @param decalageDeg valeur du déclage angulaire par rapport au point X,Y
     *
     * @throws ObstacleFoundException
     */
    public void alignFrontToAvecDecalage(final double x, final double y, final double decalageDeg) throws ObstacleFoundException {
        if (decalageDeg != 0) {
            log.info("Décalage de {}° par rapport au point X = {}mm ; Y = {}mm", decalageDeg, x, y);
        }

        long dX = (long) (conv.mmToPulse(x) - position.getPt().getX());
        long dY = (long) (conv.mmToPulse(y) - position.getPt().getY());

        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation(calculAngleConsigne(dX, dY) + (long) conv.degToPulse(decalageDeg));
        cmdRobot.setFrein(true);

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot dos a un point
     *
     * @param x the x
     * @param y the y
     */
    public void alignBackTo(final double x, final double y) throws ObstacleFoundException {
        log.info("Aligne ton cul sur le point X = {}mm ; Y = {}mm", x, y);

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
        waitMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en avant de distance fixe.
     *
     * @param distance the distance
     */
    public void avanceMM(final double distance) throws ObstacleFoundException {
        cmdAvanceMMByType(distance, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void avanceMMSansAngle(final double distance) throws ObstacleFoundException {
        cmdAvanceMMByType(distance, TypeConsigne.DIST);
    }

    private void cmdAvanceMMByType(final double distance, TypeConsigne... types) throws ObstacleFoundException {
        if (distance > 0) {
            log.info("{} de {}mm en mode : {}", distance > 0 ? "Avance" : "Recul", distance, StringUtils.join(types, ", "));
        }

        cmdRobot.setTypes(types);
        cmdRobot.getConsigne().setDistance((long) conv.mmToPulse(distance));
        cmdRobot.getConsigne().setOrientation(0);
        cmdRobot.setFrein(true);

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en arriere de distance fixe
     *
     * @param distance the distance
     */
    public void reculeMM(final double distance) throws ObstacleFoundException {
        log.info("Recul de {}mm", Math.abs(distance));
        avanceMM(-distance);
    }

    public void reculeMMSansAngle(final double distance) throws ObstacleFoundException {
        log.info("Recul de {}mm sans angle", Math.abs(distance));
        avanceMMSansAngle(-distance);
    }

    /**
     * Méthode permettant d'effectuer une rotation d'angle fixe
     *
     * @param angle the angle
     */
    public void tourneDeg(final double angle) throws ObstacleFoundException {
        log.info("Tourne de {}°", angle);

        boolean isAvoidance = rs.isAvoidanceEnabled();
        try {
            rs.disableAvoidance();
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation((long) conv.degToPulse(angle));
            cmdRobot.setFrein(true);

            prepareNextMouvement();
            waitMouvement();
        } finally {
            if (isAvoidance) {
                rs.enableAvoidance();
            }
        }
    }

    /**
     * Follow line.
     *
     * @param x1 the x1
     * @param y1 the y1
     * @param x2 the x2
     * @param y2 the y2
     */
    public void followLine(final double x1, final double y1, final double x2, final double y2) throws ObstacleFoundException {
        // TODO : A implémenter la commande
        throw new NotYetImplementedException();
    }

    /**
     * Turn around.
     *
     * @param x the x
     * @param y the y
     * @param r the r
     */
    public void turnAround(final double x, final double y, final double r) throws ObstacleFoundException {
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
        restartAfterObstacle = false;
        obstacleFound = false;
    }

    /**
     * Définition des vitesses de déplacement sur les deux axes du robot.
     *
     * @param vDistance vitesse pour la boucle distance
     * @param vOrientation vitesse pour la boucle orientation
     */
    public void setVitesse(long vDistance, long vOrientation) {
        cmdRobot.getVitesse().setDistance(vDistance);
        cmdRobot.getVitesse().setOrientation(vOrientation);
    }

    /**
     * Permet d'attendre le passage au point suivant
     */
    public void waitMouvement() throws ObstacleFoundException {
        if (cmdRobot.isFrein()) {
            log.info("Attente fin de trajet");
            while (!isTrajetAtteint()) {
                try {
                    checkRestartAfterObstacle();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Problème dans l'attente d'atteinte du point : {}", e.toString());
                }
            }
            log.info("Trajet atteint");
        } else {
            log.info("Attente approche du point de passage");
            while (!isTrajetEnApproche()) {
                try {
                    checkRestartAfterObstacle();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Problème dans l'approche du point : {}", e.toString());
                }
            }
            log.info("Point de passage atteint");
        }
    }

    private void checkRestartAfterObstacle() throws ObstacleFoundException {
        if (restartAfterObstacle) {
            restartAfterObstacle = false;
            throw new ObstacleFoundException();
        }
    }
}
