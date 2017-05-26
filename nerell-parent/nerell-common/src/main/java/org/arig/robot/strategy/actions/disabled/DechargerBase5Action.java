package org.arig.robot.strategy.actions.disabled;

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
public class DechargerBase5Action extends AbstractAction {

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
        return "Déchargement des modules dans la base 5";
    }

    @Override
    public int order() {
        return Math.max(rs.nbPlacesDansBase(5), rs.nbModulesMagasin()) * 100;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.BLEU == rs.getTeam() && rs.hasModuleDansMagasin();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(2660, 730, false);
            mv.gotoPointMM(2920 - 298, 700 - 90);
            mv.gotoOrientationDeg(180);

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMM(180);

            while (rs.hasModuleDansMagasin() && rs.canAddModuleDansBase(5)) {
                ejectionModuleService.ejectionModule();
                rs.addModuleDansBase(5);
            }

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);

        } catch (EjectionModuleException e) {
            rs.setBaseFull(5);

        } finally {
            completed = !rs.canAddModuleDansBase(5);

            try {
                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

                mv.avanceMM(180);

            } catch (RefreshPathFindingException e) {
                log.error(e.getMessage());
            }
        }
    }
}
