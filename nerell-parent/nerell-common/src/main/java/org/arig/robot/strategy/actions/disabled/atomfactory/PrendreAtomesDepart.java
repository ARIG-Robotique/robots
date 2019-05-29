package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PrendreAtomesDepart extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise des palets de départ";
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

            mv.setVitesse(IConstantesNerellConfig.vitesseMouvement, IConstantesNerellConfig.vitesseOrientation);

            rs.disableAvoidance();

            List<Pair<Point, Boolean>> configs = new ArrayList<>();

            if (rs.getTeam().equals(Team.VIOLET)) {
                configs.add(Pair.of(new Point(2500, 1550 - IConstantesNerellConfig.dstAtomeCentre), true));
                configs.add(Pair.of(new Point(2500 - IConstantesNerellConfig.dstAtomeCentre, 1250), false));
                configs.add(Pair.of(new Point(2500 + IConstantesNerellConfig.dstAtomeCentre, 950), true));
            } else {
                configs.add(Pair.of(new Point(500, 1550 - IConstantesNerellConfig.dstAtomeCentre), true));
                configs.add(Pair.of(new Point(500 + IConstantesNerellConfig.dstAtomeCentre, 1250), false));
                configs.add(Pair.of(new Point(500 - IConstantesNerellConfig.dstAtomeCentre, 950), true));
            }

            for (Pair<Point, Boolean> config : configs) {
                mv.gotoPointMM(config.getLeft().getX(), config.getLeft().getY(), config.getRight());
            }


        } catch (RefreshPathFindingException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());

        } finally {
            // si ça a échoué on a surement shooté dans les palets...
            completed = true;
        }
    }

}
