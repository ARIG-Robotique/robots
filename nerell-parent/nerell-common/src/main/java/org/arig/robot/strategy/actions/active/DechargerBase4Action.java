package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.EjectionModuleException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DechargerBase4Action extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private EjectionModuleService ejectionModuleService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déchargement des modules dans la base 4";
    }

    @Override
    public int order() {
        int val = Math.min(rs.nbPlacesDansBase(4), rs.nbModulesMagasin()) * 100;

        if (Team.JAUNE == rs.getTeam()) {
            val /= 10;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return rs.getTeam() == Team.BLEU &&
                rs.hasModuleDansMagasin() &&
                rs.isModuleRecupere(6) &&
                rs.isModuleRecupere(7) &&
                rs.isModuleRecupere(9) &&
                rs.isModuleRecupere(10);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.disableMagasin();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            double x = 1500 + 900 * Math.cos(-Math.PI / 4) + 300 * Math.cos(-3 * Math.PI / 4);
            double y = 2000 + 900 * Math.sin(-Math.PI / 4) + 300 * Math.sin(-3 * Math.PI / 4);

            mv.pathTo(x, y);
            mv.gotoOrientationDeg(-135);

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMM(180);

            while (rs.hasModuleDansMagasin() && rs.canAddModuleDansBase(4)) {
                ejectionModuleService.ejectionModule();
                rs.addModuleDansBase(4);
                Thread.sleep(10);
            }

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.avanceMM(180);

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } catch (EjectionModuleException e) {
            rs.setBaseFull(4);

        } finally {
            completed = !rs.canAddModuleDansBase(4);
            rs.enableMagasin();
        }
    }
}
