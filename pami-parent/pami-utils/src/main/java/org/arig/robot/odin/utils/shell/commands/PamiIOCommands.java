package org.arig.robot.odin.utils.shell.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.PamiIOServiceRobot;
import org.arig.robot.services.PamiRobotServosService;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@AllArgsConstructor
public class PamiIOCommands {

    private final PamiIOServiceRobot odinIOServiceRobot;
    private final PamiRobotServosService odinServosService;


}
