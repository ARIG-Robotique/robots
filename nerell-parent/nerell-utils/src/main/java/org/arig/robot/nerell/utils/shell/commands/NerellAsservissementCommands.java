package org.arig.robot.nerell.utils.shell.commands;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.io.Console;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ShellComponent
@ShellCommandGroup("Asservissement")
@RequiredArgsConstructor
public class NerellAsservissementCommands {

    private final MonitoringWrapper monitoringWrapper;
    private final NerellIOService ioService;
    private final AbstractEnergyService energyService;
    private final TrajectoryManager trajectoryManager;
    private final NerellRobotStatus rs;
    private final ConvertionRobotUnit convRobot;
    private final CommandeRobot cmdRobot;
    private final Position currentPosition;
    private final PidFilter pidDistance;
    private final PidFilter pidOrientation;
    private final IAsservissementPolaire asservissement;
    private final Abstract2WheelsEncoders wheelsEncoders;

    private enum PIDCoef {
        KP, KI, KD
    }

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
    public void vitesseRobotPercent(@NotNull int vitesseDistance, @NotNull int vitesseOrientation) {
        trajectoryManager.setVitessePercent(vitesseDistance, vitesseOrientation);
    }

    @ShellMethod("Réglage des rampes de distance")
    public void rampDistanceRobotPercent(@NotNull int accel, @NotNull int decel) {
        trajectoryManager.setRampesDistancePercent(accel, decel);
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Avance le robot")
    public void avanceRobot(@NotNull long distance) throws AvoidingException {
        startMonitoring();

        wheelsEncoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        rs.enableCalageTempo(10000);

        trajectoryManager.avanceMM(distance);

        endMonitoring();
        rs.disableAsserv();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Tourne le robot")
    public void tourneRobot(@NotNull long angle) throws AvoidingException {
        startMonitoring();

        wheelsEncoders.reset();
        rs.enableAsserv();
        rs.disableAvoidance();

        rs.enableCalageTempo(10000);

        trajectoryManager.tourneDeg(angle);

        endMonitoring();
        rs.disableAsserv();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du robot")
    public void asservRobot(@NotNull long distance, @NotNull long orientation, SensDeplacement sens, TypeConsigne[] typeConsignes) {
        startMonitoring();

        trajectoryManager.setVitessePercent(50, 100);
        trajectoryManager.setRampesDistancePercent(50, 50);

        asservissement.reset(true);

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

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Reglage asserv orientation")
    public void reglageAsservOrientation(double dplct, double increment, double kp, double ki, double kd) {
        startMonitoring();

        trajectoryManager.setVitessePercent(100, 100);

        Console console = System.console();
        String tmpCoef = console.readLine("Quel coefficient ? (KP, KI, KD) : ");
        PIDCoef coefToChange = tmpCoef.equals("KP") ? PIDCoef.KP : tmpCoef.equals("KI") ? PIDCoef.KI : PIDCoef.KD;
        boolean continueInc;
        boolean alt = false;
        do {
            alt = !alt;
            pidOrientation.setTunings(kp, ki, kd);
            switch(coefToChange) {
                case KP:
                    kp += increment;
                    break;
                case KI:
                    ki += increment;
                    break;
                case KD:
                    kd += increment;
                    break;
            }

            cmdRobot.setTypes(new TypeConsigne[]{TypeConsigne.ANGLE});
            cmdRobot.setSensDeplacement(SensDeplacement.AUTO);
            cmdRobot.getConsigne().setOrientation((long) convRobot.degToPulse(alt ? dplct : -dplct));
            cmdRobot.setFrein(true);

            rs.enableAsserv();

            String checkContinue = console.readLine("Continuer ? (O/N) : ");
            continueInc = !checkContinue.equals("N");

        } while (continueInc);

        disableAsservRobot();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Reglage asserv distance")
    public void reglageAsservDistance(double dplct, double increment, double kp, double ki, double kd) {
        startMonitoring();

        trajectoryManager.setVitessePercent(100, 100);

        Console console = System.console();
        String tmpCoef = console.readLine("Quel coefficient ? (KP, KI, KD) : ");
        PIDCoef coefToChange = tmpCoef.equals("KP") ? PIDCoef.KP : tmpCoef.equals("KI") ? PIDCoef.KI : PIDCoef.KD;
        boolean continueInc;
        boolean alt = false;
        do {
            alt = !alt;
            pidDistance.setTunings(kp, ki, kd);
            switch(coefToChange) {
                case KP:
                    kp += increment;
                    break;
                case KI:
                    ki += increment;
                    break;
                case KD:
                    kd += increment;
                    break;
            }

            cmdRobot.setTypes(new TypeConsigne[]{TypeConsigne.ANGLE, TypeConsigne.DIST});
            cmdRobot.setSensDeplacement(SensDeplacement.AUTO);
            cmdRobot.getConsigne().setOrientation(0);
            cmdRobot.getConsigne().setDistance((long) convRobot.mmToPulse(alt ? dplct : -dplct));
            cmdRobot.setFrein(true);

            rs.enableAsserv();

            String checkContinue = console.readLine("Continuer ? (O/N) : ");
            continueInc = !checkContinue.equals("N");

        } while (continueInc);

        disableAsservRobot();
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
