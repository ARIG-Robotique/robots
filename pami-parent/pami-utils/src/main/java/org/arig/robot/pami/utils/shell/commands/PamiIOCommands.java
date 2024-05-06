package org.arig.robot.pami.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.PamiIOServiceRobot;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.capteurs.i2c.ARIG2024IoPamiSensors;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.utils.ThreadUtils;
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
import java.util.concurrent.CompletableFuture;

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

    @ShellMethod("Identification des servos")
    public void readAllIo() {
        log.info("Lecture des IOs");
        log.info("Calage arriere gauche : {}", pamiIOServiceRobot.calageArriereGauche());
        log.info("Calage arriere droit  : {}", pamiIOServiceRobot.calageArriereDroit());
        log.info("GP2D gauche           : {}", pamiIOServiceRobot.distanceGauche());
        log.info("GP2D centre           : {}", pamiIOServiceRobot.distanceCentre());
        log.info("GP2D droit            : {}", pamiIOServiceRobot.distanceDroit());
    }

    @SneakyThrows
    @ShellMethod("Monitor ADC")
    public void monitorAdc(int duration) {
        startMonitoring();

        long end = System.currentTimeMillis() + duration * 1000L;

        do {
            double gauche = pamiIOServiceRobot.distanceGauche();
            double centre = pamiIOServiceRobot.distanceCentre();
            double droit = pamiIOServiceRobot.distanceDroit();

            log.info("{} {} {}", gauche, centre, droit);

            MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("adc")
                .addField("gauche", gauche)
                .addField("centre", centre)
                .addField("droit", droit);

            monitoringWrapper.addTimeSeriePoint(serie);

            ThreadUtils.sleep(20);

        } while (System.currentTimeMillis() < end);

        endMonitoring();
    }

    /**
     * Recul de 1000mm tout en tracant les valeurs des GP2D
     *
     * @example Query Flux
     *
     from(bucket: "robots")
     |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
     |> filter(fn: (r) => r["_measurement"] == "adc")
     |> filter(fn: (r) => r["_field"] == "x" or r["_field"] == "value1" or r["_field"] == "value2" or r["_field"] == "value3" or r["_field"] == "value4")
     |> aggregateWindow(every: 100ms, fn: mean, createEmpty: false)
     |> map(fn: (r) =>  ({ r with _value: if r._field == "x" then r._value else 230000.0 / r._value - 50.0 }))
     |> yield(name: "mean")
     */
    @SneakyThrows
    @ShellMethod()
    @ShellMethodAvailability("alimentationOk")
    public void calibrationGp() {
        startMonitoring();

        wheelsEncoders.reset();
        rs.enableAsserv();

        trajectoryManager.setVitessePercent(30, 100);

        CompletableFuture.runAsync(() -> {
            try {
                trajectoryManager.reculeMM(1000);
            } catch (AvoidingException e) {
                throw new RuntimeException(e);
            }
        });

        do {
            double currentX = trajectoryManager.currentXMm();
            double gauche = pamiIOServiceRobot.distanceGauche();
            double centre = pamiIOServiceRobot.distanceCentre();
            double droit = pamiIOServiceRobot.distanceDroit();

            MonitorTimeSerie serie = new MonitorTimeSerie()
                .measurementName("adc")
                .addField("x", currentX)
                .addField("gauche", gauche)
                .addField("centre", centre)
                .addField("droit", droit);

            monitoringWrapper.addTimeSeriePoint(serie);

            ThreadUtils.sleep(20);

        } while (!trajectoryManager.isTrajetAtteint());

        endMonitoring();
        rs.disableAsserv();
    }
}
