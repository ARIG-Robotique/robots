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
 * Created by gdepuille on 11/05/15.
 */
@Slf4j
@Component
public class PriseGobeletEscalierVertAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private IOService ioService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Prise gobelet escalier vert";
    }

    @Override
    public int order() {
        return (rs.getTeam() == Team.VERT) ? 600 : 0;
    }

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }
        return !ioService.produitDroit() || !ioService.produitGauche();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            mv.pathTo(1200, 3000 - 910);
            mv.gotoOrientationDeg(180);
            if (!ioService.produitDroit()) {
                // On prend à droite
                servosService.ouvrePriseDroite();
                mv.gotoPointMM(900, 3000 - 795);
            } else {
                // On prend à gauche
                servosService.ouvrePriseGauche();
                mv.gotoPointMM(900, 3000 - 1025);
            }
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
            rs.setGobeletEscalierVertRecupere(true);
            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
        }
    }
}
