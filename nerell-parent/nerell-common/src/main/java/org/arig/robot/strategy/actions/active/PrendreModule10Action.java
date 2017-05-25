package org.arig.robot.strategy.actions.active;

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
public class PrendreModule10Action extends AbstractAction {

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
        return "Récuperation du Module 10";
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

        return Team.BLEU == rs.getTeam() && !rs.isModuleRecupere(10) && !ioService.presencePinceCentre();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.setModuleLunaireExpected(new ModuleLunaire(10, ModuleLunaire.Type.MONOCHROME));

            mv.pathTo(2800 + 280 * Math.cos(3 * Math.PI / 4), 600 + 280 * Math.sin(3 * Math.PI / 4));
            mv.alignFrontTo(2800, 600);
            mv.avanceMM(150);
            mv.reculeMM(150);
            mv.gotoOrientationDeg(135);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);
        } finally {
            completed = true;
            rs.setModuleRecupere(10);
        }
    }
}
