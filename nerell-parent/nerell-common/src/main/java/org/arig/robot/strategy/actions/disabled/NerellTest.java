package org.arig.robot.strategy.actions.disabled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellTest extends AbstractNerellAction {

    @Autowired
    private INerellPincesArriereService pincesArriereService;

    @Autowired
    private INerellPincesAvantService pincesAvantService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Nerell Test";
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
            rsNerell.disableAvoidance();

            // Récupération des élements dans la pince avant
            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(1200,1200, GotoOption.AVANT);

            // Récupération de l'ecueil
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(400,1200, GotoOption.AVANT);
            pincesArriereService.preparePriseEcueil();
            mv.gotoPoint(250,1200, GotoOption.ARRIERE);
            rsNerell.enableCalageBordure();
            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation(30));
            mv.reculeMMSansAngle(60);
            pincesArriereService.finalisePriseEcueil(INerellPincesArriereService.EEcueil.EQUIPE,
                    ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU);

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(1200,1200, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
            mv.gotoOrientationDeg(180);
            pincesArriereService.deposePetitPort();
            mv.avanceMM(35);
            if (!rsNerell.pincesAvantEmpty()) {
                pincesAvantService.deposePetitPort();
            }
            mv.gotoPoint(800, 1200);

            completed = true;
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
