package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.ActionSuperviseur;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.ILidarService;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private StrategyManager strategyManager;

    @GetMapping
    public Map<String, Object> showPosition() {
        List<ActionSuperviseur> actions = strategyManager.getActions().stream()
                .map(ActionSuperviseur::fromAction)
                .sorted(Comparator.comparingInt(ActionSuperviseur::getOrder).reversed())
                .collect(Collectors.toList());

        Map<String, Object> pos = new LinkedHashMap<>();
        pos.put("x", conv.pulseToMm(position.getPt().getX()));
        pos.put("y", conv.pulseToMm(position.getPt().getY()));
        pos.put("angle", conv.pulseToDeg(position.getAngle()));
        pos.put("targetMvt", trajectoryManager.getCurrentMouvement());
        pos.put("trajetAtteint", trajectoryManager.isTrajetAtteint());
        pos.put("trajetEnApproche", trajectoryManager.isTrajetEnApproche());
        pos.put("typeAsserv", cmdRobot.typeAsserv());
        pos.put("pointsLidar", new ArrayList<>(lidarService.getDetectedPointsMm()));
        pos.put("collisions", new ArrayList<>(lidarService.getCollisionsShape()));
        pos.put("matchTime", rs.getElapsedTime());
        pos.put("score", rs.calculerPoints());
        pos.put("currentAction", strategyManager.getCurrentAction());
        pos.put("actions", actions);
        pos.put("gameStatus", rs.gameStatus());
        pos.put("scoreStatus", rs.scoreStatus());
        return pos;
    }

    @PostMapping(value = "/path")
    public void cheminVersPosition(@RequestParam("x") final double x,
                                   @RequestParam("y") final double y,
                                   @RequestParam("sens") final SensDeplacement sens
    ) throws NoPathFoundException, AvoidingException {
        rs.enableAvoidance();
        trajectoryManager.pathTo(x, y, sens != null ? sens : SensDeplacement.AUTO);
        rs.disableAvoidance();
    }

    @PostMapping(value = "/position")
    public void allerEnPosition(@RequestParam("x") final double x,
                                @RequestParam("y") final double y,
                                @RequestParam("sens") final SensDeplacement sens
    ) throws AvoidingException {
        trajectoryManager.gotoPointMM(x, y, true, sens != null ? sens : SensDeplacement.AUTO);
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
    public void orientation(@RequestParam("angle") final double angle,
                            @RequestParam("sens") final SensRotation sens
    ) throws AvoidingException {
        trajectoryManager.gotoOrientationDeg(angle, sens != null ? sens : SensRotation.AUTO);
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
