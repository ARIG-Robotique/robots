package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.OdinOrdonanceur;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("match")
@AllArgsConstructor
public class OdinStartMatchCommands {

    private OdinOrdonanceur ordonanceur;

    @SneakyThrows
    @ShellMethod("Démarrer un match")
    public void start() {
        ordonanceur.run();
    }
}
