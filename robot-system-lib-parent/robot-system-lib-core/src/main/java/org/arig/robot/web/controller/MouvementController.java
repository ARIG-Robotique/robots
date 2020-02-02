package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Position;
import org.arig.robot.system.ILidarService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 22/12/14.
 */
@Slf4j
@RestController
@RequestMapping("/mouvement")
@Profile(IConstantesConfig.profileMonitoring)
public class MouvementController {

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private Position position;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private ILidarService lidarService;

    @Autowired
    @Qualifier("trajectoryManager")
    private ITrajectoryManager trajectoryManager;

    @GetMapping
    public Map<String, Object> showPosition() {
        Map<String, Object> pos = new LinkedHashMap<>();
        pos.put("x", conv.pulseToMm(position.getPt().getX()));
        pos.put("y", conv.pulseToMm(position.getPt().getY()));
        pos.put("angle", conv.pulseToDeg(position.getAngle()));
        pos.put("targetMvt", trajectoryManager.getCurrentMouvement());
        pos.put("trajetAtteint", trajectoryManager.isTrajetAtteint());
        pos.put("trajetEnApproche", trajectoryManager.isTrajetEnApproche());
        pos.put("typeAsserv", cmdRobot.typeAsserv());
        pos.put("pointsLidar", lidarService.getDetectedPointsMm());
        pos.put("collisions", lidarService.getCollisionsShape());
        pos.put("matchTime", rs.getElapsedTime());
        return pos;
    }

    @PostMapping(value = "/path")
    public void cheminVersPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) throws NoPathFoundException, AvoidingException {
        trajectoryManager.pathTo(x, y);
    }

    @PostMapping(value = "/position")
    public void allerEnPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) throws AvoidingException {
        trajectoryManager.gotoPointMM(x, y, true);
    }

    @PostMapping(value = "/face")
    public void alignFace(@RequestParam("x") final double x, @RequestParam("y") final double y) throws AvoidingException {
        trajectoryManager.alignFrontTo(x, y);
    }

    @PostMapping(value = "/dos")
    public void alignDos(@RequestParam("x") final double x, @RequestParam("y") final double y) throws AvoidingException {
        trajectoryManager.alignBackTo(x, y);
    }

    @PostMapping(value = "/orientation")
    public void orientation(@RequestParam("angle") final double angle) throws AvoidingException {
        trajectoryManager.gotoOrientationDeg(angle);
    }

    @PostMapping(value = "/tourne")
    public void tourne(@RequestParam("angle") final double angle) throws AvoidingException {
        trajectoryManager.tourneDeg(angle);
    }

    @PostMapping(value = "/avance")
    public void avance(@RequestParam("distance") final double distance) throws AvoidingException {
        trajectoryManager.avanceMM(distance);
    }

    @PostMapping(value = "/recule")
    public void recule(@RequestParam("distance") final double distance) throws AvoidingException {
        trajectoryManager.reculeMM(distance);
    }
}
