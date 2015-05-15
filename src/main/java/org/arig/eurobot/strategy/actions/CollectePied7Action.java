package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.IOService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by gdepuille on 14/05/15.
 */
@Slf4j
//@Component
public class CollectePied7Action implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IOService ioService;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Collecte du pied 7";
    }

    @Override
    public int order() {
        return 5;
    }

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }
        return !rs.isPied7Recupere() && rs.getNbPied() < IConstantesRobot.nbPiedMax
            && ((rs.getTeam() == Team.JAUNE) ? rs.isGobeletClapJauneRecupere() : rs.isGobeletClapVertRecupere())
            && ((rs.getTeam() == Team.JAUNE) ? !ioService.produitDroit() : !ioService.produitGauche());
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1550, 290);
                rs.disableAvoidance();
                servosService.initProduitDroit();
                mv.alignFrontTo(1650, 190);
                mv.gotoPointMM(1650, 190);
            } else {
                mv.pathTo(1550, 3000 - 290);
                rs.disableAvoidance();
                servosService.initProduitGauche();
                mv.alignFrontTo(1650, 3000 - 190);
                mv.gotoPointMM(1650, 3000 - 190);
            }
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException e) {
                log.warn("Erreur d'attente dans la prise du pied : {}", e.toString());
            }
            mv.reculeMM(200);
            rs.setPied7Recupere(true);
            completed = true;
        } catch (ObstacleFoundException | AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            rs.enableAvoidance();
        }
    }
}
