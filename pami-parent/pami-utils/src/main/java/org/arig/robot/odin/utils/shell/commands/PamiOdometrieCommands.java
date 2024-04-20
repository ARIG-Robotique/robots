package org.arig.robot.odin.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.PamiIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@ShellCommandGroup("Odométrie")
@RequiredArgsConstructor
public class PamiOdometrieCommands {

    private static final String LOG_SEPARATOR = "-------------------------------------";
    private static final String LOG_CYCLE = "Cycle {} / {}";

    private static final double DISTANCE_TABLE = 2997; // Table Greg 2022

    private final MonitoringWrapper monitoringWrapper;
    private final PamiIOService ioService;
    private final AbstractEnergyService energyService;
    private final TrajectoryManager trajectoryManager;
    private final PamiRobotStatus rs;
    private final Abstract2WheelsEncoders encoders;
    private final ConvertionRobotUnit convRobot;
    private final Position currentPosition;

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @SneakyThrows
    @ShellMethod("Réglage coef roue")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieCoefRoue(int nbCycle) {
        encoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        rs.enableCalageBordure(TypeCalage.ARRIERE);
        trajectoryManager.setVitesse(100, 1000);
        trajectoryManager.reculeMMSansAngle(1000);
        trajectoryManager.setVitesse(100, 100);
        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
        for (int cycle = 0 ; cycle < nbCycle ; cycle++) {
            log.info(LOG_CYCLE, cycle + 1, nbCycle);
            trajectoryManager.setVitesse(500, 1000);
            trajectoryManager.avanceMM(cycle == 0 ? 1000 : 900);
            trajectoryManager.setVitesse(1000, 500);
            trajectoryManager.tourneDeg(180);
            trajectoryManager.setVitesse(500, 1000);
            trajectoryManager.avanceMM(900);
            trajectoryManager.setVitesse(1000, 500);
            trajectoryManager.tourneDeg(-180);
        }
        rs.enableCalageBordure(TypeCalage.ARRIERE);
        trajectoryManager.setVitesse(100, 1000);
        trajectoryManager.reculeMM(100);
        rs.enableCalageBordure(TypeCalage.ARRIERE);
        trajectoryManager.reculeMMSansAngle(100);
        rs.disableForceMonitoring();
        ThreadUtils.sleep(5000); // Stabilisation
        rs.disableAsserv();

        double roueDroite = 0;
        double roueGauche = 0;

        // Filtrage sur les métriques codeurs
        List<MonitorTimeSerie> codeursData = monitoringWrapper.monitorTimeSeriePoints().stream()
                .filter(m -> m.getMeasurementName().equals("encodeurs"))
                .collect(Collectors.toList());

        monitoringWrapper.cleanAllPoints();

        for (MonitorTimeSerie d : codeursData) {
            roueDroite += d.getFields().get("droit").doubleValue();
            roueGauche += d.getFields().get("gauche").doubleValue();
        }

        // NB: Roue plus grande => perimètre plus grand => nombre de pulse plus petit
        // Delta = distance roue gauche - distance roue droite
        // Si delta > 0 => roue gauche plus petite que la roue droite (roue gauche a fait plus de tour)

        double distance = (roueDroite + roueGauche) / 2;
        double delta = roueGauche - roueDroite; // Si delta > 0, la roue gauche est plus petite que la roue droite

        double ratioError = delta / distance;

        double oldGauche = encoders.getCoefGauche();
        double oldDroit = encoders.getCoefDroit();
        double newGauche = (1 - ratioError) * oldGauche;
        double newDroit = (1 + ratioError) * oldDroit;
        if (delta > 0) {
            log.info("Roue gauche plus petite que la roue droite");
            newDroit = 1;
        } else {
            log.info("Roue droite plus petite que la roue gauche");
            newGauche = 1;
        }

        log.info("Roue gauche : {} pulse", roueGauche);
        log.info("Roue droite : {} pulse", roueDroite);
        log.info("Distance    : {} pulse", distance);
        log.info("Delta G - D : {} pulse", delta);
        log.info("Ratio error : {}", ratioError);
        log.info(LOG_SEPARATOR);
        log.info("Coef gauche : {} -> {}", oldGauche, newGauche);
        log.info("Coef droit  : {} -> {}", oldDroit, newDroit);
        log.info(LOG_SEPARATOR);
        log.info("Application des nouveaux coef");

        encoders.setCoefs(newGauche, newDroit);
    }

    @SneakyThrows
    @ShellMethod("Réglage distance")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieDistance(int nbCycle) {

        double distanceReel = DISTANCE_TABLE - (PamiConstantesConfig.dstCallage * 2);

        encoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        // Calage arriere
        rs.enableCalageBordure(TypeCalage.ARRIERE);
        trajectoryManager.setVitesse(100, 1000);
        trajectoryManager.reculeMMSansAngle(1000);

        List<MonitorTimeSerie> codeursData;
        double roueDroite = 0;
        double roueGauche = 0;
        double localRoueDroite;
        double localRoueGauche;

        List<Double> countPerMmByCycle = new ArrayList<>();
        for (int cycle = 0 ; cycle < nbCycle ; cycle++) {
            log.info(LOG_CYCLE, cycle + 1, nbCycle);

            rs.enableForceMonitoring();
            currentPosition.updatePosition(0, 0, 0);
            trajectoryManager.gotoPoint(distanceReel - 50, 0, GotoOption.AVANT, GotoOption.SANS_ORIENTATION);
            rs.enableCalageBordure(TypeCalage.AVANT);
            trajectoryManager.avanceMMSansAngle(200);
            rs.disableForceMonitoring();
            ThreadUtils.sleep(1000); // Stabilisation

            codeursData = monitoringWrapper.monitorTimeSeriePoints().stream()
                    .filter(m -> m.getMeasurementName().equals("encodeurs"))
                    .collect(Collectors.toList());
            monitoringWrapper.cleanAllPoints();
            localRoueDroite = 0;
            localRoueGauche = 0;
            for (MonitorTimeSerie d : codeursData) {
                localRoueDroite += d.getFields().get("droit").doubleValue();
                localRoueGauche += d.getFields().get("gauche").doubleValue();
            }
            roueDroite += localRoueDroite;
            roueGauche += localRoueGauche;
            countPerMmByCycle.add(((localRoueDroite + localRoueGauche) / 2) / distanceReel);

            rs.enableForceMonitoring();
            currentPosition.updatePosition(convRobot.mmToPulse(DISTANCE_TABLE - PamiConstantesConfig.dstCallage), 0, 0);
            trajectoryManager.gotoPoint(DISTANCE_TABLE - distanceReel - 50, 0, GotoOption.ARRIERE, GotoOption.SANS_ORIENTATION);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMMSansAngle(200);
            rs.disableForceMonitoring();
            ThreadUtils.sleep(1000); // Stabilisation

            codeursData = monitoringWrapper.monitorTimeSeriePoints().stream()
                    .filter(m -> m.getMeasurementName().equals("encodeurs"))
                    .collect(Collectors.toList());
            monitoringWrapper.cleanAllPoints();
            localRoueDroite = 0;
            localRoueGauche = 0;
            for (MonitorTimeSerie d : codeursData) {
                localRoueDroite += d.getFields().get("droit").doubleValue() * -1;
                localRoueGauche += d.getFields().get("gauche").doubleValue() * -1;
            }
            roueDroite += localRoueDroite;
            roueGauche += localRoueGauche;
            countPerMmByCycle.add(((localRoueDroite + localRoueGauche) / 2) / distanceReel);
        }
        rs.disableAsserv();

        double distance = (roueDroite + roueGauche) / 2;
        double newCountPerMM = distance / (distanceReel * 2 * nbCycle);
        log.info("Roue gauche      : {} pulse", roueGauche);
        log.info("Roue droite      : {} pulse", roueDroite);
        log.info("Distance Rob     : {} pulse", distance);
        log.info("Distance /cycle  : {} pulse", distance / nbCycle);
        log.info("Distance G       : {} mm", convRobot.pulseToMm(roueGauche));
        log.info("Distance D       : {} mm", convRobot.pulseToMm(roueDroite));
        log.info("Distance Rob     : {} mm", convRobot.pulseToMm(distance));
        log.info(LOG_SEPARATOR);
        for (int i = 0; i < countPerMmByCycle.size(); i++) {
            log.info("Count per mm {}  : {}", i, countPerMmByCycle.get(i));
        }
        log.info(LOG_SEPARATOR);
        log.info("Old count per mm : {}", convRobot.countPerMm());
        log.info("New count per mm : {}", newCountPerMM); // Distance de la table 2022
        log.info("Ecart            : {}", convRobot.countPerMm() - newCountPerMM);
        log.info(LOG_SEPARATOR);
        log.info("Application du nouveau paramètre de conversion");

        convRobot.countPerMm(newCountPerMM);
    }

    @SneakyThrows
    @ShellMethod("Réglage distance (manuelle)")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieDistanceManuel(int distanceCmd) {
        encoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        // Calage arriere
        rs.enableCalageBordure(TypeCalage.ARRIERE);
        trajectoryManager.setVitesse(100, 1000);
        trajectoryManager.reculeMMSansAngle(1000);
        trajectoryManager.setVitesse(500, 1000);
        trajectoryManager.avanceMM(distanceCmd);
        ThreadUtils.sleep(3000); // Stabilisation

        rs.disableAsserv();

        log.info("Pour upgrade il faut calculer : {} * ({} / MESURE_MM)", convRobot.countPerMm(), distanceCmd);
        log.info("Pour le calcule la méthode 'odometrie-distance-manuel-reglage --distance-cmd {} --mesure-mm <value>' peut être utilisée", distanceCmd);
    }
    @ShellMethod("Réglage distance (manuelle) - Application du nouveau paramètre de conversion")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieDistanceManuelReglage(double mesureMm, int distanceCmd) {
        double newCountPerMM = convRobot.countPerMm() * (distanceCmd / mesureMm);
        log.info(LOG_SEPARATOR);
        log.info("Old count per mm : {}", convRobot.countPerMm());
        log.info("New count per mm : {}", newCountPerMM);
        log.info(LOG_SEPARATOR);
        log.info("Application du nouveau paramètre de conversion");

        convRobot.countPerMm(newCountPerMM);
    }

    @SneakyThrows
    @ShellMethod("Réglage rotation")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieRotation(int nbCycle) {
        boolean first = true;
        int i = 0;
        double newCountPerDegFirst = 0;
        double newCountPerDegSecond = 0;
        do {
            encoders.reset();
            rs.enableAsserv();
            rs.disableAvoidance();

            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.setVitesse(100, 1000);
            trajectoryManager.reculeMMSansAngle(100);

            rs.enableForceMonitoring();
            monitoringWrapper.cleanAllPoints();
            currentPosition.updatePosition(0, 0, 0);
            trajectoryManager.setVitesse(100, 1000);
            trajectoryManager.avanceMM(100);
            trajectoryManager.setVitesse(1000, 200);
            for (int cycle = 0; cycle < nbCycle; cycle++) {
                log.info(LOG_CYCLE, cycle + 1, nbCycle);
                trajectoryManager.tourneDeg(360 * (first ? 1 : -1));
                trajectoryManager.gotoOrientationDeg(0);
            }
            trajectoryManager.setVitesse(100, 1000);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMM(90);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMMSansAngle(30);

            rs.disableForceMonitoring();
            ThreadUtils.sleep(3000); // Stabilisation
            rs.disableAsserv();

            double roueDroite = 0;
            double roueGauche = 0;

            // Filtrage sur les métriques codeurs
            List<MonitorTimeSerie> codeursData = monitoringWrapper.monitorTimeSeriePoints().stream()
                    .filter(m -> m.getMeasurementName().equals("encodeurs"))
                    .collect(Collectors.toList());

            monitoringWrapper.cleanAllPoints();

            for (MonitorTimeSerie d : codeursData) {
                roueDroite += d.getFields().get("droit").doubleValue();
                roueGauche += d.getFields().get("gauche").doubleValue();
            }

            double distance = Math.abs(roueDroite) + Math.abs(roueGauche);
            if (first) {
                newCountPerDegFirst = distance / (360 * nbCycle);
            } else {
                newCountPerDegSecond = distance / (360 * nbCycle);
            }

            log.info("Roue gauche     : {} pulse", roueGauche);
            log.info("Roue droite     : {} pulse", roueDroite);
            log.info("Distance totale : {} pulse", distance);

            first = false;
            i++;
        } while (i < 2);
        double newCountPerDeg = (newCountPerDegFirst + newCountPerDegSecond) / 2;
        log.info(LOG_SEPARATOR);
        log.info("Count per mm          : {}", convRobot.countPerMm());
        log.info("Count per deg         : {}", convRobot.countPerDegree());
        log.info("New count per deg 1   : {}", newCountPerDegFirst);
        log.info("New count per deg 2   : {}", newCountPerDegSecond);
        log.info("New count per deg moy : {}", newCountPerDeg);
        log.info("Ecart                 : {}", newCountPerDeg - convRobot.countPerDegree());
        log.info(LOG_SEPARATOR);
        log.info("Application du nouveau paramètre de conversion");

        convRobot.countPerDegree(newCountPerDeg);
    }

    @SneakyThrows
    @ShellMethod("Réglage entraxe")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieEntraxe(int nbCycle) {
        boolean first = true;
        int i = 0;
        double oldTrack = convRobot.entraxe();
        double newTrackFirst = 0;
        double newTrackSecond = 0;
        do {
            encoders.reset();
            rs.enableAsserv();
            rs.disableAvoidance();

            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.setVitesse(100, 1000);
            trajectoryManager.reculeMMSansAngle(100);

            rs.enableForceMonitoring();
            monitoringWrapper.cleanAllPoints();
            currentPosition.updatePosition(0, 0, 0);
            trajectoryManager.avanceMM(70);
            trajectoryManager.setVitesse(1000, 500);
            trajectoryManager.tourneDeg(360 * (first ? nbCycle : -nbCycle));
            trajectoryManager.gotoOrientationDeg(0);
            trajectoryManager.setVitesse(100, 1000);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMM(70);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMMSansAngle(70);

            rs.disableForceMonitoring();
            double finalAngle = convRobot.pulseToDeg(currentPosition.getAngle());
            ThreadUtils.sleep(300); // Stabilisation
            rs.disableAsserv();

            double roueDroite = 0;
            double roueGauche = 0;

            // Filtrage sur les métriques codeurs
            List<MonitorTimeSerie> codeursData = monitoringWrapper.monitorTimeSeriePoints().stream()
                    .filter(m -> m.getMeasurementName().equals("encodeurs"))
                    .collect(Collectors.toList());

            monitoringWrapper.cleanAllPoints();

            for (MonitorTimeSerie d : codeursData) {
                roueDroite += d.getFields().get("droit").doubleValue();
                roueGauche += d.getFields().get("gauche").doubleValue();
            }

            double ratio = finalAngle / (360 * nbCycle * (first ? 1 : -1));
            double newTrack = oldTrack * (1 + ratio);
            if (first) {
                newTrackFirst = newTrack;
            } else {
                newTrackSecond = newTrack;
            }

            log.info("Roue gauche     : {} pulse", roueGauche);
            log.info("Roue droite     : {} pulse", roueDroite);
            log.info("Delta Angle     : {} °", finalAngle);
            log.info("Ratio           : {}", ratio);

            first = false;
            i++;
        } while (i < 2);
        double newTrackMoy = (newTrackFirst + newTrackSecond) / 2;
        log.info(LOG_SEPARATOR);
        log.info("Count per mm          : {} pulse", convRobot.countPerMm());
        log.info("Old count per deg     : {} pulse", convRobot.countPerDegree());
        log.info("Old entraxe           : {} mm", oldTrack);
        log.info("New entraxe 1         : {} mm", newTrackFirst);
        log.info("New entraxe 2         : {} mm", newTrackSecond);
        log.info("New entraxe moy       : {} mm", newTrackMoy);
        log.info("Ecart                 : {} mm", newTrackMoy - oldTrack);
        log.info(LOG_SEPARATOR);
        log.info("Application du nouveau paramètre de conversion");

        convRobot.entraxe(newTrackMoy);
        log.info("New countPerDeg       : {} pulse", convRobot.countPerDegree());
    }
}
