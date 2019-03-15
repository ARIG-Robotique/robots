package org.arig.robot.strategy.actions.disabled.moonvillage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrendreModules6Et9Action extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Récuperation du Module 6 ET du module 9";
    }

    @Override
    public int order() {
        int val = 200;

        if (Team.BLEU.equals(rs.getTeam())) {
            val += 1000;
        } else {
            val /= 10;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.BLEU == rs.getTeam() &&
                !rs.isModuleRecupere(6) &&
                !rs.isModuleRecupere(9) &&
                (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();
            rs.enablePinces();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.addModuleLunaireExpected(new ModuleLunaire(6, ModuleLunaire.Type.POLYCHROME));

            mv.gotoPointMM(2000, 600, false, false);

//            rs.enableAvoidance();

            rs.addModuleLunaireExpected(new ModuleLunaire(9, ModuleLunaire.Type.POLYCHROME));

            mv.gotoPointMM(2500 + 85 * Math.cos(3 * Math.PI / 4), 1100 + 85 * Math.sin(3 * Math.PI / 4));

            Thread.sleep(500);

//            mv.gotoOrientationDeg(-45);


        } catch (InterruptedException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            rs.enableAvoidance();
            rs.setModuleRecupere(6);
            rs.setModuleRecupere(9);
            completed = true;
        }
    }
}
