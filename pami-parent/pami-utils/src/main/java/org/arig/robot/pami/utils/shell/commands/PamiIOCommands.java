package org.arig.robot.pami.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.PamiIOServiceRobot;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.leds.ARIG2024IoPamiLeds;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class PamiIOCommands {

    private final PamiIOServiceRobot pamiIOServiceRobot;
    private final AbstractEnergyService energyService;
    private final MonitoringWrapper monitoringWrapper;
    private final PamiRobotStatus rs;
    private final Abstract2WheelsEncoders wheelsEncoders;
    private final TrajectoryManager trajectoryManager;
    private final ARIG2024IoPamiLeds arig2024IoPamiLeds;

    public Availability alimentationOk() {
        return pamiIOServiceRobot.auOk() && energyService.checkMoteurs()
            ? Availability.available() : Availability.unavailable("Alimentation moteurs KO");
    }

    private void startMonitoring() {
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern(ConstantesConfig.executiondIdFormat));
        System.setProperty(ConstantesConfig.keyExecutionId, execId);
        rs.enableForceMonitoring();
        monitoringWrapper.cleanAllPoints();
    }

    @SneakyThrows
    private void endMonitoring() {
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
    }

    @ShellMethod("Identification des IOs")
    public void readAllIo() {
        log.info("Lecture des IOs");
        log.info("Calage arriere gauche : {}", pamiIOServiceRobot.calageArriereGauche());
        log.info("Calage arriere droit  : {}", pamiIOServiceRobot.calageArriereDroit());
        log.info("Sol gauche            : {}", pamiIOServiceRobot.presenceSolGauche(false));
        log.info("Sol droit             : {}", pamiIOServiceRobot.presenceSolDroit(false));
    }

    @ShellMethod
    public void testLeds() {
        for (ARIG2024IoPamiLeds.LedColor c : ARIG2024IoPamiLeds.LedColor.values()) {
            arig2024IoPamiLeds.setLedAU(c);
            ThreadUtils.sleep(1000);
        }
        for (ARIG2024IoPamiLeds.LedColor c : ARIG2024IoPamiLeds.LedColor.values()) {
            arig2024IoPamiLeds.setLedTeam(c);
            ThreadUtils.sleep(1000);
        }
        for (ARIG2024IoPamiLeds.LedColor c : ARIG2024IoPamiLeds.LedColor.values()) {
            arig2024IoPamiLeds.setLedCalage(c);
            ThreadUtils.sleep(1000);
        }
        for (ARIG2024IoPamiLeds.LedColor c : ARIG2024IoPamiLeds.LedColor.values()) {
            arig2024IoPamiLeds.setAllLeds(c);
            ThreadUtils.sleep(1000);
        }
        arig2024IoPamiLeds.setAllLeds(ARIG2024IoPamiLeds.LedColor.Black);
    }
}
