package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.IOService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 11/05/15.
 */
@Slf4j
@Component
public class PriseGobeletEscalierJauneAction implements IAction {

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

    @Override
    public String name() {
        return "Prise gobelet escalier jaune action";
    }

    @Override
    public int order() {
        return (rs.getTeam() == Team.JAUNE) ? 500 : 200;
    }

    @Override
    public boolean isValid() {
        return !ioService.produitDroit() || !ioService.produitGauche();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(400, 800);
            mv.pathTo(1200, 910);
            mv.gotoOrientationDeg(180);
            mv.setVitesse(200, 800);
            if (!ioService.produitDroit()) {
                // On prend à droite
                servosService.ouvrePriseDroite();
                mv.gotoPointMM(900, 795);
            } else {
                // On prend à gauche
                servosService.ouvrePriseGauche();
                mv.gotoPointMM(900, 1025);
            }
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
            rs.setGobeletEscalierJauneRecupere(true);
            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
        }
    }
}
