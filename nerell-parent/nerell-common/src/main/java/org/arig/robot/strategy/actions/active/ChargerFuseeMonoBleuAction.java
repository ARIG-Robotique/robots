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
import org.arig.robot.services.BrasService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChargerFuseeMonoBleuAction extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private BrasService brasService;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Chargement des modules de la fusée monochrome bleue";
    }

    @Override
    public int order() {
        return 400;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.BLEU == rs.getTeam() &&
                rs.nbModulesMagasin() <= IConstantesNerellConfig.nbModuleMax - 4 &&
                !ioService.presencePinceCentre() &&
                !rs.isFuseeMonochromeBleuRecupere();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(1965, 275);
            mv.gotoOrientationDeg(-170);

            for (int i = 0; i < 4; i++) {
                if (brasService.stockerModuleFusee()) {
                    rs.addModuleDansMagasin(ModuleLunaire.monochrome());
                } else {
                    break;
                }
            }

            servosService.homes();

            mv.gotoOrientationDeg(90);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);
        } finally {
            completed = true;
            rs.disablePinces();
            rs.setFuseeMonochromeBleuRecupere(true);
        }
    }
}
