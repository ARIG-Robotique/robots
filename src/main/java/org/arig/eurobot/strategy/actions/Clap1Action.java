package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by gdepuille on 14/05/15.
 */
@Slf4j
@Component
public class Clap1Action implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private SD21Servos servos;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Clap 1";
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
        if (rs.getElapsedTime() > 60000) {
            return true;
        }

        return rs.isPied8Recupere() && !rs.isTapisPresent();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(2000 - 270, 270);
                mv.gotoOrientationDeg(60);
                servos.setPositionAndWait(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_CLAP);
                mv.gotoOrientationDeg(95);
            } else {
                mv.pathTo(2000 - 270, 3000 - 270);
                mv.gotoOrientationDeg(-60);
                servos.setPositionAndWait(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_CLAP);
                mv.gotoOrientationDeg(-95);
            }
            servos.setPosition(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT);
            servos.setPosition(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HAUT);
            rs.setClap1Fait(true);
            if (rs.getTeam() == Team.JAUNE) {
                rs.setGobeletClapJauneRecupere(true);
            } else {
                rs.setGobeletClapVertRecupere(true);
            }
            completed = true;
        } catch (ObstacleFoundException | AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        }
    }
}
