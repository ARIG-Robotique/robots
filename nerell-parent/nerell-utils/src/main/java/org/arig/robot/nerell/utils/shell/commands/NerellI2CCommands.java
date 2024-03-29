package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManager;
import org.arig.robot.communication.I2CMultiplexerDevice;
import org.arig.robot.exception.I2CException;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("I2C")
@AllArgsConstructor
public class NerellI2CCommands {

    private final I2CManager i2CManager;
    private final I2CMultiplexerDevice mux;

    @ShellMethod("Scan I2C")
    public void scanI2C() {
        log.info("Scan des devices I2C enregistré");
        try {
            i2CManager.executeScan();
        } catch (I2CException e) {
            log.error("Erreur lors du scan I2C", e);
        }
    }

    @ShellMethod("Selection du canal multiplexeur")
    public void selectMuxChannel(byte channel) {
        log.info("Selection du canal multiplexeur {}", channel);
        mux.selectChannel(channel);
    }
}
