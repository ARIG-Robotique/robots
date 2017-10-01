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
public class PrendreModule1EtTransfertRessourcesAction extends AbstractAction {

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
        return "Récuperation du Module 1 et transfert des ressources dans Elfa";
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.JAUNE == rs.getTeam() &&
                !rs.isModuleRecupere(1) &&
                !ioService.presencePinceCentre() &&
                ioService.presenceBallesAspiration();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            rs.enablePinces();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.addModuleLunaireExpected(new ModuleLunaire(1, ModuleLunaire.Type.MONOCHROME));

            mv.pathTo(200 + 280 * Math.cos(Math.PI / 4), 600 + 280 * Math.sin(Math.PI / 4));

            mv.alignFrontTo(200, 600);

            mv.avanceMM(150);
            mv.reculeMM(100);

            // là on a pris le module 1
            rs.setModuleRecupere(1);

            mv.alignFrontTo(320, 690);
            mv.gotoPointMM(320, 690);

            servosService.aspirationMax();

            if (rs.isHasPetitesBalles()) {
                mv.gotoOrientationDeg(145);
            } else {
                mv.gotoOrientationDeg(180 - 16);
            }

            Thread.sleep(1500);

            servosService.aspirationTransfert();
            servosService.waitAspiration();

            servosService.aspirationStop();
            Thread.sleep(2000);

            servosService.aspirationFerme();
            servosService.waitAspiration();

            rs.addTransfertElfa();

            completed = true;

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        }
    }
}