package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Test extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Getter
    private boolean completed = false;


    @Override
    public String name() {
        return "Test";
    }

    @Override
    protected Point entryPoint() {
        return null;
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean isValid() {
        return isTimeValid();
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();
            mv.setVitesse(300, 300);

            rs.enablePinces();
            mv.avanceMM(1000);
            rs.disablePinces();
            mv.gotoOrientationDeg(180);
            mv.avanceMM(800);
            mv.gotoOrientationDeg(0);
            pincesArriereService.preparePriseEcueil();
            mv.reculeMM(150);
            rs.enableCalageBordure();
            mv.reculeMM(60);
            pincesArriereService.finalisePriseEcueil(ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU);
            mv.avanceMM(1200);
            mv.gotoOrientationDeg(180);
            pincesArriereService.deposeArrierePetitPort();
            mv.avanceMM(60);
            mv.gotoOrientationDeg(0);
            servos.ascenseurAvantBas(true);
            servos.pincesAvantOuvert(true);
            mv.reculeMM(120);
            servos.pincesAvantFerme(false);

            completed = true;
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
