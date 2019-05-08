package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.TypeConsigne;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.services.IIOService;
import org.arig.robot.utils.ConvertionCarouselUnit;
import org.arig.robot.utils.ConvertionRobotUnit;
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

@Slf4j
@ShellComponent
@ShellCommandGroup("Asservissement")
@AllArgsConstructor
public class AsservissementCommands {

    private final IMonitoringWrapper monitoringWrapper;
    private final IIOService ioService;
    private final RobotStatus rs;
    private final ConvertionRobotUnit convRobot;
    private final ConvertionCarouselUnit convCarousel;
    private final CommandeRobot cmdRobot;
    private final CommandeAsservissementPosition cmdAsservCarousel;
    private final Position currentPosition;
    private final IPidFilter pidDistance;
    private final IPidFilter pidOrientation;
    private final IPidFilter pidMoteurDroit;
    private final IPidFilter pidMoteurGauche;

    private void startMonitoring() {
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);
        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
    }

    @SneakyThrows
    private void endMonitoring() {
        monitoringWrapper.save();
        rs.disableForceMonitoring();

        final String execId = System.getProperty(IConstantesConfig.keyExecutionId);

        final File execFile = new File("./logs/" + execId + ".exec");
        DateTimeFormatter execIdPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter savePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> lines = new ArrayList<>();
        lines.add(LocalDateTime.parse(execId, execIdPattern).format(savePattern));
        lines.add(LocalDateTime.now().format(savePattern));
        FileUtils.writeLines(execFile, lines);

        log.info("Création du fichier de fin d'éxécution {}", execFile.getAbsolutePath());
    }

    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    public void pidDistance(@NotNull @Min(0) double kp, @NotNull @Min(0) double ki, @NotNull @Min(0) double kd) {
        pidDistance.setTunings(kp, ki * IConstantesNerellConfig.asservTimeS, kd / IConstantesNerellConfig.asservTimeS);
        pidDistance.reset();
    }

    public void pidOrientation(@NotNull @Min(0) double kp, @NotNull @Min(0) double ki, @NotNull @Min(0) double kd) {
        pidOrientation.setTunings(kp, ki * IConstantesNerellConfig.asservTimeS, kd / IConstantesNerellConfig.asservTimeS);
        pidOrientation.reset();
    }

    public void pidMoteurDroit(@NotNull @Min(0) double kp, @NotNull @Min(0) double ki, @NotNull @Min(0) double kd) {
        pidMoteurDroit.setTunings(kp, ki * IConstantesNerellConfig.asservTimeS, kd / IConstantesNerellConfig.asservTimeS);
        pidMoteurDroit.reset();
    }

    public void pidMoteurGauche(@NotNull @Min(0) double kp, @NotNull @Min(0) double ki, @NotNull @Min(0) double kd) {
        pidMoteurGauche.setTunings(kp, ki * IConstantesNerellConfig.asservTimeS, kd / IConstantesNerellConfig.asservTimeS);
        pidMoteurGauche.reset();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du robot")
    public void asservRobot(@NotNull TypeConsigne[] typeConsignes, long distance, long orientation, long vitesseDistance, long vitesseOrientation) {
        startMonitoring();


        cmdRobot.setTypes(typeConsignes);
        cmdRobot.getVitesse().setDistance(vitesseDistance);
        cmdRobot.getVitesse().setOrientation(vitesseOrientation);
        cmdRobot.getConsigne().setDistance((long) convRobot.mmToPulse(distance));
        cmdRobot.getConsigne().setOrientation((long) convRobot.degToPulse(orientation));
        cmdRobot.setFrein(true);

        rs.enableAsserv();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du robot en XY")
    public void asservRobotXY(@NotNull double x, @NotNull double y, @NotNull double angle, long vitesseDistance, long vitesseOrientation) {
        startMonitoring();

        cmdRobot.setTypes(TypeConsigne.XY);
        cmdRobot.getVitesse().setDistance(vitesseDistance);
        cmdRobot.getVitesse().setOrientation(vitesseOrientation);
        final Point pt = new Point(convRobot.mmToPulse(x), convRobot.mmToPulse(y));
        cmdRobot.setPosition(new Position(pt, convRobot.degToPulse(angle)));
        cmdRobot.setFrein(true);

        rs.enableAsserv();
    }

    @ShellMethod("Désactivation asservissement du robot")
    public void disableAsservRobot() {
        rs.disableAsserv();
        endMonitoring();
    }

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du Carousel")
    public void asservCarousel(int index) {
        startMonitoring();

        cmdAsservCarousel.getVitesse().setValue(100);
        cmdAsservCarousel.getConsigne().setValue(convCarousel.indexToPulse(index));
        cmdAsservCarousel.setFrein(true);

        rs.enableAsservCarousel();
    }

    @ShellMethod("Désactivation asservissement du carousel")
    public void disableAsservCarousel() {
        rs.disableAsservCarousel();
        endMonitoring();
    }

    @ShellMethod("Lecture de la position actuelle")
    public void readPosition() {
        log.info("X: {}", convRobot.pulseToMm(currentPosition.getPt().getX()));
        log.info("Y: {}", convRobot.pulseToMm(currentPosition.getPt().getY()));
        log.info("A: {}", convRobot.pulseToDeg(currentPosition.getAngle()));
    }

    @ShellMethod("Réinitialisation de la position")
    public void resetPosition() {
        currentPosition.getPt().setX(0);
        currentPosition.getPt().setY(0);
        currentPosition.setAngle(0);
        readPosition();
    }
}
