package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@AllArgsConstructor
public class TeamCommand {

    private final RobotStatus rs;

    @ShellMethod
    public void team(@NotNull Team team) {
        rs.setTeam(team);
        log.info("Equipe slectionné : {}", team.name());
    }
}
