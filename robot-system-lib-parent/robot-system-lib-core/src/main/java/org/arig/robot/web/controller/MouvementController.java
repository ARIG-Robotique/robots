package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Position;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private Position position;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private IAvoidingService avoidingService;

    @Autowired
    @Qualifier("trajectoryManagerAsync")
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
        pos.put("pointsLidar", avoidingService.getDetectedPointsMmLidar());
        pos.put("pointsCapteurs", avoidingService.getDetectedPointsMmCapteurs());
        pos.put("collisions", avoidingService.getCollisionsShape());
        return pos;
    }

    @PostMapping(value = "/path")
    public void cheminVersPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) throws NoPathFoundException, RefreshPathFindingException, AvoidingException {
        trajectoryManager.pathTo(x, y);
    }

    @PostMapping(value = "/position")
    public void allerEnPosition(@RequestParam("x") final double x, @RequestParam("y") final double y) throws RefreshPathFindingException {
        trajectoryManager.gotoPointMM(x, y);
    }

    @PostMapping(value = "/face")
    public void alignFace(@RequestParam("x") final double x, @RequestParam("y") final double y) throws RefreshPathFindingException {
        trajectoryManager.alignFrontTo(x, y);
    }

    @PostMapping(value = "/dos")
    public void alignDos(@RequestParam("x") final double x, @RequestParam("y") final double y) throws RefreshPathFindingException {
        trajectoryManager.alignBackTo(x, y);
    }

    @PostMapping(value = "/orientation")
    public void orientation(@RequestParam("angle") final double angle) throws RefreshPathFindingException {
        trajectoryManager.gotoOrientationDeg(angle);
    }

    @PostMapping(value = "/tourne")
    public void tourne(@RequestParam("angle") final double angle) throws RefreshPathFindingException {
        trajectoryManager.tourneDeg(angle);
    }

    @PostMapping(value = "/avance")
    public void avance(@RequestParam("distance") final double distance) throws RefreshPathFindingException {
        trajectoryManager.avanceMM(distance);
    }

    @PostMapping(value = "/recule")
    public void recule(@RequestParam("distance") final double distance) throws RefreshPathFindingException {
        trajectoryManager.reculeMM(distance);
    }
}
