package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.Position;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 22/12/14.
 */
@Slf4j
@Profile(IConstantesConfig.profileMonitoring)
@RestController
@RequestMapping("/mouvement")
public class MouvementController {

    @Autowired
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private TrajectoryManager trajectoryManager;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> showPosition() {
        Map<String, Object> pos = new LinkedHashMap<>();
        pos.put("x", conv.pulseToMm(position.getPt().getX()));
        pos.put("y", conv.pulseToMm(position.getPt().getY()));
        pos.put("angle", conv.pulseToDeg(position.getAngle()));
        pos.put("trajetAtteint", trajectoryManager.isTrajetAtteint());
        pos.put("trajetEnApproche", trajectoryManager.isTrajetEnApproche());
        return pos;
    }

    @RequestMapping(value = "/path", method = RequestMethod.POST)
    public void cheminVersPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) throws NoPathFoundException, ObstacleFoundException, AvoidingException {
        trajectoryManager.pathTo(x, y);
    }

    @RequestMapping(value = "/position", method = RequestMethod.POST)
    public void allerEnPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) throws ObstacleFoundException {
        trajectoryManager.gotoPointMM(x, y, true);
    }

    @RequestMapping(value = "/face", method = RequestMethod.POST)
    public void alignFace(@RequestParam("x") final double x, @RequestParam("y") final double y) throws ObstacleFoundException {
        trajectoryManager.alignFrontTo(x, y);
    }

    @RequestMapping(value = "/dos", method = RequestMethod.POST)
    public void alignDos(@RequestParam("x") final double x, @RequestParam("y") final double y) throws ObstacleFoundException {
        trajectoryManager.alignBackTo(x, y);
    }

    @RequestMapping(value = "/orientation", method = RequestMethod.POST)
    public void orientation(@RequestParam("angle") final double angle) throws ObstacleFoundException {
        trajectoryManager.gotoOrientationDeg(angle);
    }

    @RequestMapping(value = "/tourne", method = RequestMethod.POST)
    public void tourne(@RequestParam("angle") final double angle) throws ObstacleFoundException {
        trajectoryManager.tourneDeg(angle);
    }

    @RequestMapping(value = "/avance", method = RequestMethod.POST)
    public void avance(@RequestParam("distance") final double distance) throws ObstacleFoundException {
        trajectoryManager.avanceMM(distance);
    }

    @RequestMapping(value = "/recule", method = RequestMethod.POST)
    public void recule(@RequestParam("distance") final double distance) throws ObstacleFoundException {
        trajectoryManager.reculeMM(distance);
    }
}
