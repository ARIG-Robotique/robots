package org.arig.robot.strategy.actions.active;

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
public class CratereZoneDepartBleuAction extends AbstractAction {

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
        return "Récupération des ressources dans le petit cratère proche de la zone de départ bleue";
    }

    @Override
    public int order() {
        int val = 150;

        if (Team.BLEU == rs.getTeam()) {
            val += 500;
        } else {
            val /= 10;
        }

        return val;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return !rs.isCratereZoneDepartBleuRecupere() && !ioService.presenceBallesAspiration();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(2000, 680);

            servosService.aspirationMax();

            mv.gotoOrientationDeg(Math.toDegrees(2 * Math.PI / 3) - 270);

            Thread.sleep(1500);

            servosService.aspirationCratere();

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            mv.reculeMM(300);
            mv.avanceMM(350);

            servosService.aspirationFerme();
            servosService.waitAspiration();

            servosService.aspirationStop();

            completed = true;
            rs.setCratereZoneDepartBleuRecupere(true);
            rs.setHasPetitesBalles(false);

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);
        }
    }
}
