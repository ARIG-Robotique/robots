package org.arig.robot.strategy.actions.active.echappement;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AntiblocagePort extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    protected NerellRobotStatus rs;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Getter
    private final boolean completed = false;

    @Override
    protected Point entryPoint() {
        double x = 460;
        double y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }
        return new Point(x, y);
    }

    @Override
    public String name() {
        return "Antiblocage port";
    }

    @Override
    public int order() {
        return -100;
    }

    @Override
    public boolean isValid() {
        double x = conv.pulseToMm(currentPosition.getPt().getX());
        double y = conv.pulseToMm(currentPosition.getPt().getY());

        return isTimeValid() && y >= 900 && y <= 1500 && (x <= 400 || x >= 2600);
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.gotoPoint(entryPoint());
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
