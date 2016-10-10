package org.arig.eurobot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gdepuille on 07/05/15.
 */
@Slf4j
@Service
public class CalageBordureService {

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private IOService ioService;

    public void process() {
        if (ioService.buteeAvantDroit() && ioService.buteeAvantGauche()) {
            mouvementManager.setObstacleFound(true);
            mouvementManager.setRestartAfterObstacle(true);
        }
    }
}
