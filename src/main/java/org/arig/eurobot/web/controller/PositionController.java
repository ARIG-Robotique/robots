package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesSpringConfig;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by gdepuille on 22/12/14.
 */
@Slf4j
@Profile(IConstantesSpringConfig.profileMonitoring)
@RestController
@RequestMapping("/position")
public class PositionController {

    @Autowired
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private MouvementManager mouvementManager;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> showPosition() {
        Map<String, Object> pos = new LinkedHashMap<>();
        pos.put("x", conv.pulseToMm(position.getPt().getX()));
        pos.put("y", conv.pulseToMm(position.getPt().getY()));
        pos.put("angle", conv.pulseToDeg(position.getAngle()));
        pos.put("trajetAtteint", mouvementManager.isTrajetAtteint());
        pos.put("trajetEnApproche", mouvementManager.isTrajetEnApproche());
        return pos;
    }

    @RequestMapping(value = "/go", method = RequestMethod.GET)
    public void va(@RequestParam("x") final double x, @RequestParam("y") final double y) {
        mouvementManager.gotoPointMM(x, y, true);
    }

    @RequestMapping(value = "/face", method = RequestMethod.GET)
    public void alignFace(@RequestParam("x") final double x, @RequestParam("y") final double y) {
        mouvementManager.alignFrontTo(x, y);
    }

    @RequestMapping(value = "/dos", method = RequestMethod.GET)
    public void alignDos(@RequestParam("x") final double x, @RequestParam("y") final double y) {
        mouvementManager.alignBackTo(x, y);
    }

    @RequestMapping(value = "/tourne", method = RequestMethod.GET)
    public void tourne(@RequestParam("angle") final double angle) {
        mouvementManager.tourneDeg(angle);
    }

    @RequestMapping(value = "/avance", method = RequestMethod.GET)
    public void avance(@RequestParam("distance") final double distance) {
        mouvementManager.avanceMM(distance);
    }

    @RequestMapping(value = "/recule", method = RequestMethod.GET)
    public void recule(@RequestParam("distance") final double distance) {
        mouvementManager.reculeMM(distance);
    }
}
