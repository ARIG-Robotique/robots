package org.arig.robot.strategy.actions.disabled.moonvillage;

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
public class CratereZoneDepartJauneAction extends AbstractAction {

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
        return "Récupération des ressources dans le petit cratère proche de la zone de départ jaune";
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return Team.JAUNE == rs.getTeam() &&
                !rs.isCratereZoneDepartJauneRecupere() && !ioService.presenceBallesAspiration();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            // tangeante au centre du cratère à une distance de 2*180 et un angle de PI/3 avec un recul de 100
            //double x = 650 + (90 + 180) * Math.cos(Math.PI / 3) + 100 * Math.cos(Math.PI / 3 - Math.PI / 2);
            //double y = 540 + (90 + 180) * Math.sin(Math.PI / 3) + 100 * Math.sin(Math.PI / 3 - Math.PI / 2);

            /* version aller-retour
            mv.pathTo(940, 720);

            servosService.aspirationMax();

            mv.gotoOrientationDeg(Math.toDegrees(Math.PI / 3) + 90);

            Thread.sleep(1500);

            servosService.aspirationCratere();

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            mv.avanceMM(350);
            mv.reculeMM(400);

            servosService.aspirationFerme();
            servosService.waitAspiration();

            servosService.aspirationStop();*/

            // version rotation
            mv.pathTo(940, 720);

            servosService.aspirationMax();

            mv.gotoOrientationDeg(70);

            Thread.sleep(1500);

            servosService.aspirationCratere();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientationBasse);

            mv.tourneDeg(40);
            mv.tourneDeg(-40);

            servosService.aspirationFerme();
            servosService.waitAspiration();

            servosService.aspirationStop();

            completed = true;
            rs.setCratereZoneDepartJauneRecupere(true);
            rs.setHasPetitesBalles(false);

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }
}