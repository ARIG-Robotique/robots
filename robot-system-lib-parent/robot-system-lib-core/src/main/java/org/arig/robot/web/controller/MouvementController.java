package org.arig.robot.web.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.ActionSuperviseur;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.LidarService;
import org.arig.robot.services.TrajectoryManager;
import org.arig.robot.strategy.StrategyManager;
import org.arig.robot.system.pathfinding.PathFinder;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/mouvement")
@Profile(ConstantesConfig.profileMonitoring)
public class MouvementController {

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private RobotConfig config;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private LidarService lidarService;

    @Autowired
    @Qualifier("trajectoryManager")
    private TrajectoryManager trajectoryManager;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private StrategyManager strategyManager;

    @Autowired
    private PathFinder pathFinder;

    @GetMapping
    public Map<String, Object> showPosition() {
        List<ActionSuperviseur> actions = strategyManager.actions().stream()
                .map(ActionSuperviseur::fromAction)
                .sorted(Comparator.comparingInt(ActionSuperviseur::getOrder).reversed())
                .collect(Collectors.toList());

        Map<String, Object> pos = new HashMap<>();
        pos.put("x", trajectoryManager.currentXMm());
        pos.put("y", trajectoryManager.currentYMm());
        pos.put("angle", trajectoryManager.currentAngleDeg());
        pos.put("targetMvt", trajectoryManager.getCurrentMouvement());
        pos.put("trajetAtteint", trajectoryManager.isTrajetAtteint());
        pos.put("trajetEnApproche", trajectoryManager.isTrajetEnApproche());
        pos.put("typeAsserv", cmdRobot.typeAsserv());
        pos.put("pointsLidar", new ArrayList<>(lidarService.getDetectedPointsMm()));
        pos.put("collisions", new ArrayList<>(lidarService.getCollisionsShape()));
        pos.put("strategy", rs.strategyDescription());
        pos.put("matchTime", rs.getElapsedTime());
        pos.put("score", rs.calculerPoints());
        pos.put("currentAction", rs.currentAction());
        pos.put("actions", actions);
        pos.put("gameStatus", rs.gameStatus());
        pos.put("gameFlags", rs.gameFlags());
        pos.put("gameConfigs", rs.gameConfigs());
        pos.put("scoreStatus", rs.scoreStatus());
        return pos;
    }

    @GetMapping(value = "/mask", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @SneakyThrows
    public byte[] getMask() {
        BufferedImage image = pathFinder.getWorkImage();
        if (image == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    @PostMapping(value = "/path")
    public void cheminVersPosition(@RequestParam("x") final double x,
                                   @RequestParam("y") final double y,
                                   @RequestParam("sens") final GotoOption sens
    ) throws NoPathFoundException, AvoidingException {
        trajectoryManager.pathTo(x, y, sens != null ? sens : GotoOption.AUTO);
    }

    @PostMapping(value = "/position")
    public void allerEnPosition(@RequestParam("x") final double x,
                                @RequestParam("y") final double y,
                                @RequestParam("sens") final GotoOption sens
    ) throws AvoidingException {
        trajectoryManager.gotoPoint(x, y, sens != null ? sens : GotoOption.AUTO);
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

    @PostMapping(value = "/calage")
    public void calage(@RequestParam("type") final TypeCalage type) throws AvoidingException {
        double angle = trajectoryManager.currentAngleDeg();
        double currX = trajectoryManager.currentXMm();
        double currY = trajectoryManager.currentYMm();

        // détermine ou on va se caller
        double dstToBorder;
        double finalAngle;
        Double finalX = null;
        Double finalY = null;
        if (type == TypeCalage.AVANT) {
            if (Math.abs(angle) >= 178) {
                dstToBorder = currX;
                finalAngle = 180;
                finalX = config.distanceCalageAvant();
            } else if (Math.abs(angle) <= 2) {
                dstToBorder = 3000 - currX;
                finalAngle = 0;
                finalX = 3000 - config.distanceCalageAvant();
            } else if (Math.abs(angle - 90) <= 2) {
                dstToBorder = 2000 - currY;
                finalAngle = 90;
                finalY = 2000 - config.distanceCalageAvant();
            } else if (Math.abs(angle + 90) <= 2) {
                dstToBorder = currY;
                finalAngle = -90;
                finalY = config.distanceCalageAvant();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
        } else if (type == TypeCalage.ARRIERE) {
            if (Math.abs(angle) >= 178) {
                dstToBorder = 3000 - currX;
                finalAngle = 180;
                finalX = 3000 - config.distanceCalageArriere();
            } else if (Math.abs(angle) <= 2) {
                dstToBorder = currX;
                finalAngle = 0;
                finalX = config.distanceCalageArriere();
            } else if (Math.abs(angle - 90) <= 2) {
                dstToBorder = currY;
                finalAngle = 90;
                finalY = config.distanceCalageArriere();
            } else if (Math.abs(angle + 90) <= 2) {
                dstToBorder = 2000 - currY;
                finalAngle = -90;
                finalY = 2000 - config.distanceCalageArriere();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }

        if (type == TypeCalage.AVANT) {
            double mvt = dstToBorder - config.distanceCalageAvant() - 10;
            if (mvt > 0) {
                rs.enableCalage(type, TypeCalage.FORCE);
                trajectoryManager.setVitessePercent(50, 100);
                trajectoryManager.avanceMM(mvt);
            }
        } else {
            double mvt = dstToBorder - config.distanceCalageArriere() - 10;
            if (mvt > 0) {
                rs.enableCalage(type, TypeCalage.FORCE);
                trajectoryManager.setVitessePercent(50, 100);
                trajectoryManager.reculeMM(mvt);
            }
        }

        if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
            return;
        }

        trajectoryManager.setVitessePercent(0, 100);
        rs.enableCalage(type);

        if (type == TypeCalage.AVANT) {
            trajectoryManager.avanceMMSansAngle(40);
        } else {
            trajectoryManager.reculeMMSansAngle(40);
        }

        position.setAngle(conv.degToPulse(finalAngle));
        if (finalX != null) {
            position.getPt().setX(conv.mmToPulse(finalX));
        }
        if (finalY != null) {
            position.getPt().setY(conv.mmToPulse(finalY));
        }

        trajectoryManager.setVitessePercent(50, 100);
    }
}
