package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinNettoyageGrandPort extends AbstractOdinAction {

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_NETTOYAGE_GRAND_PORT;
    }

    @Override
    public Point entryPoint() {
        double X = 255;
        double Y = 1000;
        if (ETeam.JAUNE == rsOdin.team()) {
            X = 3000 - X;
        }
        return new Point(X, Y);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public void execute() {
        try {
            // TODO Optim d'entry point avec distance comme pour le phare et les dépose chenaux de Nerell
            final Point entry = entryPoint();
            rsOdin.disableAvoidance();

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            // FIXME Gestion entry point à définir

            mv.gotoPoint(entry.getX(), entry.getY() - 200, GotoOption.AVANT);
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
            group.boueePrise(4);

            mv.gotoPoint(408, 927, GotoOption.ARRIERE);
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
            group.boueePrise(3);

            if (rs.boueePresente(2) || rsOdin.boueePresente(1)) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.gotoPoint(412, 1390, GotoOption.AVANT);

                mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
                mv.gotoPoint(412, 1430, GotoOption.AVANT);
                ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
                group.boueePrise(2);

                mv.gotoPoint(337, 1593, GotoOption.ARRIERE);
                ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
                group.boueePrise(1);
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            // Dépose verte
            mv.gotoPoint(180, 1490, GotoOption.AVANT);
            mv.gotoPoint(150, 1490, GotoOption.AVANT);
            pincesAvantService.deposeGrandPort(); // FIXME a changer en chenal
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
            mv.gotoPoint(250,1490, GotoOption.ARRIERE);

            // Dépose rouge
            mv.gotoPoint(180, 920, GotoOption.ARRIERE);
            mv.gotoPoint(150, 920, GotoOption.ARRIERE);
            pincesArriereService.deposeGrandPort(); // FIXME a changer en chenal
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
            mv.gotoPoint(250,920, GotoOption.AVANT);

            complete();
        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
