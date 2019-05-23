package org.arig.robot.system;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.*;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.model.monitor.*;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class TrajectoryManager.
 *
 * @author gdepuille
 */
@Slf4j
public class TrajectoryManager implements InitializingBean, ITrajectoryManager {

    @Autowired
    private IOdometrie odom;

    @Autowired
    private IMonitoringWrapper monitoring;

    @Autowired
    private IAsservissementPolaire asservissementPolaire;

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
    private Position currentPosition;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    protected IMonitoringWrapper monitoringWrapper;

    @Getter
    private AbstractMonitorMouvement currentMouvement = null;

    private AtomicBoolean trajetAtteint = new AtomicBoolean(false);

    private AtomicBoolean trajetEnApproche = new AtomicBoolean(false);

    /**
     * Boolean si un obstacle est rencontré (stop le robot sur place)
     **/
    private AtomicBoolean obstacleFound = new AtomicBoolean(false);

    /**
     * Boolean si un calage bordure est demandé (consigne distance angle = 0)
     */
    private AtomicBoolean calageBordure = new AtomicBoolean(false);

    /**
     * Boolean pour relancer après un obstacle (gestion de l'évittement)
     */
    private AtomicBoolean refreshPath = new AtomicBoolean(false);

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
    private long startAngle = -1;

    /**
     * Instantiates a new robot manager.
     *
     * @param arretDistanceMm the arret distance mm
     * @param arretOrientDeg  the arret orient deg
     * @param coefAngle       the coef angle
     */
    public TrajectoryManager(final double arretDistanceMm, final double approcheDistanceMm,
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
    public boolean isTrajetAtteint() {
        return trajetAtteint.get();
    }

    @Override
    public boolean isTrajetEnApproche() {
        return trajetEnApproche.get();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        fenetreArretDistance = conv.mmToPulse(arretDistanceMm);
        fenetreApprocheDistance = conv.mmToPulse(approcheDistanceMm);

        fenetreArretOrientation = conv.degToPulse(arretOrientDeg);
        fenetreApprocheOrientation = conv.degToPulse(approcheOrientDeg);

        if (coefAngle != -1) {
            // Angle de départ pour les déplacements.
            // Si l'angle est supérieur en absolu, on annule la distance
            // afin de naviguer en priorité en marche avant.
            // Cela a pour effet de tourner sur place en reculant avant de partir en avant.
            startAngle = (long) (coefAngle * conv.getPiPulse());
            log.info("Angle pour le demi tour {}°", conv.pulseToDeg(startAngle));
        } else {
            log.info("Angle pour le demi tour désactivé");
        }
    }

    /**
     * Fonction permettant d'initialiser les composants externe pour le fonctionnement
     */
    @Override
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
    @Override
    public void resetEncodeurs() {
        encoders.reset();
    }

    /**
     * Stop.
     */
    @Override
    public void stop() {
        propulsionsMotors.stopDroit();
        propulsionsMotors.stopGauche();
        asservissementPolaire.reset(true);
    }

    /**
     * Process. Cette méthode permet de réaliser les fonctions lié aux déplacements.
     */
    @Override
    public void process() {
        // 1. Calcul de la position du robot
        encoders.lectureValeurs();

        long tStart = System.nanoTime();
        odom.calculPosition();

        long tCalcul1 = System.nanoTime();
        long tCalcul2 = tCalcul1;
        long tAsserv = tCalcul1;

        // 2. Gestion de l'evittement
        if (obstacleFound.get()) {
            // Obstacle détecté, on stop les moteurs
            stop();

            // Commande moteur null
            cmdRobot.getMoteur().setDroit(0);
            cmdRobot.getMoteur().setGauche(0);
        } else {
            // C. Pas d'obstacle, asservissement koi
            // C.1. Calcul des consignes
            calculConsigne();

            tCalcul2 = System.nanoTime();

            // C.2. Asservissement sur les consignes
            asservissementPolaire.process();

            tAsserv = System.nanoTime();
        }

        // 3. Envoi aux moteurs
        propulsionsMotors.generateMouvement(cmdRobot.getMoteur().getGauche(), cmdRobot.getMoteur().getDroit());

        // 4. Gestion des flags pour indiquer l'approche et l'atteinte sur l'objectif
        gestionFlags();

        long tEnd = System.nanoTime();

        final MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("asservissement")
                .addTag(MonitorTimeSerie.TAG_NAME, "polaire")
                .addField("t_calcul1", tCalcul1 - tStart)
                .addField("t_calcul2", tCalcul2 - tCalcul1)
                .addField("t_asserv", tAsserv - tCalcul2)
                .addField("t_flags", tEnd - tAsserv)
                .addField("mot_d", cmdRobot.getMoteur().getDroit())
                .addField("mot_g", cmdRobot.getMoteur().getGauche());

        monitoringWrapper.addTimeSeriePoint(serie);
    }

    /**
     * Calcul des consignes d'asservissement
     * -> a : Gestion en fonction de l'odométrie
     * -> b : Si dans fenêtre d'approche : consigne(n) = consigne(n - 1) - d(position)
     */
    private void calculConsigne() {
        if (calageBordure.get()) {
            // Un calage sur bordure est fait. On asservi sur place jusqu'au prochain mouvement
            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation(0);
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
            calageBordure.set(false);
        }

        if (!trajetAtteint.get() && cmdRobot.isType(TypeConsigne.XY)) {
            // Calcul en fonction de l'odométrie
            double dX = (cmdRobot.getPosition().getPt().getX() - currentPosition.getPt().getX());
            double dY = (cmdRobot.getPosition().getPt().getY() - currentPosition.getPt().getY());

            // Calcul des consignes
            double consDist = calculDistance(dX, dY);
            double consOrient = calculAngleConsigne(dX, dY);

            // Calcul du coef d'annulation de la distance
            // Permet d'effectuer un demi tour en 3 temps.
            if (startAngle != -1 && Math.abs(consOrient) > startAngle) {
                consDist = (consDist * ((startAngle - Math.abs(consOrient)) / startAngle));
            }

            // Sauvegarde des consignes
            cmdRobot.getConsigne().setDistance((long) consDist);
            cmdRobot.getConsigne().setOrientation((long) consOrient);

        } else if (!trajetAtteint.get() && cmdRobot.isType(TypeConsigne.LINE)) {
            // TODO : Consigne de suivi de ligne (gérer les clothoïde pour la liaison)

        } else if (!trajetAtteint.get() && cmdRobot.isType(TypeConsigne.CIRCLE)) {
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
     * @return valeur en pulse de l'angle ajusté à PI
     */
    private double calculAngleConsigne(double dX, double dY) {

        return conv.degToPulse(calculAngleDelta(
                conv.pulseToDeg(currentPosition.getAngle()),
                Math.toDegrees(Math.atan2(conv.pulseToRad(dY), conv.pulseToRad(dX))),
                SensRotation.AUTO
        ));
    }

    /**
     * Méthode permettant d'ajuster l'angle en fonction du bornage -180 .. 180
     *
     * @param angle angle a ajusté
     * @return Angle ajusté dans les borne -180 .. 180
     */
    private double ajusteAngle(double angle) {
        if (angle > 180) {
            return ajusteAngle(angle - 360);
        } else if (angle < -180) {
            return ajusteAngle(angle + 360);
        }

        // L'angle est dans les borne.
        return angle;
    }

    /**
     * Calcule de l'angle de déplacement le plus court entre deux angles
     *
     * @param angleOrig    angle courant
     * @param angle        angle désiré
     * @param sensRotation
     * @return
     */
    private double calculAngleDelta(double angleOrig, double angle, SensRotation sensRotation) {
        angle = ajusteAngle(angle);

        switch (sensRotation) {
            case TRIGO:
                if (angle > angleOrig) {
                    return angle - angleOrig;
                } else {
                    return angle - angleOrig + 360;
                }

            case HORAIRE:
                if (angle < angleOrig) {
                    return angle - angleOrig;
                } else {
                    return angle - angleOrig - 360;
                }

            case AUTO:
                return Stream.of(
                        angle - angleOrig,
                        angle - angleOrig - 360.0,
                        angle - angleOrig + 360.0
                )
                        .min((a, b) -> new Double(Math.abs(a) - Math.abs(b)).intValue())
                        .get();

            default:
                return 0;
        }
    }

    /**
     * Méthode de calcul de la consigne de distance en fonction de dX et dY.
     *
     * @param dX delta X
     * @param dY delta Y
     * @return valeur de la consigne de distance
     */
    private double calculDistance(double dX, double dY) {
        return Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
    }

    private void gestionFlags() {
        boolean distAtteint, orientAtteint, distApproche, orientApproche;

        // ------------------------------------------------------------------------------- //
        // Calcul du trajet atteints en mode freinage (toujours en DIST,ANGLE ici normale) //
        // ------------------------------------------------------------------------------- //

        distAtteint = Math.abs(cmdRobot.getConsigne().getDistance()) < fenetreArretDistance;
        orientAtteint = Math.abs(cmdRobot.getConsigne().getOrientation()) < fenetreArretOrientation;
        trajetAtteint.set(cmdRobot.isFrein() && distAtteint && orientAtteint);

        // -------------------------------------------------------------------------- //
        // Calcul des fenetres d'approche pour le passage au point suivant sans arret //
        // -------------------------------------------------------------------------- //

        // Si on est en mode déplacement XY, seul la distance d'approche du point est importante.
        if (cmdRobot.isType(TypeConsigne.XY)) {
            // Calcul en fonction de l'odométrie
            long dX = (long) (cmdRobot.getPosition().getPt().getX() - currentPosition.getPt().getX());
            long dY = (long) (cmdRobot.getPosition().getPt().getY() - currentPosition.getPt().getY());

            // On recalcul car la consigne de distance est altéré par le coeficient pour le demi tour
            distApproche = Math.abs(calculDistance(dX, dY)) < fenetreApprocheDistance;
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
                trajetEnApproche.set(true);
            }
        }
    }

    /**
     * Génération d'un déplacement avec le Path Finding
     *
     * @param targetXmm position sur l'axe X
     * @param targetYmm position sur l'axe Y
     * @throws NoPathFoundException
     */
    @Override
    public void pathTo(final double targetXmm, final double targetYmm) throws NoPathFoundException, AvoidingException {
        pathTo(targetXmm, targetYmm, true);
    }


    /**
     * Génération d'un déplacement avec le Path Finding
     *
     * @param targetXmm position sur l'axe X
     * @param targetYmm position sur l'axe Y
     * @param avecArret
     * @throws NoPathFoundException
     */
    @Override
    public void pathTo(final double targetXmm, final double targetYmm, final boolean avecArret) throws NoPathFoundException, AvoidingException {
        boolean trajetOk = false, first = true;
        int nbCollisionDetected = 0, nbTryPath = 1;
        int divisor = 10;

        do {
            Point ptFromCm = new Point(
                    conv.pulseToMm(currentPosition.getPt().getX()) / divisor,
                    conv.pulseToMm(currentPosition.getPt().getY()) / divisor
            );
            Point ptToCm = new Point(targetXmm / divisor, targetYmm / divisor);
            try {
                log.info("Demande de chemin vers X = {}mm ; Y = {}mm", targetXmm, targetYmm);
                Chemin c = pathFinder.findPath(ptFromCm, ptToCm);

                MonitorMouvementPath mPath = new MonitorMouvementPath();
                mPath.setPath(new ArrayList<>(c.getPoints().size() + 1));
                mPath.getPath().add(new Point(
                        conv.pulseToMm(currentPosition.getPt().getX()),
                        conv.pulseToMm(currentPosition.getPt().getY())
                ));
                mPath.getPath().addAll(c.getPoints().stream()
                        .map(point -> point.multiplied(divisor))
                        .collect(Collectors.toList()));
                currentMouvement = mPath;
                monitoring.addMouvementPoint(mPath);

                while (c.hasNext()) {
                    Point targetPoint = c.next().multiplied(divisor);

                    // Processing du path
                    if (first) {
                        first = false;
                        rs.disableAvoidance();
                        alignFrontTo(targetPoint.getX(), targetPoint.getY());
                    }

                    // Toujours activer l'évittement en Path
                    rs.enableAvoidance();

                    // Va au premier point
                    gotoPointMM(targetPoint.getX(), targetPoint.getY(), !c.hasNext() && avecArret, true);
                }

                // Contrôle que l'on est proche de la position demandée
                double dXmm = (targetXmm - conv.pulseToMm(currentPosition.getPt().getX()));
                double dYmm = (targetYmm - conv.pulseToMm(currentPosition.getPt().getY()));
                double targetDistMm = calculDistance(dXmm, dYmm);

                // Trajet ok si il reste moins de 2cm
                trajetOk = targetDistMm <= 20;

                if (!trajetOk) {
                    // Le trajet n'est pas OK
                    nbTryPath++;

                    if (nbTryPath >= 3) {
                        log.warn("Trop de tentative de path, on passe à l'action suivante");
                        throw new AvoidingException();
                    }

                    log.warn("Tentative de path non atteint, on réessai (tentative : {})", nbTryPath);
                    prepareNextMouvement();
                }

            } catch (RefreshPathFindingException e) {
                nbCollisionDetected++;
                log.info("Collision detectée n° {}, on recalcul un autre chemin", nbCollisionDetected);

                if (nbCollisionDetected > 100) {
                    log.warn("Trop de collision ({}), on passe à la suite", nbCollisionDetected);
                    throw new AvoidingException();
                }
            }
        } while (!trajetOk);
    }

    /**
     * Méthode permettant de donner une consigne de position sur un point avec arret sur celui-ci.
     *
     * @param x position sur l'axe X
     * @param y position sur l'axe Y
     */
    @Override
    public void gotoPointMM(final double x, final double y) throws RefreshPathFindingException {
        gotoPointMM(x, y, true, false);
    }

    /**
     * Méthode permettant de donner une consigne de position sur un point
     *
     * @param x         position sur l'axe X
     * @param y         position sur l'axe Y
     * @param avecArret demande d'arret sur le point
     */
    @Override
    public void gotoPointMM(final double x, final double y, final boolean avecArret, boolean disableMonitor) throws RefreshPathFindingException {
        log.info("Va au point X = {}mm ; Y = {}mm {}", x, y, avecArret ? "et arrete toi" : "sans arret");
        cmdRobot.getPosition().setAngle(0);
        cmdRobot.getPosition().getPt().setX(conv.mmToPulse(x));
        cmdRobot.getPosition().getPt().setY(conv.mmToPulse(y));
        cmdRobot.setFrein(avecArret);
        cmdRobot.setTypes(TypeConsigne.XY);

        if (!disableMonitor) {
            double dX = (cmdRobot.getPosition().getPt().getX() - currentPosition.getPt().getX());
            double dY = (cmdRobot.getPosition().getPt().getY() - currentPosition.getPt().getY());
            double distance = calculDistance(dX, dY);

            MonitorMouvementTranslation mTr = new MonitorMouvementTranslation();
            mTr.setFromPoint(new Point(
                    conv.pulseToMm(currentPosition.getPt().getX()),
                    conv.pulseToMm(currentPosition.getPt().getY())
            ));
            mTr.setToPoint(new Point(x, y));
            mTr.setDistance(conv.pulseToMm(distance));
            currentMouvement = mTr;
            monitoring.addMouvementPoint(mTr);
        }

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot sur un angle en fonction du repere
     *
     * @param angle the angle
     */
    @Override
    public void gotoOrientationDeg(final double angle) throws RefreshPathFindingException {
        gotoOrientationDeg(angle, SensRotation.AUTO);
    }

    @Override
    public void gotoOrientationDeg(double angle, SensRotation sensRotation) throws RefreshPathFindingException {
        log.info("Aligne toi sur l'angle {}° du repère dans le sens {}", angle, sensRotation.name());

        double newOrient = calculAngleDelta(conv.pulseToDeg(currentPosition.getAngle()), angle, sensRotation);

        tourneDeg(newOrient);
    }

    /**
     * Méthode permettant d'aligner le robot face a un point
     *
     * @param x the x
     * @param y the y
     */
    @Override
    public void alignFrontTo(final double x, final double y) throws RefreshPathFindingException {
        log.info("Aligne ton avant sur le point X = {}mm ; Y = {}mm", x, y);
        alignFrontToAvecDecalage(x, y, 0);
    }

    /**
     * Alignement sur un point avec un décalage en degré (dans le sens trigo)
     *
     * @param x           position sur l'axe X
     * @param y           position sur l'axe Y
     * @param decalageDeg valeur du déclage angulaire par rapport au point X,Y
     * @throws RefreshPathFindingException
     */
    @Override
    public void alignFrontToAvecDecalage(final double x, final double y, final double decalageDeg) throws RefreshPathFindingException {
        if (decalageDeg != 0) {
            log.info("Décalage de {}° par rapport au point X = {}mm ; Y = {}mm", decalageDeg, x, y);
        }

        double dX = conv.mmToPulse(x) - currentPosition.getPt().getX();
        double dY = conv.mmToPulse(y) - currentPosition.getPt().getY();

        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation((long) (calculAngleConsigne(dX, dY) + conv.degToPulse(decalageDeg)));
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
    @Override
    public void alignBackTo(final double x, final double y) throws RefreshPathFindingException {
        log.info("Aligne ton cul sur le point X = {}mm ; Y = {}mm", x, y);

        double dX = conv.mmToPulse(x) - currentPosition.getPt().getX();
        double dY = conv.mmToPulse(y) - currentPosition.getPt().getY();

        double consOrient = calculAngleConsigne(dX, dY);
        if (consOrient > 0) {
            consOrient -= conv.getPiPulse();
        } else {
            consOrient += conv.getPiPulse();
        }

        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation((long) consOrient);
        cmdRobot.setFrein(true);

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en avant de distance fixe.
     *
     * @param distance the distance
     */
    @Override
    public void avanceMM(final double distance) throws RefreshPathFindingException {
        cmdAvanceMMByType(distance, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    @Override
    public void avanceMMSansAngle(final double distance) throws RefreshPathFindingException {
        cmdAvanceMMByType(distance, TypeConsigne.DIST);
    }

    private void cmdAvanceMMByType(final double distance, TypeConsigne... types) throws RefreshPathFindingException {
        if (distance > 0) {
            log.info("{} de {}mm en mode : {}", distance > 0 ? "Avance" : "Recul", distance, StringUtils.join(types, ", "));
        }

        cmdRobot.setTypes(types);
        cmdRobot.getConsigne().setDistance((long) conv.mmToPulse(distance));
        cmdRobot.getConsigne().setOrientation(0);
        cmdRobot.setFrein(true);

        MonitorMouvementTranslation mTr = new MonitorMouvementTranslation();
        mTr.setDistance(distance);
        mTr.setFromPoint(new Point(
                conv.pulseToMm(currentPosition.getPt().getX()),
                conv.pulseToMm(currentPosition.getPt().getY())
        ));
        mTr.setToPoint(tableUtils.getPointFromAngle(distance, 0));
        currentMouvement = mTr;
        monitoring.addMouvementPoint(mTr);

        prepareNextMouvement();
        waitMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en arriere de distance fixe
     *
     * @param distance the distance
     */
    @Override
    public void reculeMM(final double distance) throws RefreshPathFindingException {
        log.info("Recul de {}mm", Math.abs(distance));
        avanceMM(-distance);
    }

    @Override
    public void reculeMMSansAngle(final double distance) throws RefreshPathFindingException {
        log.info("Recul de {}mm sans angle", Math.abs(distance));
        avanceMMSansAngle(-distance);
    }

    /**
     * Méthode permettant d'effectuer une rotation d'angle fixe
     *
     * @param angle the angle
     */
    @Override
    public void tourneDeg(final double angle) throws RefreshPathFindingException {
        log.info("Tourne de {}°", angle);

        boolean isAvoidance = rs.isAvoidanceEnabled();
        try {
            rs.disableAvoidance();
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation((long) conv.degToPulse(angle));
            cmdRobot.setFrein(true);

            MonitorMouvementRotation mRot = new MonitorMouvementRotation();
            mRot.setAngle(angle);
            mRot.setFromAngle(conv.pulseToDeg(currentPosition.getAngle()));
            mRot.setToAngle(angle + mRot.getFromAngle());
            currentMouvement = mRot;
            monitoring.addMouvementPoint(mRot);

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
    @Override
    public void followLine(final double x1, final double y1, final double x2, final double y2) throws RefreshPathFindingException {
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
    @Override
    public void turnAround(final double x, final double y, final double r) throws RefreshPathFindingException {
        // TODO : A implémenter la commande
        throw new NotYetImplementedException();
    }

    /**
     * Méthode pour préparer le prochain mouvement.
     */
    private void prepareNextMouvement() {
        // Reset de l'erreur de l'asservissementPolaire sur le mouvement précédent lorsqu'il
        // s'agit d'un nouveau mouvement au départ vitesse presque nulle.
        if (trajetAtteint.get()) {
            asservissementPolaire.reset();
        }

        // Réinitialisation des infos de trajet.
        trajetAtteint.set(false);
        trajetEnApproche.set(false);
        refreshPath.set(false);
        obstacleFound.set(false);
        calageBordure.set(false);
    }

    /**
     * Définition des vitesses de déplacement sur les deux axes du robot.
     *
     * @param vDistance    vitesse pour la boucle distance
     * @param vOrientation vitesse pour la boucle orientation
     */
    @Override
    public void setVitesse(long vDistance, long vOrientation) {
        cmdRobot.getVitesse().setDistance(vDistance);
        cmdRobot.getVitesse().setOrientation(vOrientation);
    }

    /**
     * Permet d'attendre le passage au point suivant
     */
    @Override
    public void waitMouvement() throws RefreshPathFindingException {
        if (cmdRobot.isFrein()) {
            log.info("Attente fin de trajet");
            while (!trajetAtteint.get()) {
                try {
                    checkRefreshPath();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Problème dans l'attente d'atteinte du point : {}", e.toString());
                }
            }
            log.info("Trajet atteint");
        } else {
            log.info("Attente approche du point de passage");
            while (!trajetEnApproche.get()) {
                try {
                    checkRefreshPath();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Problème dans l'approche du point : {}", e.toString());
                }
            }
            log.info("Point de passage atteint");
        }
    }

    @Override
    public void obstacleFound() {
        obstacleFound.set(true);
    }

    @Override
    public void obstacleNotFound() {
        obstacleFound.set(false);
    }

    @Override
    public void calageBordureDone() {
        calageBordure.set(true);
    }

    @Override
    public void refreshPathFinding() {
        refreshPath.set(true);
    }

    private void checkRefreshPath() throws RefreshPathFindingException {
        if (refreshPath.get()) {
            refreshPath.set(false);
            throw new RefreshPathFindingException();
        }
    }
}
