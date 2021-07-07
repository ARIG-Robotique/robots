package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@ShellCommandGroup("Odométrie")
@RequiredArgsConstructor
public class NerellOdometrieCommands {

    private final IMonitoringWrapper monitoringWrapper;
    private final INerellIOService ioService;
    private final AbstractEnergyService energyService;
    private final TrajectoryManager trajectoryManager;
    private final NerellRobotStatus rs;
    private final ARIG2WheelsEncoders encoders;
    private final ConvertionRobotUnit convRobot;
    private final CommandeRobot cmdRobot;
    private final Position currentPosition;
    private final IPidFilter pidDistance;
    private final IPidFilter pidOrientation;

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

        rs.enableCalageBordure();
        trajectoryManager.setVitesse(100, 100);
        trajectoryManager.reculeMMSansAngle(1000);

        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
        for (int cycle = 0 ; cycle < nbCycle ; cycle++) {
            log.info("Cycle {} / {}", cycle + 1, nbCycle);
            trajectoryManager.avanceMM(cycle == 0 ? 1000 : 800);
            trajectoryManager.tourneDeg(180);
            trajectoryManager.avanceMM(800);
            trajectoryManager.tourneDeg(-180);
        }
        rs.enableCalageBordure();
        trajectoryManager.reculeMMSansAngle(1000);
        rs.disableForceMonitoring();
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

        double distance = (roueDroite + roueGauche) / 2;
        double delta = roueDroite - roueGauche;

        double correction = delta / distance;

        double oldGauche = encoders.getCoefGauche();
        double oldDroit = encoders.getCoefDroit();

        log.info("Roue gauche : {} pulse", roueGauche);
        log.info("Roue droite : {} pulse", roueDroite);
        log.info("Distance    : {} pulse", distance);
        log.info("Delta D - G : {} pulse", delta);
        log.info("Correction  : {}", correction);
        log.info("-------------------------------------------------");
        log.info("Coef gauche : {} -> {}", oldGauche, oldGauche + correction);
        log.info("Coef droit  : {} -> {}", oldDroit, oldDroit - correction);
    }

    @SneakyThrows
    @ShellMethod("Réglage distance")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieDistance() {
        double distanceEntreCalage = 2999; // Table Gite 2021
        double dstCalageAvant = 103; // Distance calage avant
        double distanceReel = distanceEntreCalage - INerellConstantesConfig.dstCallage - dstCalageAvant;

        encoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        trajectoryManager.setVitesse(100, 500);
        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
        trajectoryManager.reculeMM(distanceReel - 50);
        rs.enableCalageBordure();
        trajectoryManager.reculeMMSansAngle(200);

        rs.disableForceMonitoring();
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

        double distance = (roueDroite + roueGauche) / 2;

        log.info("Roue gauche  : {} pulse", roueGauche);
        log.info("Roue droite  : {} pulse", roueDroite);
        log.info("Distance Rob : {} pulse", distance);
        log.info("Distance G   : {} mm", convRobot.pulseToMm(roueGauche));
        log.info("Distance D   : {} mm", convRobot.pulseToMm(roueDroite));
        log.info("Distance Rob : {} mm", convRobot.pulseToMm(distance));
        log.info("-------------------------------------------------");
        log.info("Count per mm : {}", distance / distanceReel);
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

            rs.enableCalageBordure();
            trajectoryManager.setVitesse(100, 100);
            trajectoryManager.reculeMMSansAngle(1000);

            rs.enableForceMonitoring();
            monitoringWrapper.cleanAllPoints();
            trajectoryManager.avanceMM(100);
            for (int cycle = 0; cycle < nbCycle; cycle++) {
                log.info("Cycle {} / {}", cycle + 1, nbCycle);
                trajectoryManager.tourneDeg(360 * (first ? 1 : -1));
            }
            trajectoryManager.gotoOrientationDeg(0);
            rs.enableCalageBordure();
            trajectoryManager.reculeMMSansAngle(1000);

            rs.disableForceMonitoring();
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
        log.info("-------------------------------------------------");
        log.info("Count per mm          : {}", INerellConstantesConfig.countPerMm);
        log.info("Count per deg         : {}", INerellConstantesConfig.countPerDeg);
        log.info("New Count per deg 1   : {}", newCountPerDegFirst);
        log.info("New Count per deg 2   : {}", newCountPerDegSecond);
        log.info("New Count per deg moy : {}", (newCountPerDegSecond + newCountPerDegFirst) / 2);
    }
}
