package org.arig.robot.strategy.actions.temp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DechargerBase2Action extends AbstractAction {

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
        return "Déchargement des modules dans la base 2";
    }

    @Override
    public int order() {
        return Math.max(rs.nbPlacesDansBase(2), rs.nbModulesMagasin()) * 100;
    }

    @Override
    public boolean isValid() {
        return rs.hasModuleDansMagasin();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            double x = 1500 + 890 * Math.cos(-3 * Math.PI / 4) + 298 * Math.cos(3 * Math.PI / 4);
            double y = 2000 + 890 * Math.sin(-3 * Math.PI / 4) + 298 * Math.sin(3 * Math.PI / 4);

            mv.pathTo(x, y);
            mv.gotoOrientationDeg(135);

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMM(180);

            while (rs.hasNextModule() && rs.canAddModuleDansBase(2)) {
                ejectionModuleService.ejectionModule();
                rs.addModuleDansBase(2);
            }

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.avanceMM(165);
            mv.gotoOrientationDeg(-135);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = !rs.canAddModuleDansBase(2);
        }
    }
}
