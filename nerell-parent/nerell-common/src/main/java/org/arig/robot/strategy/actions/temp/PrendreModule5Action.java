package org.arig.robot.strategy.actions.temp;

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
public class PrendreModule5Action extends AbstractAction {

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
        return "Récuperation du Module 5";
    }

    @Override
    public int order() {
        int val = 20;

        if (Team.JAUNE.equals(rs.getTeam())) {
            val += 1000;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        return !rs.isModuleRecupere(5) && (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.setModuleLunaireExpected(new ModuleLunaire(5, ModuleLunaire.Type.POLYCHROME));

            if (Team.JAUNE == rs.getTeam()) {
                mv.gotoOrientationDeg(Math.toDegrees(Math.atan2(600-165, 1000-890)));
                mv.avanceMM(500);
            }
            else {
                // TODO
                if (!ioService.presencePinceDroite()) {
                    // alignement pour prendre dans la pince droite depuis la droite
                    mv.pathTo(1300, 580);
                    mv.gotoOrientationDeg(180);
                    mv.avanceMM(300);
                } else {
                    mv.pathTo(1000, 600);
                }
            }

            if (rs.getModuleLunaireExpected() == null) {
                completed = true;
            }

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
