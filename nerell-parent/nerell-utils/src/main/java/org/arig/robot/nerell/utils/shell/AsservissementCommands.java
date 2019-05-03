package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.model.CommandeRobot;
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

    @ShellMethodAvailability("alimentationOk")
    @ShellMethod("Asservissement du robot")
    public void asservRobot(@NotNull TypeConsigne[] typeConsignes, long distance, long orientation) {
        startMonitoring();

        cmdRobot.setTypes(typeConsignes);
        cmdRobot.getVitesse().setDistance(100);
        cmdRobot.getVitesse().setOrientation(100);
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
}
