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
public class PrendreModule6Action extends AbstractAction {

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
        return "Récuperation du Module 6";
    }

    @Override
    public int order() {
        int val = 20;

        if (Team.BLEU.equals(rs.getTeam())) {
            val += 1000;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        return !rs.isModuleRecupere(6) && (!ioService.presencePinceCentre() || !ioService.presencePinceDroite());
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.setModuleLunaireExpected(new ModuleLunaire(6, ModuleLunaire.Type.POLYCHROME));

            if (Team.BLEU == rs.getTeam()) {
                mv.gotoOrientationDeg(Math.toDegrees(Math.atan2(600 - 165, 2000 - 2110)));
                mv.avanceMM(500);

            } else {
                // TODO
                if (!ioService.presencePinceDroite()) {
                    // alignement pour prendre dans la pince droite depuis la gauche
                    mv.pathTo(1700, 680);
                    mv.gotoOrientationDeg(0);
                    mv.avanceMM(300);
                } else {
                    mv.pathTo(2000, 600);
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
