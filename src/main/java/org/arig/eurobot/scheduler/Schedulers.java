package org.arig.eurobot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.ServosServices;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.pathfinding.impl.MultiPathFinderImpl;
import org.arig.robot.system.pathfinding.impl.NoPathFinderImpl;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Chemin;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Component
public class Schedulers {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    private MultiPathFinderImpl pf;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private ServosServices servosServices;

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void robotManagerTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        } else {
            mouvementManager.stop();
        }
    }

    private boolean first = true;

    @Scheduled(fixedDelay = 3000)
    public void trajetTest() throws NoPathFoundException {
        if (rs.isMatchEnabled()) {
            if (first) {
                first = false;
                mouvementManager.gotoPointMM(365, 210, true);
                mouvementManager.waitMouvement();
            }

            int x = (int) conv.pulseToMm(position.getPt().getX());
            int y = (int) conv.pulseToMm(position.getPt().getY());
            Chemin c = pf.findPath(new Point(x / 10, y /10), new Point(90, 140));
            while (c.hasNext()) {
                Point p = c.next();
                mouvementManager.gotoPointMM(p.getX() * 10, p.getY() * 10, true);
                mouvementManager.waitMouvement();
            }

            x = (int) conv.pulseToMm(position.getPt().getX());
            y = (int) conv.pulseToMm(position.getPt().getY());
            c = pf.findPath(new Point(x / 10, y /10), new Point(25, 120));
            while (c.hasNext()) {
                Point p = c.next();
                mouvementManager.gotoPointMM(p.getX() * 10, p.getY() * 10, true);
                mouvementManager.waitMouvement();
            }

            x = (int) conv.pulseToMm(position.getPt().getX());
            y = (int) conv.pulseToMm(position.getPt().getY());
            c = pf.findPath(new Point(x / 10, y /10), new Point(70, 50));
            while (c.hasNext()) {
                Point p = c.next();
                mouvementManager.gotoPointMM(p.getX() * 10, p.getY() * 10, true);
                mouvementManager.waitMouvement();
            }

            x = (int) conv.pulseToMm(position.getPt().getX());
            y = (int) conv.pulseToMm(position.getPt().getY());
            c = pf.findPath(new Point(x / 10, y /10), new Point(25, 50));
            while (c.hasNext()) {
                Point p = c.next();
                mouvementManager.gotoPointMM(p.getX() * 10, p.getY() * 10, true);
                mouvementManager.waitMouvement();
            }
        }
    }

    @Scheduled(fixedDelay = 100)
    public void ascenseurTask() {
        if (rs.isMatchEnabled()) {
            servosServices.checkAscenseur();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void produitGaucheTask() {
        if (rs.isMatchEnabled()) {
            servosServices.checkProduitGauche();
        }
    }

    @Scheduled(fixedDelay = 100)
    public void produitDroitTask() {
        if (rs.isMatchEnabled()) {
            servosServices.checkProduitDroit();
        }
    }
}
