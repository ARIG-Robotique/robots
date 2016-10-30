package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 14/05/15.
 */
@Slf4j
@Component
public class Clap3Action implements IAction {

    @Autowired
    private Environment env;

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
        return "Clap 3";
    }

    @Override
    public int order() {
        return 2;
    }

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }
        return env.getProperty("strategy.collect.zone.adverse", Boolean.class);
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(2000 - 270, 2550);
                mv.gotoOrientationDeg(-90);
                servos.setPositionAndWait(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_CLAP);
                mv.gotoPointMM(2000 - 270, 2400);
            } else {
                mv.pathTo(2000 - 270, 3000 - 2550);
                mv.gotoOrientationDeg(90);
                servos.setPositionAndWait(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_CLAP);
                mv.gotoPointMM(2000 - 270, 3000 - 2400);
            }
            servos.setPosition(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT);
            servos.setPosition(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HAUT);
            rs.setClap3Fait(true);
            completed = true;
        } catch (ObstacleFoundException | AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(IConstantesNerellConfig.invalidActionTimeSecond);
        }
    }
}
