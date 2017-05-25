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
public class PrendreModule2Action extends AbstractAction {

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
        return "Récuperation du Module 2";
    }

    @Override
    public int order() {
        int val = 100;

        if (Team.JAUNE.equals(rs.getTeam())) {
            val += 500;
        } else {
            val /= 10;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        return !rs.isModuleRecupere(2) && (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.setModuleLunaireExpected(new ModuleLunaire(2, ModuleLunaire.Type.POLYCHROME));

            if (ioService.presencePinceCentre()) {
                log.info("Récupération du module 2 dans la pince droite");
                mv.pathTo(500 + 180 + 100, 1015);
            } else {
                log.info("Récupération du module 2 dans la pince centre");
                mv.pathTo(500 + 180 + 100, 1100);
            }

            mv.gotoOrientationDeg(180);
            mv.avanceMM(150);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = true;
            rs.setModuleRecupere(2);
        }
    }
}
