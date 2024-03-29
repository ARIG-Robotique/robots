package org.arig.robot.nerell.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CMultiplexerDevice;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("Multiplexeur")
@AllArgsConstructor
public class NerellMuxCommands {

    private final I2CMultiplexerDevice mux;

    @ShellMethod("Selection du canal multiplexeur")
    public void selectMuxChannel(byte channel) {
        log.info("Selection du canal multiplexeur {}", channel);
        mux.selectChannel(channel);
    }
}
