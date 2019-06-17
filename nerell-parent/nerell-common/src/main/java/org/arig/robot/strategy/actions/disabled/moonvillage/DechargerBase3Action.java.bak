package org.arig.robot.strategy.actions.disabled.moonvillage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.EjectionModuleException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DechargerBase3Action extends AbstractAction {

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
        return "Déchargement des modules dans la base 3";
    }

    @Override
    public int order() {
        return Math.min(rs.nbPlacesDansBase(3), rs.nbModulesMagasin()) * 100 - 1;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return rs.hasModuleDansMagasin() && (rs.getElapsedTime() > 45000 || rs.nbModulesMagasin() >= 4);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.disableMagasin();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(1200, 1090);
            mv.gotoOrientationDeg(180);

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMMSansAngle(180);

            while (rs.hasModuleDansMagasin() && rs.canAddModuleDansBase(3)) {
                ejectionModuleService.ejectionModule();
                rs.addModuleDansBase(3);
                Thread.sleep(10);
            }

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.avanceMM(180);
            mv.gotoOrientationDeg(-90);

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } catch (EjectionModuleException e) {
            rs.setBaseFull(3);

        } finally {
            completed = !rs.canAddModuleDansBase(3);
            rs.enableMagasin();
        }
    }
}
