package org.arig.eurobot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gdepuille on 07/05/15.
 */
@Slf4j
public class BasicAvoidingService extends AbstractAvoidingService {

    @Autowired
    private MouvementManager mouvementManager;

    private boolean currentObstacle = false;


    @Override
    protected void processWithPoints(List<Point> points) {
        if (!points.isEmpty() && !currentObstacle) {
            log.info("Attente que l'obstacle sans aille ...");
            mouvementManager.setObstacleFound(true);
            currentObstacle = true;
        }
        if (points.isEmpty() && currentObstacle) {
            log.info("L'obstacle à disparu on relance le cycle.");
            // 3.4 On relance le bouzin
            mouvementManager.setObstacleFound(false);
            currentObstacle = false;
        }
    }
}
