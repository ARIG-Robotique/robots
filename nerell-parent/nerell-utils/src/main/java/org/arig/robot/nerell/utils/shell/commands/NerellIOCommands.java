package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.I2CException;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.NerellIOServiceRobot;
import org.arig.robot.system.capteurs.i2c.I2CAdcAnalogInput;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class NerellIOCommands {

    private final NerellIOServiceRobot nerellIOServiceRobot;
    private final AbstractEnergyService energyService;
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
    public void readAdc() throws I2CException {
        for (int i = 0; i < 8; i++) {
            int value = adc.readCapteurValue((byte) i);
            log.info("ADC {}: raw={} 10bits={}", i, value, convertTo10BitValue(value));
        }
    }

    private int convertTo10BitValue(int value) {
        short min10Bit = 0, min12Bit = 0, max10Bit = 1023, max12Bit = 4095;
        return (value - min12Bit) * (max10Bit - min10Bit) / (max12Bit - min12Bit) + min12Bit;
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
