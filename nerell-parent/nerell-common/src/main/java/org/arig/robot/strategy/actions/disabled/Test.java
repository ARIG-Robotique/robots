package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Test extends AbstractNerellAction {

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Test";
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
    public boolean isValid() {
        return isTimeValid();
    }

    @Override
    public void execute() {
        try {
            rs.disableAvoidance();

            // Récupération des élements dans la pince avant
            rs.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(3), robotConfig.vitesseOrientation(10));
            mv.gotoPoint(1200,1200, GotoOption.AVANT);
            rs.disablePincesAvant();

            // Récupération de l'ecueil
            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation(10));
            mv.gotoPoint(400,1200, GotoOption.AVANT);
            pincesArriereService.preparePriseEcueil();
            mv.gotoPoint(250,1200, GotoOption.ARRIERE);
            rs.enableCalageBordure();
            mv.setVitesse(robotConfig.vitesse(3), robotConfig.vitesseOrientation(3));
            mv.reculeMMSansAngle(60);
            pincesArriereService.finalisePriseEcueil(ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU);

            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation(10));
            mv.gotoPoint(1200,1200, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
            mv.gotoOrientationDeg(180);
            pincesArriereService.deposePetitPort();
            mv.avanceMM(35);
            if (!rs.pincesAvantEmpty()) {
                // TODO
            }
            mv.gotoPoint(800, 1200);

            completed = true;
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
