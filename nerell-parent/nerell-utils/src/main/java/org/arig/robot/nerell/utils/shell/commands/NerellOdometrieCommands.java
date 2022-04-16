package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
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
public class NerellOdometrieCommands {

    private final MonitoringWrapper monitoringWrapper;
    private final NerellIOService ioService;
    private final AbstractEnergyService energyService;
    private final TrajectoryManager trajectoryManager;
    private final NerellRobotStatus rs;
    private final ARIG2WheelsEncoders encoders;
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
        rs.enableCalageBordure(TypeCalage.ARRIERE);
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
        log.info("Ecart       : {}%", correction * 100);
    }

    @SneakyThrows
    @ShellMethod("Réglage distance")
    @ShellMethodAvailability("alimentationOk")
    public void odometrieDistance(int nbCycle) {
        double distanceEntreCalage = 2999; // Table Greg 2022
        double distanceReel = distanceEntreCalage - (NerellConstantesConfig.dstCallage * 2);

        encoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        // Calage arriere
        rs.enableCalageBordure(TypeCalage.ARRIERE);
        trajectoryManager.setVitesse(100, 100);
        trajectoryManager.reculeMMSansAngle(1000);

        List<MonitorTimeSerie> codeursData;
        double roueDroite = 0;
        double roueGauche = 0;
        double localRoueDroite = 0;
        double localRoueGauche = 0;

        List<Double> countPerMmByCycle = new ArrayList<>();
        for (int cycle = 0 ; cycle < nbCycle ; cycle++) {
            log.info("Cycle {} / {}", cycle + 1, nbCycle);

            rs.enableForceMonitoring();
            currentPosition.updatePosition(0, 0, 0);
            trajectoryManager.gotoPoint(distanceReel - 50, 0, GotoOption.AVANT, GotoOption.SANS_ORIENTATION);
            rs.enableCalageBordure(TypeCalage.AVANT);
            trajectoryManager.avanceMMSansAngle(200);
            rs.disableForceMonitoring();

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
            currentPosition.updatePosition(convRobot.mmToPulse(distanceEntreCalage - NerellConstantesConfig.dstCallage), 0, 0);
            trajectoryManager.gotoPoint(distanceEntreCalage - distanceReel - 50, 0, GotoOption.ARRIERE, GotoOption.SANS_ORIENTATION);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMMSansAngle(200);
            rs.disableForceMonitoring();

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

        log.info("Roue gauche     : {} pulse", roueGauche);
        log.info("Roue droite     : {} pulse", roueDroite);
        log.info("Distance Rob    : {} pulse", distance);
        log.info("Distance /cycle : {} pulse", distance / nbCycle);
        log.info("Distance G      : {} mm", convRobot.pulseToMm(roueGauche));
        log.info("Distance D      : {} mm", convRobot.pulseToMm(roueDroite));
        log.info("Distance Rob    : {} mm", convRobot.pulseToMm(distance));
        log.info("-------------------------------------------------");
        log.info("Count per mm    : {}", distance / (distanceReel * 2 * nbCycle)); // Distance de la table 2022
        log.info("-------------------------------------------------");
        for (int i = 0; i < countPerMmByCycle.size(); i++) {
            log.info("Count per mm {} : {}", i, countPerMmByCycle.get(i));
        }
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
            trajectoryManager.setVitesse(100, 100);
            trajectoryManager.reculeMMSansAngle(100);

            rs.enableForceMonitoring();
            monitoringWrapper.cleanAllPoints();
            trajectoryManager.avanceMM(100);
            for (int cycle = 0; cycle < nbCycle; cycle++) {
                log.info("Cycle {} / {}", cycle + 1, nbCycle);
                trajectoryManager.tourneDeg(360 * (first ? 1 : -1));
            }
            trajectoryManager.gotoOrientationDeg(0);
            rs.enableCalageBordure(TypeCalage.ARRIERE);
            trajectoryManager.reculeMMSansAngle(500);

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
        log.info("Count per mm          : {}", NerellConstantesConfig.countPerMm);
        log.info("Count per deg         : {}", NerellConstantesConfig.countPerDeg);
        log.info("New Count per deg 1   : {}", newCountPerDegFirst);
        log.info("New Count per deg 2   : {}", newCountPerDegSecond);
        log.info("New Count per deg moy : {}", (newCountPerDegSecond + newCountPerDegFirst) / 2);
    }
}
