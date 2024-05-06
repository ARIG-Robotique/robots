package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOServiceRobot;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.system.capteurs.i2c.I2CAdcAnalogInput;
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
@RequiredArgsConstructor
public class NerellIOCommands {

    private final NerellIOServiceRobot nerellIOServiceRobot;
    private final AbstractEnergyService energyService;
    private final MonitoringWrapper monitoringWrapper;
    private final Abstract2WheelsEncoders wheelsEncoders;
    private final TrajectoryManager trajectoryManager;
    private final NerellRobotStatus rs;
    private final I2CAdcAnalogInput adc;

    public Availability alimentationOk() {
        return nerellIOServiceRobot.auOk() && energyService.checkMoteurs()
                ? Availability.available() : Availability.unavailable("Alimentation moteurs KO");
    }

    @ShellMethod("Read all IOs")
    public void readAllIO() {
        log.info("Tirette = {}", nerellIOServiceRobot.tirette());
        log.info("Calage avant gauche = {}", nerellIOServiceRobot.calageAvantGauche());
        log.info("Calage avant droit = {}", nerellIOServiceRobot.calageAvantDroit());
        log.info("Calage arriere gauche = {}", nerellIOServiceRobot.calageArriereGauche());
        log.info("Calage arriere droit = {}", nerellIOServiceRobot.calageArriereDroit());
        log.info("Inductif gauche = {}", nerellIOServiceRobot.inductifGauche(false));
        log.info("Inductif centre = {}", nerellIOServiceRobot.inductifCentre(false));
        log.info("Inductif droite = {}", nerellIOServiceRobot.inductifDroite(false));
        log.info("Stock gauche = {}", nerellIOServiceRobot.presenceStockGauche(false));
        log.info("Stock centre = {}", nerellIOServiceRobot.presenceStockCentre(false));
        log.info("Stock droite = {}", nerellIOServiceRobot.presenceStockDroite(false));
        log.info("Pince avant gauche = {}", nerellIOServiceRobot.pinceAvantGauche(false));
        log.info("Pince avant centre = {}", nerellIOServiceRobot.pinceAvantCentre(false));
        log.info("Pince avant droite = {}", nerellIOServiceRobot.pinceAvantDroite(false));
        log.info("Pince arriere gauche = {}", nerellIOServiceRobot.pinceArriereGauche(false));
        log.info("Pince arriere centre = {}", nerellIOServiceRobot.pinceArriereCentre(false));
        log.info("Pince arriere droite = {}", nerellIOServiceRobot.pinceArriereDroite(false));
        log.info("Présence avant gauche = {}", nerellIOServiceRobot.presenceAvantGauche(false));
        log.info("Présence avant centre = {}", nerellIOServiceRobot.presenceAvantCentre(false));
        log.info("Présence avant droite = {}", nerellIOServiceRobot.presenceAvantDroite(false));
        log.info("Présence arriere gauche = {}", nerellIOServiceRobot.presenceArriereGauche(false));
        log.info("Présence arriere centre = {}", nerellIOServiceRobot.presenceArriereCentre(false));
        log.info("Présence arriere droite = {}", nerellIOServiceRobot.presenceArriereDroite(false));
    }

    @ShellMethod("Read ADC")
    @SneakyThrows
    public void readAdc() {
        for (int i = 0; i < 8; i++) {
            int value = adc.readCapteurValue((byte) i);
            log.info("ADC {}: {}", i, value);
        }
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

    @SneakyThrows
    @ShellMethod("Monitor ADC")
    public void monitorAdc(int duration) {
        startMonitoring();

        long end = System.currentTimeMillis() + duration * 1000L;

        do {
            int value1 = adc.readCapteurValue((byte) 1);
            int value2 = adc.readCapteurValue((byte) 5);
            int value3 = adc.readCapteurValue((byte) 4);
            int value4 = adc.readCapteurValue((byte) 0);

            log.info("{} {} {} {}", value1, value2, value3, value4);

            MonitorTimeSerie serie = new MonitorTimeSerie()
                    .measurementName("adc")
                    .addField("value1", value1)
                    .addField("value2", value2)
                    .addField("value3", value3)
                    .addField("value4", value4);

            monitoringWrapper.addTimeSeriePoint(serie);

            ThreadUtils.sleep(20);

        } while (System.currentTimeMillis() < end);

        endMonitoring();
    }

    /**
     * Avance de 1000mm tout en tracant les valeurs des GP2D
     *
     * @example Query Flux
     *
     * from(bucket: "robots")
     * |> range(start: v.timeRangeStart, stop: v.timeRangeStop)
     * |> filter(fn: (r) => r["_measurement"] == "adc")
     * |> filter(fn: (r) => r["_field"] == "x" or r["_field"] == "value1" or r["_field"] == "value2" or r["_field"] == "value3" or r["_field"] == "value4")
     * |> aggregateWindow(every: 100ms, fn: mean, createEmpty: false)
     * |> map(fn: (r) =>  ({ r with _value: if r._field == "x" then r._value else 230000.0 / r._value - 50.0 }))
     * |> yield(name: "mean")
     */
    @SneakyThrows
    @ShellMethod()
    @ShellMethodAvailability("alimentationOk")
    public void calibrationGp() {
        startMonitoring();

        wheelsEncoders.reset();
        rs.enableAsserv();

        trajectoryManager.setVitessePercent(10, 100);

        CompletableFuture.runAsync(() -> {
            try {
                trajectoryManager.avanceMM(1000);
            } catch (AvoidingException e) {
                throw new RuntimeException(e);
            }
        });

        do {
            double currentX = trajectoryManager.currentXMm();
            int value1 = adc.readCapteurValue((byte) 1);
            int value2 = adc.readCapteurValue((byte) 5);
            int value3 = adc.readCapteurValue((byte) 4);
            int value4 = adc.readCapteurValue((byte) 0);

            MonitorTimeSerie serie = new MonitorTimeSerie()
                    .measurementName("adc")
                    .addField("x", currentX)
                    .addField("value1", value1)
                    .addField("value2", value2)
                    .addField("value3", value3)
                    .addField("value4", value4);

            monitoringWrapper.addTimeSeriePoint(serie);

            ThreadUtils.sleep(20);

        } while (!trajectoryManager.isTrajetAtteint());

        endMonitoring();
        rs.disableAsserv();
    }

    @ShellMethod("Enable Electro Aimant")
    @ShellMethodAvailability("alimentationOk")
    public void enableElectroAimant() {
        nerellIOServiceRobot.enableElectroAimant();
    }

    @ShellMethod("Enable Electro Aimant")
    @ShellMethodAvailability("alimentationOk")
    public void disableElectroAimant() {
        nerellIOServiceRobot.disableElectroAimant();
    }

    @ShellMethod("Tourne solar wheel")
    @ShellMethodAvailability("alimentationOk")
    public void tourneSolarWheel(boolean avant, int speed) {
        if (avant) {
            nerellIOServiceRobot.tournePanneauBleu(speed);
        } else {
            nerellIOServiceRobot.tournePanneauJaune(speed);
        }
    }

    @ShellMethod("Stop solar wheel")
    @ShellMethodAvailability("alimentationOk")
    public void stopSolarWheel() {
        nerellIOServiceRobot.stopTournePanneau();
    }
}
