package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.filters.common.SignalEdgeFilter;
import org.arig.robot.model.*;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.arig.robot.model.monitor.MonitorMouvementPath;
import org.arig.robot.model.monitor.MonitorMouvementRotation;
import org.arig.robot.model.monitor.MonitorMouvementTranslation;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.system.motion.IOdometrie;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.pathfinding.PathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TrajectoryManager {

    @Autowired
    private RobotConfig robotConfig;

    @Autowired
    private IOdometrie odom;

    @Autowired
    private MonitoringWrapper monitoring;

    @Autowired
    private IAsservissementPolaire asservissementPolaire;

    @Autowired
    private Abstract2WheelsEncoders encoders;

    @Autowired
    private AbstractPropulsionsMotors propulsionsMotors;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private PathFinder pathFinder;

    @Autowired
    private LidarService lidarService;

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
    protected MonitoringWrapper monitoringWrapper;

    @Getter
    private AbstractMonitorMouvement currentMouvement = null;

    private AtomicLong vitesseDistance = new AtomicLong(0);
    private AtomicLong vitesseOrientation = new AtomicLong(0);

    private AtomicBoolean trajetAtteint = new AtomicBoolean(false);

    private AtomicBoolean trajetEnApproche = new AtomicBoolean(false);

    /**
     * Boolean si un obstacle est rencontré (stop le robot sur place)
     **/
    private AtomicBoolean obstacleFound = new AtomicBoolean(false);

    /**
     * Boolean si un obstacle est proche pour réduire la vitesse
     */
    private AtomicBoolean obstacleLowSpeed = new AtomicBoolean(false);

    /**
     * Boolean si un calage bordure est demandé (consigne distance angle = 0)
     */
    private AtomicBoolean calageBordure = new AtomicBoolean(false);

    /**
     * Boolean pour relancer après un obstacle (gestion de l'évittement)
     */
    private AtomicBoolean refreshPath = new AtomicBoolean(false);

    private AtomicBoolean cancelMouvement = new AtomicBoolean(false);

    private SignalEdgeFilter obstacleFoundFilter = new SignalEdgeFilter(false, SignalEdgeFilter.Type.RISING);
    private SignalEdgeFilter obstacleNotFoundFilter = new SignalEdgeFilter(false, SignalEdgeFilter.Type.FALLING);

    @PostConstruct
    public void postConstruct() {
        log.info("Fenetre arret distance                  : {} pulse -> {} mm", robotConfig.fenetreArretDistance(), conv.pulseToMm(robotConfig.fenetreArretDistance()));
        log.info("Fenetre approche distance avec frein    : {} pulse -> {} mm", robotConfig.fenetreApprocheAvecFreinDistance(), conv.pulseToMm(robotConfig.fenetreApprocheAvecFreinDistance()));
        log.info("Fenetre approche distance sans frein    : {} pulse -> {} mm", robotConfig.fenetreApprocheSansFreinDistance(), conv.pulseToMm(robotConfig.fenetreApprocheSansFreinDistance()));
        log.info("Fenetre arret orientation               : {} pulse -> {} °", robotConfig.fenetreArretOrientation(), conv.pulseToMm(robotConfig.fenetreArretOrientation()));
        log.info("Fenetre approche orientation avec frein : {} pulse -> {} °", robotConfig.fenetreApprocheAvecFreinOrientation(), conv.pulseToMm(robotConfig.fenetreApprocheAvecFreinOrientation()));
        log.info("Fenetre approche orientation sans frein : {} pulse -> {} °", robotConfig.fenetreApprocheSansFreinOrientation(), conv.pulseToMm(robotConfig.fenetreApprocheSansFreinOrientation()));
        log.info("Angle de démarrage en demi tour         : {} pulse -> {} °", robotConfig.startAngleDemiTour(), conv.pulseToDeg(robotConfig.startAngleDemiTour()));
        log.info("Angle d'ajustement vitesse déplacement  : {} pulse -> {} °", robotConfig.startAngleLimitSpeedDistance(), conv.pulseToDeg(robotConfig.startAngleLimitSpeedDistance()));
    }

    public boolean isTrajetAtteint() {
        return trajetAtteint.get();
    }

    public boolean isTrajetEnApproche() {
        return trajetEnApproche.get();
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
        asservissementPolaire.reset(true);
    }

    public double currentXMm() {
        return conv.pulseToMm(currentPosition.getPt().getX());
    }

    public double currentYMm() {
        return conv.pulseToMm(currentPosition.getPt().getY());
    }

    public double currentAngleDeg() {
        return conv.pulseToDeg(currentPosition.getAngle());
    }

    /**
     * Process. Cette méthode permet de réaliser les fonctions lié aux déplacements.
     *
     * @param timeStepMs
     */
    public void process(final long timeStepMs) {
        synchronized (this) {
            // 1. Calcul de la position du robot
            encoders.lectureValeurs();

            long tStart = System.nanoTime();
            odom.calculPosition();

            if (rs.waitTirette()) {
                propulsionsMotors.generateMouvement(0, 0);
                return;
            }

            long tCalcul1 = System.nanoTime();
            long tCalcul2 = tCalcul1;
            long tAsserv = tCalcul1;

            // 2. Gestion de l'evittement
            // 2.A. Calcul des consignes
            calculConsigne();

            tCalcul2 = System.nanoTime();

            // 2.B Asservissement sur les consignes
            asservissementPolaire.process(timeStepMs, obstacleFound.get());

            tAsserv = System.nanoTime();

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
    }

    ConsigneAsservissementPolaire oldConsigne;
    TypeConsigne[] oldTypeConsigne;

    /**
     * Calcul des consignes d'asservissement
     * -> a : Gestion en fonction de l'odométrie
     * -> b : Si dans fenêtre d'approche : consigne(n) = consigne(n - 1) - d(position)
     */
    private void calculConsigne() {
        if (Boolean.TRUE.equals(obstacleFoundFilter.filter(obstacleFound.get()))) {
            log.info("Sauvegarde des vieilles consignes sur detection d'obstacle");
            oldConsigne = cmdRobot.getConsigne();
            oldTypeConsigne = cmdRobot.getTypes();
        }

        if (calageBordure.get() || obstacleFound.get()) {
            // Un calage sur bordure est fait. On asservi sur place jusqu'au prochain mouvement
            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation(0);
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
            calageBordure.set(false);
        }

        if (Boolean.TRUE.equals(obstacleNotFoundFilter.filter(obstacleFound.get())) && oldConsigne != null) {
            log.info("Restauration des vieilles consignes sur disparition d'obstacle");
            cmdRobot.setConsigne(oldConsigne);
            cmdRobot.setTypes(oldTypeConsigne);
        }

        if (!trajetAtteint.get() && cmdRobot.isType(TypeConsigne.XY)) {
            // Calcul en fonction de l'odométrie
            double dX = (cmdRobot.getPosition().getPt().getX() - currentPosition.getPt().getX());
            double dY = (cmdRobot.getPosition().getPt().getY() - currentPosition.getPt().getY());

            // Calcul des consignes
            double consDist = calculDistance(dX, dY);
            double consOrient = calculAngleConsigne(dX, dY);

            switch (cmdRobot.getSensDeplacement()) {
                case DEMI_TOUR:
                    if (Math.abs(consOrient) > robotConfig.startAngleDemiTour()) {
                        // Calcul du coef d'annulation de la distance
                        // Permet d'effectuer un demi tour en 3 temps.
                        consDist = (consDist * ((robotConfig.startAngleDemiTour() - Math.abs(consOrient)) / robotConfig.startAngleDemiTour()));

                    }
                    break;

                case ARRIERE:
                case AUTO:
                    if (cmdRobot.getSensDeplacement() == SensDeplacement.ARRIERE || Math.abs(consOrient) > conv.degToPulse(90)) {
                        // Atteindre la destination en arrière
                        consDist = -consDist;
                        consOrient = conv.degToPulse(ajusteAngleDeg(180 + conv.pulseToDeg(consOrient)));
                    }
                    break;

            }

            // Limite de la vitesse de déplacement en fonction de l'angle du prochain point.
            // On favorise la rotation pour l'enchainement des points
            if (Math.abs(consOrient) > robotConfig.startAngleLimitSpeedDistance()) {
                applyVitesse(0, vitesseOrientation.get());
            } else {
                double coef = (robotConfig.startAngleLimitSpeedDistance() - Math.abs(consOrient)) / robotConfig.startAngleLimitSpeedDistance();
                applyVitesse((long) (coef * vitesseDistance.get()), vitesseOrientation.get());
            }

            // Sauvegarde des consignes
            cmdRobot.getConsigne().setDistance((long) consDist);
            cmdRobot.getConsigne().setOrientation((long) consOrient);

        } else if (!trajetAtteint.get() && cmdRobot.isType(TypeConsigne.LINE)) {
            // TODO : Consigne de suivi de ligne (gérer les clothoïde pour la liaison)

        } else if (!trajetAtteint.get() && cmdRobot.isType(TypeConsigne.CIRCLE)) {
            // TODO : Consigne de rotation autour d'un point.

        } else {
            applyVitesse(vitesseDistance.get(), vitesseOrientation.get());

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
    private double ajusteAngleDeg(double angle) {
        if (angle > 180) {
            return ajusteAngleDeg(angle - 360);
        } else if (angle < -180) {
            return ajusteAngleDeg(angle + 360);
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
        angle = ajusteAngleDeg(angle);

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
                        .min((a, b) -> Double.valueOf(Math.abs(a) - Math.abs(b)).intValue())
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
        // Si on est en cours de gestion d'évitement, on ne raffraichit pas les flags.
        // Si on les rafraichit, on va indiquer que l'on est en trajet atteint, hors ce n'est pas le cas.
        if (obstacleFound.get()) {
            return;
        }

        boolean distAtteint, orientAtteint, distApproche, orientApproche;
        // ------------------------------------------------------------------------------- //
        // Calcul du trajet atteints en mode freinage (toujours en DIST,ANGLE ici normale) //
        // ------------------------------------------------------------------------------- //

        distAtteint = Math.abs(cmdRobot.getConsigne().getDistance()) < robotConfig.fenetreArretDistance();
        orientAtteint = Math.abs(cmdRobot.getConsigne().getOrientation()) < robotConfig.fenetreArretOrientation();
        trajetAtteint.set(cmdRobot.isFrein() && distAtteint && orientAtteint);

        // ---------------------------------------------------------------------------------- //
        // Calcul des fenetres d'approche pour le passage au point suivant avec ou sans arret //
        // ---------------------------------------------------------------------------------- //

        final double fenetreApprocheDistance;
        final double fenetreApprocheOrientation;
        if (cmdRobot.isFrein()) {
            fenetreApprocheDistance = robotConfig.fenetreApprocheAvecFreinDistance();
            fenetreApprocheOrientation = robotConfig.fenetreApprocheAvecFreinOrientation();
        } else {
            //fenetreApprocheDistance = trajectoryManagerConfig.getFenetreApprocheSansFreinDistance();
            fenetreApprocheDistance = (long) (conv.mmToPulse(vitesseDistance()) * robotConfig.sampleTimeS() * 4);
            fenetreApprocheOrientation = robotConfig.fenetreApprocheSansFreinOrientation();
        }

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

        // Lorsque l'on est dans la fenêtre d'approche on bascule l'asserve en mode basique (distance, angle)
        // Si on ne fait pas ça on obtient une spirale sur le point d'arrivé qui est jolie mais pas très pratique
        if (distApproche && orientApproche) {
            // Modification du type de consigne pour la stabilisation
            cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);
            cmdRobot.setBypassRampDistance(true);
            cmdRobot.setBypassRampOrientation(true);

            // Notification que le point de passage est atteint uniquement lors d'un enchainement sans arret
            if (!cmdRobot.isFrein()) {
                trajetEnApproche.set(true);
            }
        }
    }

    public void pathTo(final Point pt, final GotoOption... flags) throws NoPathFoundException, AvoidingException {
        pathTo(pt.getX(), pt.getY(), flags);
    }

    public void pathTo(final double targetXmm, final double targetYmm, final GotoOption... flags) throws NoPathFoundException, AvoidingException {
        final EnumSet<GotoOption> options = getMvtOptions(flags);

        rs.enableAvoidance();
        if (!lidarService.waitCleanup()) {
            throw new AvoidingException("Timeout du lidar");
        }

        boolean trajetOk = false;
        int nbCollisionDetected = 0, nbTryPath = 1;
        int divisor = 10;

        do {
            Point ptFromCm = new Point(currentXMm() / divisor, currentYMm() / divisor);
            Point ptToCm = new Point(targetXmm / divisor, targetYmm / divisor);
            try {
                // si c'est une nouvelle tentative et qu'on est dans le noir, on recule ou avance selon le sens déplacement
                if (nbCollisionDetected > 0 && pathFinder.isBlockedByObstacle(ptFromCm)) {
                    if (degagementPath(targetXmm, targetYmm)) {
                        ptFromCm = new Point(currentXMm() / divisor, currentYMm() / divisor);
                    } else {
                        log.warn("Impossible de se dégager de l'obstacle");
                        throw new AvoidingException();
                    }
                }

                log.info("Demande de chemin vers X = {}mm ; Y = {}mm", targetXmm, targetYmm);
                Chemin c = pathFinder.findPath(ptFromCm, ptToCm);

                MonitorMouvementPath mPath = new MonitorMouvementPath();
                mPath.setPath(new ArrayList<>(c.getPoints().size() + 1));
                mPath.getPath().add(new Point(currentXMm(), currentYMm()));
                mPath.getPath().addAll(c.getPoints().stream()
                        .map(point -> point.multiplied(divisor))
                        .collect(Collectors.toList()));
                currentMouvement = mPath;
                monitoring.addMouvementPoint(mPath);

                boolean firstPoint = true;
                while (c.hasNext()) {
                    final Point targetPoint = c.next().multiplied(divisor);

                    // Alignement en rotation sur le premier point, puis enchainement sans freinage jusqu'au dernier
                    final EnumSet<GotoOption> localOptions = options.clone();
                    localOptions.add(GotoOption.NO_CATCH_REFRESH_PATH);

                    // Si on est avec l'option SANS_ARRET_PASSAGE_ONLY_PATH, et que ce n'est pas le dernier point.
                    // On réalise un enchainement de point avec frein sur le dernier
                    if (c.hasNext() && options.contains(GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH)) {
                        localOptions.add(GotoOption.SANS_ARRET);
                    }

                    // Ne pas faire d'orientation entre les points.
                    // Uniquement le premier point est avec sur un path
                    if (!firstPoint) {
                        localOptions.add(GotoOption.SANS_ORIENTATION);
                    }
                    gotoPoint(targetPoint.getX(), targetPoint.getY(), localOptions);

                    // Après un point de passage ce n'est plus le premier point
                    firstPoint = false;
                }

                if (options.contains(GotoOption.SANS_ARRET)) {
                    trajetOk = true;

                } else {
                    // Contrôle que l'on est proche de la position demandée
                    double dXmm = (targetXmm - currentXMm());
                    double dYmm = (targetYmm - currentYMm());
                    double targetDistMm = calculDistance(dXmm, dYmm);

                    // Trajet ok si il reste moins de 2cm avec le freinage, sinon le dernier point n'est pas la fin
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
                }

            } catch (RefreshPathFindingException e) {
                nbCollisionDetected++;
                log.info("Collision detectée n° {}, on recalcul un autre chemin", nbCollisionDetected);

                if (nbCollisionDetected > 10) {
                    log.warn("Trop de collision ({}), on passe à la suite", nbCollisionDetected);
                    throw new AvoidingException();
                }
            }
        } while (!trajetOk);
    }

    /**
     * Dégagement d'adversaire pendant un path
     */
    private boolean degagementPath(final double targetXmm, final double targetYmm) throws AvoidingException {
        // direction générale du déplacement
        double dX = conv.mmToPulse(targetXmm) - currentPosition.getPt().getX();
        double dY = conv.mmToPulse(targetYmm) - currentPosition.getPt().getY();
        double angle = calculAngleConsigne(dX, dY);

        int dir = 1;
        if (Math.abs(angle) < conv.degToPulse(90)) {
            dir = -1;
        }

        // vérifie qu'un point correspondant au bord du robot est toujours dans la table
        Point pointTest = new Point();
        pointTest.setX(dir * 250 * Math.cos(Math.toRadians(currentAngleDeg())));
        pointTest.setY(dir * 250 * Math.sin(Math.toRadians(currentAngleDeg())));
        pointTest.addDeltaX(currentXMm());
        pointTest.addDeltaY(currentYMm());

        if (tableUtils.isInPhysicalTable(pointTest)) {
            log.info("Dégagement d'obstacle");
            avanceMM(dir * 100);
            return true;
        }

        return false;
    }

    private EnumSet<GotoOption> getMvtOptions(final GotoOption... flags) {
        final EnumSet<GotoOption> options = EnumSet.noneOf(GotoOption.class);
        options.addAll(Arrays.asList(flags));
        return options;
    }

    public void gotoPoint(final Point pt, final GotoOption... flags) throws AvoidingException {
        gotoPoint(pt.getX(), pt.getY(), getMvtOptions(flags));
    }

    /**
     * Méthode permettant de donner une consigne de position sur un point
     *
     * @param x     position sur l'axe X
     * @param y     position sur l'axe Y
     * @param flags options de déplacement
     */
    public void gotoPoint(final double x, final double y, final GotoOption... flags) throws AvoidingException {
        gotoPoint(x, y, getMvtOptions(flags));
    }

    private void gotoPoint(final double x, final double y, final EnumSet<GotoOption> options) throws AvoidingException {
        log.info("Va au point X = {}mm ; Y = {}mm {}", x, y, !options.contains(GotoOption.SANS_ARRET) ? "et arrete toi" : "sans arret");

        if (!options.contains(GotoOption.SANS_ORIENTATION)) {
            boolean avoidanceEnabled = rs.avoidanceEnabled();
            boolean avoidanceLong = rs.avoidanceLong();

            // Alignement sur le point
            if (avoidanceEnabled) {
                rs.disableAvoidance();
            }

            double dX = conv.mmToPulse(x) - currentPosition.getPt().getX();
            double dY = conv.mmToPulse(y) - currentPosition.getPt().getY();
            double angle = calculAngleConsigne(dX, dY);
            final SensDeplacement sensAuto = Math.abs(angle) < conv.degToPulse(90) ? SensDeplacement.AVANT : SensDeplacement.ARRIERE;

            // si on est bord de la table on ne peut pas tourner sur place
            // => on tourne en glissant, on avance un peu, puis on enchaine sur le mouvement normal
            final Point currentCm = new Point(
                    conv.pulseToMm(currentPosition.getPt().getX()) / 10,
                    conv.pulseToMm(currentPosition.getPt().getY()) / 10
            );
            if (pathFinder.isBordureTable(currentCm)) {
                if (sensAuto == SensDeplacement.ARRIERE) {
                    alignBackToSansDistance(x, y);
                    reculeMM(30);
                } else {
                    alignFrontToSansDistance(x, y);
                    avanceMM(30);
                }
            }

            final SensDeplacement realSens;
            if (options.contains(GotoOption.AVANT)) {
                realSens = SensDeplacement.AVANT;
            } else if (options.contains(GotoOption.ARRIERE)) {
                realSens = SensDeplacement.ARRIERE;
            } else {
                realSens = sensAuto;
            }

            if (realSens == SensDeplacement.ARRIERE) {
                alignBackTo(x, y);
            } else {
                alignFrontTo(x, y);
            }

            // Retour de l'évittement si actif avant
            if (avoidanceEnabled) {
                rs.enableAvoidance(avoidanceLong);
            }
        }

        synchronized (this) {
            cmdRobot.getPosition().setAngle(0);
            cmdRobot.getPosition().getPt().setX(conv.mmToPulse(x));
            cmdRobot.getPosition().getPt().setY(conv.mmToPulse(y));
            cmdRobot.setFrein(!options.contains(GotoOption.SANS_ARRET));
            cmdRobot.setBypassRampDistance(false);
            cmdRobot.setBypassRampOrientation(true);
            cmdRobot.setTypes(TypeConsigne.XY);
            cmdRobot.setSensDeplacement(options.contains(GotoOption.AVANT) ? SensDeplacement.AVANT : options.contains(GotoOption.ARRIERE) ? SensDeplacement.ARRIERE : SensDeplacement.AUTO);

            prepareNextMouvement();
        }

        if (options.contains(GotoOption.NO_CATCH_REFRESH_PATH)) {
            waitMouvement();
        } else {
            try {
                waitMouvement();
            } catch (RefreshPathFindingException e) {
                throw new AvoidingException(e);
            }
        }
    }

    /**
     * Méthode permettant d'aligner le robot sur un angle en fonction du repere
     *
     * @param angle the angle
     */
    public void gotoOrientationDeg(final double angle) throws AvoidingException {
        gotoOrientationDeg(angle, SensRotation.AUTO);
    }

    public void gotoOrientationDeg(double angle, SensRotation sensRotation) throws AvoidingException {
        gotoOrientationDegByType(angle, sensRotation, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void gotoOrientationDegSansDistance(final double angle) throws AvoidingException {
        gotoOrientationDegSansDistance(angle, SensRotation.AUTO);
    }

    public void gotoOrientationDegSansDistance(final double angle, final SensRotation sensRotation) throws AvoidingException {
        gotoOrientationDegByType(angle, sensRotation, TypeConsigne.ANGLE);
    }

    private void gotoOrientationDegByType(double angle, SensRotation sensRotation, TypeConsigne... types) throws AvoidingException {
        log.info("Aligne toi sur l'angle {}° du repère dans le sens {}", angle, sensRotation.name());

        double newOrient = calculAngleDelta(currentAngleDeg(), angle, sensRotation);

        tourneDegByType(newOrient, types);
    }

    /**
     * Méthode permettant d'aligner le robot face a un point
     */
    public void alignFrontTo(final Point point) throws AvoidingException {
        alignFrontTo(point.getX(), point.getY());
    }

    public void alignFrontTo(final double x, final double y) throws AvoidingException {
        log.info("Aligne ton avant sur le point X = {}mm ; Y = {}mm", x, y);
        cmdAlignFrontToByType(x, y, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void alignFrontToSansDistance(final double x, final double y) throws AvoidingException {
        log.info("Aligne ton avant sur le point X = {}mm ; Y = {}mm ; sans distance", x, y);
        cmdAlignFrontToByType(x, y, TypeConsigne.ANGLE);
    }

    private void cmdAlignFrontToByType(final double x, final double y, final TypeConsigne... typeConsignes) throws AvoidingException {
        synchronized (this) {
            double dX = conv.mmToPulse(x) - currentPosition.getPt().getX();
            double dY = conv.mmToPulse(y) - currentPosition.getPt().getY();

            cmdRobot.setTypes(typeConsignes);
            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation((long) calculAngleConsigne(dX, dY));
            cmdRobot.setFrein(true);
            cmdRobot.setBypassRampDistance(true);
            cmdRobot.setBypassRampOrientation(false);

            prepareNextMouvement();
        }

        waitMouvement();
    }

    /**
     * Méthode permettant d'aligner le robot dos a un point
     */
    public void alignBackTo(final Point point) throws AvoidingException {
        alignBackTo(point.getX(), point.getY());
    }

    public void alignBackTo(final double x, final double y) throws AvoidingException {
        log.info("Aligne ton cul sur le point X = {}mm ; Y = {}mm", x, y);
        cmdAlignBackToByType(x, y, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void alignBackToSansDistance(final double x, final double y) throws AvoidingException {
        log.info("Aligne ton cul sur le point X = {}mm ; Y = {}mm ; sans distance", x, y);
        cmdAlignBackToByType(x, y, TypeConsigne.ANGLE);
    }

    private void cmdAlignBackToByType(final double x, final double y, final TypeConsigne... typeConsignes) throws AvoidingException {
        synchronized (this) {
            double dX = conv.mmToPulse(x) - currentPosition.getPt().getX();
            double dY = conv.mmToPulse(y) - currentPosition.getPt().getY();

            double consOrient = calculAngleConsigne(dX, dY);
            if (consOrient > 0) {
                consOrient -= conv.piPulse();
            } else {
                consOrient += conv.piPulse();
            }

            cmdRobot.setTypes(typeConsignes);
            cmdRobot.getConsigne().setDistance(0);
            cmdRobot.getConsigne().setOrientation((long) consOrient);
            cmdRobot.setFrein(true);
            cmdRobot.setBypassRampDistance(true);
            cmdRobot.setBypassRampOrientation(false);

            prepareNextMouvement();
        }

        waitMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en avant de distance fixe.
     *
     * @param distance the distance
     */
    public void avanceMM(final double distance) throws AvoidingException {
        cmdAvanceMMByType(distance, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void avanceMMSansAngle(final double distance) throws AvoidingException {
        cmdAvanceMMByType(distance, TypeConsigne.DIST);
    }

    private void cmdAvanceMMByType(final double distance, TypeConsigne... types) throws AvoidingException {
        if (distance > 0) {
            log.info("{} de {}mm en mode : {}", distance > 0 ? "Avance" : "Recul", distance, StringUtils.join(types, ", "));
        }

        synchronized (this) {
            cmdRobot.setTypes(types);
            cmdRobot.getConsigne().setDistance((long) conv.mmToPulse(distance));
            cmdRobot.getConsigne().setOrientation(0);
            cmdRobot.setFrein(true);
            cmdRobot.setBypassRampDistance(false);
            cmdRobot.setBypassRampOrientation(true);

            MonitorMouvementTranslation mTr = new MonitorMouvementTranslation();
            mTr.setDistance(distance);
            mTr.setFromPoint(new Point(currentXMm(), currentYMm()));
            mTr.setToPoint(tableUtils.getPointFromAngle(distance, 0));
            currentMouvement = mTr;
            monitoring.addMouvementPoint(mTr);

            prepareNextMouvement();
        }

        waitMouvement();
    }

    /**
     * Méthode permettant d'effectuer un déplacement en arriere de distance fixe
     *
     * @param distance the distance
     */
    public void reculeMM(final double distance) throws AvoidingException {
        log.info("Recul de {}mm", Math.abs(distance));
        avanceMM(-distance);
    }

    public void reculeMMSansAngle(final double distance) throws AvoidingException {
        log.info("Recul de {}mm sans angle", Math.abs(distance));
        avanceMMSansAngle(-distance);
    }

    /**
     * Méthode permettant d'effectuer une rotation d'angle fixe
     *
     * @param angle the angle
     */
    public void tourneDeg(final double angle) throws AvoidingException {
        tourneDegByType(angle, TypeConsigne.DIST, TypeConsigne.ANGLE);
    }

    public void tourneDegSansDistance(final double angle) throws AvoidingException {
        tourneDegByType(angle, TypeConsigne.ANGLE);
    }

    private void tourneDegByType(final double angle, TypeConsigne... types) throws AvoidingException {
        log.info("Tourne de {}° en mode : {}", angle, StringUtils.join(types, ", "));

        boolean avoidanceEnabled = rs.avoidanceEnabled();
        boolean avoidanceLong = rs.avoidanceLong();
        try {
            synchronized (this) {
                rs.disableAvoidance();
                cmdRobot.setTypes(types);
                cmdRobot.getConsigne().setDistance(0);
                cmdRobot.getConsigne().setOrientation((long) conv.degToPulse(angle));
                cmdRobot.setFrein(true);
                cmdRobot.setBypassRampDistance(true);
                cmdRobot.setBypassRampOrientation(false);

                MonitorMouvementRotation mRot = new MonitorMouvementRotation();
                mRot.setAngle(angle);
                mRot.setFromAngle(currentAngleDeg());
                mRot.setToAngle(angle + mRot.getFromAngle());
                currentMouvement = mRot;
                monitoring.addMouvementPoint(mRot);

                prepareNextMouvement();
            }

            waitMouvement();
        } finally {
            if (avoidanceEnabled) {
                rs.enableAvoidance(avoidanceLong);
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
    public void followLine(final double x1, final double y1, final double x2, final double y2) {
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
    public void turnAround(final double x, final double y, final double r) {
        // TODO : A implémenter la commande
        throw new NotYetImplementedException();
    }

    /**
     * Méthode pour préparer le prochain mouvement.
     */
    private void prepareNextMouvement() {
        oldConsigne = null;
        oldTypeConsigne = null;

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

    public long vitesseDistance() {
        return this.vitesseDistance.get();
    }

    public long vitesseOrientation() {
        return this.vitesseOrientation.get();
    }

    /**
     * Définition des vitesses de déplacement sur les deux axes du robot.
     *
     * @param vDistance    vitesse pour la boucle distance
     * @param vOrientation vitesse pour la boucle orientation
     */
    public void setVitesse(long vDistance, long vOrientation) {
        this.vitesseDistance.set(vDistance);
        this.vitesseOrientation.set(vOrientation);
        asservissementPolaire.setRampDistance(robotConfig.rampeAccelDistance(), robotConfig.rampeDecelDistance());
        asservissementPolaire.setRampOriantation(robotConfig.rampeAccelOrientation(), robotConfig.rampeDecelOrientation());
        applyVitesse(vDistance, vOrientation);
    }

    public void setRampesDistance(double rampeAccel, double rampeDecel) {
        asservissementPolaire.setRampDistance(rampeAccel, rampeDecel);
    }

    public void setRampeOrientation(double rampeAccel, double rampeDecel) {
        asservissementPolaire.setRampOriantation(rampeAccel, rampeDecel);
    }

    public void setLowSpeed(final boolean lowSpeed) {
        this.obstacleLowSpeed.set(lowSpeed);
    }

    private void applyVitesse(long vDistance, long vOrientation) {
        synchronized (this) {
            cmdRobot.getVitesse().setDistance(obstacleLowSpeed.get() ? vDistance / 3 : vDistance);
            cmdRobot.getVitesse().setOrientation(vOrientation);
        }
    }

    /**
     * Permet d'attendre le passage au point suivant
     */
    public void waitMouvement() throws AvoidingException {
        if (cmdRobot.isFrein()) {
            log.info("Attente fin de trajet");
            while (!trajetAtteint.get()) {
                try {
                    checkRefreshPath();
                    checkCancelMouvement();
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
                    checkCancelMouvement();
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    log.error("Problème dans l'approche du point : {}", e.toString());
                }
            }
            log.info("Point de passage atteint");
        }
    }

    public void obstacleFound() {
        obstacleFound.set(true);
    }

    public void obstacleNotFound() {
        obstacleFound.set(false);
    }

    public void calageBordureDone() {
        calageBordure.set(true);
        rs.disableCalageBordure();
    }

    public void refreshPathFinding() {
        oldConsigne = null;
        oldTypeConsigne = null;
        refreshPath.set(true);
    }

    public void cancelMouvement() {
        cmdRobot.getConsigne().setDistance(0);
        cmdRobot.getConsigne().setOrientation(0);
        cmdRobot.setTypes(TypeConsigne.DIST, TypeConsigne.ANGLE);

        cancelMouvement.set(true);
    }

    private void checkRefreshPath() throws RefreshPathFindingException {
        if (refreshPath.get()) {
            refreshPath.set(false);
            throw new RefreshPathFindingException();
        }
    }

    private void checkCancelMouvement() throws MovementCancelledException {
        if (cancelMouvement.get()) {
            cancelMouvement.set(false);

            if (rs.calage().contains(TypeCalage.FORCE)) {
                rs.calageCompleted().add(TypeCalage.FORCE);
                calageBordureDone();
            } else {
                throw new MovementCancelledException();
            }
        }
    }
}
