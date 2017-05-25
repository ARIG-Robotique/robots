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
import org.arig.robot.services.BrasService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChargerFuseePolyJauneAction extends AbstractAction {

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
        return "Chargement des modules de la fusée polychrome jaune";
    }

    @Override
    public int order() {
        int val = 400;

        if (Team.BLEU == rs.getTeam()) {
            val /= 10;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return rs.nbModulesMagasin() <= IConstantesNerellConfig.nbModuleMax - 4 &&
                !ioService.presencePinceCentre() &&
                !rs.isFuseePolychromeJauneRecupere();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(325, 1280);
            mv.gotoOrientationDeg(120);
            mv.avanceMM(30);

            while (ioService.presenceFusee()) {
                brasService.stockerModuleFusee();
                rs.addModuleDansMagasin(ModuleLunaire.polychrome());
            }

            servosService.brasPincesFermes();

            mv.gotoOrientationDeg(0);
            mv.avanceMM(100);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);
        } finally {
            completed = true;
            rs.disablePinces();
            rs.setFuseePolychromeJauneRecupere(true);
        }
    }
}
