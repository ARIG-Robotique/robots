package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.capteurs.TinyLidar;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("tinyLidar")
@AllArgsConstructor
public class TinyLidarCommands {

    private final IIOService ioService;
    private final TinyLidar distanceAvant;

    @ShellMethod("Distance tinyLidar facade")
    public void distanceFacade() {
        log.info("Distance avant : {}mm", distanceAvant.readValue());
    }
}
