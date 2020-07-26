package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReglageCentrifuge extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private NerellRobotStatus rs;

    @Getter
    private boolean completed = false;

    @Getter
    private final boolean isValid = true;


    @Override
    public String name() {
        return "Réglage centrifuge";
    }

    @Override
    public Point entryPoint() {
        return null;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            rs.disableAvoidance();

            for (int i = 0 ; i < 2 ; i++) {
                mv.gotoPoint(rs.bouee(11).pt(), GotoOption.SANS_ARRET);
                mv.gotoPoint(rs.bouee(8).pt(), GotoOption.SANS_ARRET);
                mv.gotoPoint(rs.bouee(9).pt(), GotoOption.SANS_ARRET);
                mv.gotoPoint(rs.bouee(6).pt());
            }
            mv.gotoPoint(200, 1200);
            mv.gotoOrientationDeg(0);

            completed = true;
        } catch (AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
