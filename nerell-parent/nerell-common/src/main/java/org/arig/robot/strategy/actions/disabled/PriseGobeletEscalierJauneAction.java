package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author gdepuille on 11/05/15.
 */
@Slf4j
@Component
public class PriseGobeletEscalierJauneAction implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private Environment env;

    @Getter
    private boolean completed = false;

    private LocalDateTime validTime = LocalDateTime.now();

    @Override
    public String name() {
        return "Prise gobelet escalier jaune";
    }

    @Override
    public int order() {
        return (rs.getTeam() == Team.JAUNE) ? 600 : -4;
    }

    @Override
    public boolean isValid() {
        boolean adverseZoneEnabled = env.getProperty("strategy.collect.zone.adverse", Boolean.class);
        if (rs.getTeam() == Team.VERT && !adverseZoneEnabled) {
            return false;
        }

        if (validTime.isAfter(LocalDateTime.now())) {
            return false;
        }
        return !ioService.produitDroit() || !ioService.produitGauche();
    }

    @Override
    public void execute() {
        boolean droite = false;
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(1200, 910);

            double r = Math.sqrt(Math.pow(830 - 1200, 2));
            double alpha = Math.asin(115 / r);

            if (!ioService.produitGauche()) {
                mv.alignFrontToAvecDecalage(830, 910, Math.toDegrees(-alpha));
                servosService.ouvrePriseGauche();
            } else {
                mv.alignFrontToAvecDecalage(830, 910, Math.toDegrees(alpha));
                servosService.ouvrePriseDroite();
                droite = true;
            }
            mv.avanceMM(r * Math.cos(alpha) - 110);
            rs.setGobeletEscalierJauneRecupere(true);
            completed = true;
        } catch (ObstacleFoundException | AvoidingException | NoPathFoundException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            validTime = LocalDateTime.now().plusSeconds(IConstantesNerellConfig.invalidActionTimeSecond);;
            rs.setGobeletEscalierJauneRecupere(false);
        } finally {
            if (droite) {
                servosService.priseProduitDroit();
            } else {
                servosService.priseProduitGauche();
            }
        }
    }
}