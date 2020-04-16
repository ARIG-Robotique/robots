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
public class EcueilEquipe extends AbstractAction {

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
        return "Ecueil equipe";
    }

    @Override
    protected Point entryPoint() {
        double x = 230;
        double y = 400;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 14; // Sur chenal, bien trié (5 bouées, 2 paires)
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.pincesArriereEmpty();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            mv.pathTo(entry);

            mv.gotoOrientationDeg(rs.getTeam() == ETeam.BLEU ? 0 : 180);

            pincesArriereService.preparePriseEcueil();
            mv.reculeMM(60);

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMMSansAngle(60);

            if (rs.getTeam() == ETeam.BLEU) {
                pincesArriereService.finalisePriseEcueil(ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE);
            } else {
                pincesArriereService.finalisePriseEcueil(ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT, ECouleurBouee.ROUGE, ECouleurBouee.VERT);
            }

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
