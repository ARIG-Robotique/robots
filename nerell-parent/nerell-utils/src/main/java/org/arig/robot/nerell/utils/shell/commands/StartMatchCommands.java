package org.arig.robot.nerell.utils.shell.commands;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;

@Slf4j
@ShellComponent
@ShellCommandGroup("match")
public class StartMatchCommands {

    @SneakyThrows
    @ShellMethod("Démarrer un match")
    public void start() throws IOException {
        // begin match
        Ordonanceur.getInstance().run();
    }
}
