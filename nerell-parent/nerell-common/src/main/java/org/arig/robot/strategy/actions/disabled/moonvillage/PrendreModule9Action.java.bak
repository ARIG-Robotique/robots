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
public class PrendreModule9Action extends AbstractAction {

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
        return "Récuperation du Module 9";
    }

    @Override
    public int order() {
        int val = 100;

        if (Team.BLEU.equals(rs.getTeam())) {
            val += 500;
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

        return !rs.isModuleRecupere(9) && (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.addModuleLunaireExpected(new ModuleLunaire(9, ModuleLunaire.Type.POLYCHROME));

            if (ioService.presencePinceCentre()) {
                log.info("Récupération du module 9 dans la pince droite");
                mv.pathTo(2500 - 180 - 100, 1185);
            } else {
                log.info("Récupération du module 9 dans la pince centre");
                mv.pathTo(2500 - 180 - 100, 1100);
            }
            mv.gotoOrientationDeg(0);
            mv.avanceMM(150);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            completed = true;
            rs.setModuleRecupere(9);
        }
    }
}
