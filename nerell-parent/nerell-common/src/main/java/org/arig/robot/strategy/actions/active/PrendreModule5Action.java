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
import org.arig.robot.services.ServosService;
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

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Récuperation du Module 5";
    }

    @Override
    public int order() {
        int val = 100;

        if (Team.JAUNE.equals(rs.getTeam())) {
            val += 1000;
        } else {
            val /= 10;
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
            rs.enablePinces();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.setModuleLunaireExpected(new ModuleLunaire(5, ModuleLunaire.Type.POLYCHROME));

            // prise directe depuis la zone de départ jaune
            if (Team.JAUNE == rs.getTeam()) {
                rs.disableAvoidance();

                mv.gotoPointMM(1000, 600);
            }
            // alignement pour prendre dans la pince droite depuis la droite
            else if (ioService.presencePinceCentre()) {
                mv.pathTo(1300, 580);

                // on révérifie que la pince centre est pas libre
                if (ioService.presencePinceCentre() || !servosService.isBrasAttente()) {
                    mv.gotoOrientationDeg(180);
                }
                // ou on passe en prise au centre
                else {
                    mv.alignFrontTo(1000, 600);
                }

                mv.gotoPointMM(1000, 600);
            }
            // prise directe au centre
            else {
                mv.pathTo(1000, 600);
            }

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            rs.enableAvoidance();
            completed = true;
        }
    }
}
