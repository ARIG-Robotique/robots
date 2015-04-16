package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private MouvementManager mouvementManager;

    @RequestMapping(method = RequestMethod.GET)
    public Position showPosition() {
        return position;
    }

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public void setPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) {
        mouvementManager.gotoPointMM(x, y, true);
    }

    @RequestMapping(value = "/avance", method = RequestMethod.GET)
    public void avance(@RequestParam("distance") final double distance) {
        mouvementManager.avanceMM(distance);
    }

    @RequestMapping(value = "/recule", method = RequestMethod.GET)
    public void recule(@RequestParam("distance") final double distance) {
        mouvementManager.reculeMM(distance);
    }

    @RequestMapping(value = "/tourne", method = RequestMethod.GET)
    public void tourne(@RequestParam("angle") final double angle) {
        mouvementManager.tourneDeg(angle);
    }
}
