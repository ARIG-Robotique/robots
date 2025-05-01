package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.filters.sensors.GP2DPhantomFilter;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
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

    @ShellMethod("Read all IOs")
    public void readAllIO() {
        log.info("AU = {}", nerellIOServiceRobot.auOk());
        log.info("Tirette = {}", nerellIOServiceRobot.tirette());
        log.info("================== FIXE ==================");
        log.info("Calage avant gauche = {}", nerellIOServiceRobot.calageAvantGauche());
        log.info("Calage avant droit = {}", nerellIOServiceRobot.calageAvantDroit());
        log.info("Calage arriere gauche = {}", nerellIOServiceRobot.calageArriereGauche());
        log.info("Calage arriere droit = {}", nerellIOServiceRobot.calageArriereDroit());
        log.info("Stock avant gauche = {}", nerellIOServiceRobot.stockAvantGauche(false));
        log.info("Stock avant droit = {}", nerellIOServiceRobot.stockAvantDroite(false));
        log.info("Stock arriere gauche = {}", nerellIOServiceRobot.stockArriereGauche(false));
        log.info("Stock arriere droit = {}", nerellIOServiceRobot.stockArriereDroite(false));
        log.info("================= MOBILE =================");
        log.info("Pince avant gauche = {}", nerellIOServiceRobot.pinceAvantGauche(false));
        log.info("Pince avant droite = {}", nerellIOServiceRobot.pinceAvantDroite(false));
        log.info("Pince arriere gauche = {}", nerellIOServiceRobot.pinceArriereGauche(false));
        log.info("Pince arriere droite = {}", nerellIOServiceRobot.pinceArriereDroite(false));
        log.info("Tiroir avant haut = {}", nerellIOServiceRobot.tiroirAvantHaut(false));
        log.info("Tiroir avant bas = {}", nerellIOServiceRobot.tiroirAvantBas(false));
        log.info("Tiroir arriere haut = {}", nerellIOServiceRobot.tiroirArriereHaut(false));
        log.info("Tiroir arriere bas = {}", nerellIOServiceRobot.tiroirArriereBas(false));
    }
}
