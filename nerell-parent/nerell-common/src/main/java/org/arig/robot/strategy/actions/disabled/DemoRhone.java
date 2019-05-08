package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IRobotSide;
import org.arig.robot.services.SerrageService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DemoRhone extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    @Qualifier("sideServices")
    private Map<ESide, IRobotSide> sideServices;

    @Autowired
    private SerrageService serrageService;

    @Getter
    boolean completed = false;

    @Override
    public String name() {
        return "Action de démo coupe du rhone";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {

            rs.enableAvoidance();

            mv.avanceMM(500);

            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(300, 1300);

                serrageService.disable();

                sideServices.get(ESide.GAUCHE).pinceSerrageRepos();

                mv.reculeMM(100);

                sideServices.get(ESide.GAUCHE).pinceSerrageRepos();

            } else {
                mv.pathTo(3000 - 300, 1300);

                serrageService.disable();

                sideServices.get(ESide.DROITE).pinceSerrageRepos();

                mv.reculeMM(100);

                sideServices.get(ESide.DROITE).pinceSerrageRepos();
            }

            serrageService.enable();

            rs.getPaletsInTableauVert().add(CouleurPalet.INCONNU);

            completed = true;

        } catch (RefreshPathFindingException | NoPathFoundException | AvoidingException e) {

        }
    }
}
