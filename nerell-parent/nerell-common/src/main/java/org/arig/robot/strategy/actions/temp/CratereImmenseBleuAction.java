package org.arig.robot.strategy.actions.temp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
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
public class CratereImmenseBleuAction extends AbstractAction {

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
        return "Récupération des ressources dans le grand cratère proche de la base lunaire bleue";
    }

    @Override
    public int order() {
        int val = 100;

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

        return !rs.isCratereImmenseBleuRecupere() && !ioService.presenceBallesAspiration() && rs.isModuleRecupere(8);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(2275, 1755);

            servosService.aspirationMax();

            mv.gotoOrientationDeg(-75);

            Thread.sleep(1500);

            servosService.aspirationCratere();

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientationBasse);

            for (int i = 0; i < 2; i++) {
                mv.avanceMM(150);
                mv.tourneDeg(13);
            }

            servosService.aspirationFerme();
            servosService.waitAspiration();

            servosService.aspirationStop();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.gotoOrientationDeg(-90);

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);
        } finally {
            rs.setCratereImmenseBleuRecupere(true);
            rs.setHasPetitesBalles(true);
            completed = true;
        }
    }
}
