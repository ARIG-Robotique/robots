package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
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
public class CollectePied3Action implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Collecte du pied 3";
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
        return !rs.isPied3Recupere() && rs.getNbPied() < IConstantesRobot.nbPiedMax;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1770, 1100);
            } else {
                mv.pathTo(1355, 3000 - 1100);
            }
            rs.setPied3Recupere(true);
            completed = true;
        } catch (AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        }
    }
}
