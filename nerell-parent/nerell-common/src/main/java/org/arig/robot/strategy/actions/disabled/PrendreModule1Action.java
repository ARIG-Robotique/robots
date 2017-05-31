package org.arig.robot.strategy.actions.disabled;

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
public class PrendreModule1Action extends AbstractAction {

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
        return "Récuperation du Module 1";
    }

    @Override
    public int order() {
        return 100;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.JAUNE == rs.getTeam() &&
                !rs.isModuleRecupere(1) &&
                (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.addModuleLunaireExpected(new ModuleLunaire(1, ModuleLunaire.Type.MONOCHROME));

            mv.pathTo(200 + 280 * Math.cos(Math.PI / 4), 600 + 280 * Math.sin(Math.PI / 4));

            if (ioService.presencePinceCentre()) {
                mv.alignFrontTo(200 + 85 * Math.cos(-Math.PI / 4), 600 + 85 * Math.sin(-Math.PI / 4));
            } else {
                mv.alignFrontTo(200, 600);
            }

            mv.avanceMM(150);
            mv.reculeMM(100);
            mv.gotoOrientationDeg(45);

            completed = true;
            rs.setModuleRecupere(1);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        }
    }
}
