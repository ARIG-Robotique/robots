package org.arig.robot.strategy.actions.disabled.moonvillage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrendreModule4Action extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Récuperation du Module 4";
    }

    @Override
    public int order() {
        int val = 100-1;

        if (Team.BLEU == rs.getTeam()) {
            val/= 10;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.JAUNE == rs.getTeam() &&
                !rs.isModuleRecupere(4) &&
                (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();
            rs.enablePinces();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.addModuleLunaireExpected(new ModuleLunaire(4, ModuleLunaire.Type.POLYCHROME));

            double offsetX = 0, offsetY = 0;

            if (ioService.presencePinceCentre()) {
                offsetX = 85 * Math.cos(3 * Math.PI / 4);
                offsetY = 85 * Math.sin(3 * Math.PI / 4);
            }

            mv.pathTo(
                    900 + 280 * Math.cos(-3 * Math.PI / 4) + offsetX,
                    1400 + 280 * Math.sin(-3 * Math.PI / 4) + offsetY
            );
            mv.alignFrontTo(900 + offsetX, 1400 + offsetY);
            mv.gotoPointMM(
                    900 - 140 * Math.cos(Math.PI / 4) + offsetX,
                    1400 - 140 * Math.sin(Math.PI / 4) + offsetY
            );

            Thread.sleep(400);

            mv.reculeMM(100);

            completed = true;
            rs.setModuleRecupere(4);

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        }
    }
}
