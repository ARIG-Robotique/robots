package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
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
 * Created by gdepuille on 13/05/15.
 */
@Slf4j
@Component
public class PriseGobeletCommunAction implements IAction {

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
        return "Collecte du gobelet commun";
    }

    @Override
    public int order() {
        return 4;
    }

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }
        return !rs.isGobeletCentraleRecupere() && (!ioService.produitGauche() || !ioService.produitDroit());
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            mv.pathTo(1250, 1500);

            double r = Math.sqrt(Math.pow(1650 - 1250, 2));
            double alpha = Math.asin(115 / r);

            if (!ioService.produitGauche()) {
                mv.alignFrontToAvecDecalage(1650, 1500, Math.toDegrees(-alpha));
                servosService.ouvrePriseGauche();
            } else {
                mv.alignFrontToAvecDecalage(1650, 1500, Math.toDegrees(alpha));
                servosService.ouvrePriseDroite();
            }
            mv.avanceMM(r * Math.cos(alpha) - 110);
            servosService.priseProduitGauche();
            servosService.priseProduitDroit();
            rs.setGobeletCentraleRecupere(true);
            completed = true;
        } catch (ObstacleFoundException | AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
            rs.setGobeletCentraleRecupere(false);
            servosService.priseProduitGauche();
            servosService.priseProduitDroit();
        }
    }
}
