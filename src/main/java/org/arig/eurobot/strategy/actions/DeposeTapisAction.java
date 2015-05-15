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

import java.time.LocalDateTime;

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

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Dépose tapis";
    }

    @Override
    public int order() {
        return 12;
    }

    @Override
    public boolean isValid() {
        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }

        if (rs.getElapsedTime() > 60000) {
            return true;
        }

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
            mv.setVitesse(IConstantesRobot.vitessePath, IConstantesRobot.vitesseOrientation);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(700, 740);
                mv.gotoOrientationDeg(180);
                boolean hasProduitOnStart = ioService.produitDroit();
                if (!hasProduitOnStart) {
                    servosService.ouvrePriseDroite();
                }
                servos.setPositionAndWait(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_BAS);
                mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                rs.disableAvoidance();
                mv.avanceMM(400);
                servos.setPositionAndWait(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_OUVERT);
                servos.setPositionAndWait(IConstantesServos.BRAS_DROIT, IConstantesServos.BRAS_DROIT_HAUT);
                servos.setPosition(IConstantesServos.TAPIS_DROIT, IConstantesServos.TAPIS_DROIT_FERME);
                rs.setTapisPresent(false);
                servosService.priseProduitDroit();
                mv.reculeMM(100);
                mv.tourneDeg(90);
                rs.enableAvoidance();
                if (ioService.piedDroit()) {
                    mv.avanceMM(250);
                    servosService.ouvrePriseDroite();
                    mv.reculeMM(150);
                    servosService.priseProduitDroit();
                    mv.tourneDeg(-23);
                    mv.avanceMM(200);
                    rs.setPied4Recupere(true);
                }
            } else {
                mv.pathTo(700, 3000 - 740);
                mv.gotoOrientationDeg(180);
                boolean hasProduitOnStart = ioService.produitGauche();
                if (!hasProduitOnStart) {
                    servosService.ouvrePriseGauche();
                }
                servos.setPositionAndWait(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_BAS);
                mv.setVitesse(IConstantesRobot.vitesseMouvement, IConstantesRobot.vitesseOrientation);
                rs.disableAvoidance();
                mv.avanceMM(400);
                servos.setPositionAndWait(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_OUVERT);
                servos.setPositionAndWait(IConstantesServos.BRAS_GAUCHE, IConstantesServos.BRAS_GAUCHE_HAUT);
                servos.setPosition(IConstantesServos.TAPIS_GAUCHE, IConstantesServos.TAPIS_GAUCHE_FERME);
                rs.setTapisPresent(false);
                servosService.priseProduitGauche();
                mv.reculeMM(100);
                mv.tourneDeg(-90);
                rs.enableAvoidance();
                if (ioService.piedGauche()) {
                    mv.avanceMM(250);
                    servosService.ouvrePriseGauche();
                    mv.reculeMM(150);
                    servosService.priseProduitGauche();
                    mv.tourneDeg(23);
                    mv.avanceMM(200);
                    rs.setPied4Recupere(true);
                }
            }

            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(10);
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
            rs.enableAvoidance();
        }
    }
}
