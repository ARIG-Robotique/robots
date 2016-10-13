package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesRobot;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 06/05/15.
 */
@Slf4j
@Component
public class DeposeGobeletSalleClapAction implements IAction {

    @Autowired
    private Environment env;

    @Autowired
    private MouvementManager mv;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IOService ioService;

    @Getter
    private boolean completed = false;

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Dépose spot dans la salle principale";
    }

    @Override
    public int order() {
        if (rs.getElapsedTime() > 60000) {
            return 3;
        }
        return 2;
    }

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }

        boolean adverseZoneEnabled = env.getProperty("strategy.collect.zone.adverse", Boolean.class);
        return adverseZoneEnabled && (ioService.gobeletDroit() || ioService.gobeletGauche());
    }

    @Override
    public void execute() {
        boolean droite = false;
        boolean gauche = false;
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1500, 2500);
                mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1420, 2600);
            } else {
                mv.pathTo(1500, 500);
                mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                mv.gotoPointMM(1420, 400);
            }

                if (ioService.gobeletDroit()) {
                    servosService.deposeProduitDroitFinMatch();
                    droite = true;
                } else if (ioService.gobeletGauche()) {
                    servosService.deposeProduitGaucheFinMatch();
                    gauche = true;
                }

                mv.reculeMM(200);
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            DeposeGobeletSalleClapAction.log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(IConstantesRobot.invalidActionTimeSecond);
        } finally {
            rs.enableAvoidance();
            if (droite) {
                servosService.priseProduitDroit();
            }
            if (gauche) {
                servosService.priseProduitGauche();
            }
        }
    }
}
