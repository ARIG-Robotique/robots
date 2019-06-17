package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.system.RobotInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gdepuille on 14/10/16.
 */
@Slf4j
@RestController
@Profile(IConstantesConfig.profileMonitoring)
public class SystemController {

    @GetMapping("/system")
    public RobotInfo system() {
        return RobotInfo.getInstance();
    }
}
