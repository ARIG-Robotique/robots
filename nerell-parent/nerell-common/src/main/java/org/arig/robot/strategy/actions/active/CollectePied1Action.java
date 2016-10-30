package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 14/05/15.
 */
@Slf4j
@Component
public class CollectePied1Action implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Collecte du pied 1";
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
        return !rs.isPied1Recupere() && rs.getNbPied() < IConstantesNerellConfig.nbPiedMax;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1355, 870);
            } else {
                mv.pathTo(1355, 3000 - 870);
            }
            rs.setPied1Recupere(true);
            completed = true;
        } catch (AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(IConstantesNerellConfig.invalidActionTimeSecond);
        }
    }
}