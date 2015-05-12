package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
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
 * Created by gdepuille on 10/05/15.
 */
@Slf4j
@Component
public class DeposeTapisAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private IOService ioService;

    @Autowired
    private SD21Servos servos;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Dépose tapis action";
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public boolean isValid() {
        if (rs.getNbPied() < IConstantesRobot.nbPiedMax) {
            if (rs.getTeam() == Team.JAUNE) {
                return rs.isGobeletEscalierJauneRecupere() && !ioService.produitDroit();
            } else {
                return rs.isGobeletEscalierVertRecupere() && !ioService.produitGauche();
            }
        }

        return false;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(400, 800);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(700, 740);
                mv.gotoOrientationDeg(180);
                servosService.ouvrePriseDroite();
                servos.setPositionAndWait(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_BAS);
                mv.setVitesse(200, 800);
                mv.gotoPointMM(300, 740);
                servos.setPositionAndWait(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_OUVERT);
                servos.setPositionAndWait(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT);
                servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME);
                rs.setTapisPresent(false);
                servosService.priseProduitDroit();
                mv.reculeMM(50);
                mv.tourneDeg(90);
                if (ioService.piedDroit()) {
                    mv.avanceMM(200);
                    servosService.ouvrePriseDroite();
                    mv.reculeMM(150);
                    servosService.priseProduitDroit();
                    mv.tourneDeg(-30);
                    mv.avanceMM(150);
                    mv.gotoPointMM(300, 600);
                }
            } else {
                // TODO : Vert
            }

            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
        }
    }
}
