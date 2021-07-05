package org.arig.robot.odin.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ShellComponent
@ShellCommandGroup("Odométrie")
@RequiredArgsConstructor
public class OdinOdometrieCommands {

    private final IMonitoringWrapper monitoringWrapper;
    private final IOdinIOService ioService;
    private final AbstractEnergyService energyService;
    private final TrajectoryManager trajectoryManager;
    private final OdinRobotStatus rs;
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
        rs.enableAsserv();
        rs.disableAvoidance();

        rs.enableCalageBordure();
        trajectoryManager.setVitesse(100, 100);
        trajectoryManager.reculeMMSansAngle(1000);

        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
        for (int i = 0 ; i < nbCycle ; i++) {
            trajectoryManager.avanceMM(i == 0 ? 1000 : 800);
            trajectoryManager.tourneDeg(180);
            trajectoryManager.avanceMM(800);
            trajectoryManager.tourneDeg(-180);
        }
        rs.enableCalageBordure();
        trajectoryManager.reculeMMSansAngle(1000);

        ThreadUtils.sleep(200);
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

        log.info("Old coef gauche : {}", oldGauche);
        log.info("Old coef droit  : {}", oldDroit);
        log.info("Roue gauche     : {}", roueGauche);
        log.info("Roue droite     : {}", roueDroite);
        log.info("Distance        : {}", distance);
        log.info("Delta D - G     : {}", delta);
        log.info("Correction      : {}", correction);

        log.info("New coef gauche : {}", oldGauche + correction);
        log.info("New coef droit  : {}", oldDroit - correction);
    }
}
