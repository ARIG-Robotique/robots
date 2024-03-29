package org.arig.robot.odin.utils.shell.commands;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ShellComponent
@ShellCommandGroup("Asservissement")
@RequiredArgsConstructor
public class OdinAsservissementCommands {

    private final MonitoringWrapper monitoringWrapper;
    private final OdinIOService ioService;
    private final AbstractEnergyService energyService;
    private final TrajectoryManager trajectoryManager;
    private final OdinRobotStatus rs;
    private final ConvertionRobotUnit convRobot;
    private final CommandeRobot cmdRobot;
    private final Position currentPosition;
    private final PidFilter pidDistance;
    private final PidFilter pidOrientation;

    private boolean monitoringRun = false;

    private void startMonitoring() {
        if (monitoringRun) {
            endMonitoring();
        }

        monitoringRun = true;
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);
        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
    }

    @SneakyThrows
    private void endMonitoring() {
        monitoringRun = false;
        monitoringWrapper.save();
        rs.disableForceMonitoring();

        final String execId = System.getProperty(ConstantesConfig.keyExecutionId);

        final File execFile = new File("./logs/" + execId + ".exec");
        DateTimeFormatter execIdPattern = DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat);
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern(ConstantesConfig.executiondDateFormat);
        List<String> lines = new ArrayList<>();
        lines.add(LocalDateTime.parse(execId, execIdPattern).format(savePattern));
        lines.add(LocalDateTime.now().format(savePattern));
        FileUtils.writeLines(execFile, lines);

        log.info("Création du fichier de fin d'exécution {}", execFile.getAbsolutePath());
    }

    public Availability alimentationOk() {
        return ioService.auOk() && energyService.checkServos() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Réglage PID Distance")
    public void pidDistance(@NotNull @Min(0) double kp, @NotNull @Min(0) double ki, @NotNull @Min(0) double kd) {
        pidDistance.setTunings(kp, ki, kd);
        pidDistance.reset();
    }

    @ShellMethod("Réglage PID Orientation")
    public void pidOrientation(@NotNull @Min(0) double kp, @NotNull @Min(0) double ki, @NotNull @Min(0) double kd) {
        pidOrientation.setTunings(kp, ki, kd);
        pidOrientation.reset();
    }

    @ShellMethod("Réglage des vitesses")
    public void vitesseRobot(@NotNull long vitesseDistance, @NotNull long vitesseOrientation) {
        trajectoryManager.setVitesse(vitesseDistance, vitesseOrientation);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du robot")
    public void asservRobot(@NotNull long distance, @NotNull long orientation, SensDeplacement sens, TypeConsigne[] typeConsignes) {
        startMonitoring();

        cmdRobot.setTypes(ArrayUtils.getLength(typeConsignes) == 0 ? new TypeConsigne[]{TypeConsigne.DIST, TypeConsigne.ANGLE} : typeConsignes);
        cmdRobot.setSensDeplacement(sens == null ? SensDeplacement.AUTO : sens);
        cmdRobot.getConsigne().setDistance((long) convRobot.mmToPulse(distance));
        cmdRobot.getConsigne().setOrientation((long) convRobot.degToPulse(orientation));
        cmdRobot.setFrein(true);

        rs.enableAsserv();
    }

    @ShellMethod("Désactivation asservissement du robot")
    public void disableAsservRobot() {
        rs.disableAsserv();
        endMonitoring();
    }

    @ShellMethod("Lecture de la position actuelle")
    public void readPosition() {
        log.info("X: {}", trajectoryManager.currentXMm());
        log.info("Y: {}", trajectoryManager.currentYMm());
        log.info("A: {}", trajectoryManager.currentAngleDeg());
    }

    @ShellMethod("Réinitialisation de la position")
    public void resetPosition() {
        currentPosition.getPt().setX(0);
        currentPosition.getPt().setY(0);
        currentPosition.setAngle(0);
        readPosition();
    }
}
