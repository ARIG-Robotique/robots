package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EccueilAdverse extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Eccueil adverse";
    }

    @Override
    protected Point entryPoint() {
        double x = 2150;
        double y = 2000 - 230;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = rs.getEccueilAdverseDispo() * 2 + (int) Math.ceil(rs.getEccueilAdverseDispo() / 2.0) * 2; // Sur chenal, bien trié (X bouées, X/2 paires)
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.pincesArriereEmpty() && rs.getEccueilAdverseDispo() > 0;
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            mv.pathTo(entry);

            mv.gotoOrientationDeg(-90);

            pincesArriereService.preparePriseEcueil();
            mv.reculeMM(60);

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMMSansAngle(60);

            ECouleurBouee[] couleursEccueil = rs.getCouleursEccueil();
            ECouleurBouee[] couleursFinales = new ECouleurBouee[5];
            for (int i = 0; i < 5; i++) {
                // on symétrise et on inverse (les connues seulement)
                if (couleursEccueil[5 - i] == ECouleurBouee.ROUGE) {
                    couleursFinales[i] = ECouleurBouee.VERT;
                } else if (couleursEccueil[5 - i] == ECouleurBouee.VERT) {
                    couleursFinales[i] = ECouleurBouee.ROUGE;
                } else {
                    couleursFinales[i] = ECouleurBouee.INCONNU;
                }
            }

            pincesArriereService.finalisePriseEcueil(couleursFinales);

            // STOCK

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.gotoPointMM(entry, false);

            completed = true;

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
