package org.arig.prehistobot.web;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.RobotManager;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gdepuille on 22/12/14.
 */
@Slf4j
@Profile("raspi")
@RestController
@RequestMapping("/position")
public class PositionController {

    @Autowired
    private Position position;

    @Autowired
    private RobotManager rm;

    @RequestMapping(method = RequestMethod.GET)
    public Position showPosition() {
        return position;
    }

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public void setPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) {
        rm.gotoPointMM(x, y, true);
    }

    @RequestMapping(value = "/avance", method = RequestMethod.GET)
    public void avance(@RequestParam("distance") final double distance) {
        rm.avanceMM(distance);
    }

    @RequestMapping(value = "/recule", method = RequestMethod.GET)
    public void recule(@RequestParam("distance") final double distance) {
        rm.reculeMM(distance);
    }

    @RequestMapping(value = "/tourne", method = RequestMethod.GET)
    public void tourne(@RequestParam("angle") final double angle) {
        rm.tourneDeg(angle);
    }
}
