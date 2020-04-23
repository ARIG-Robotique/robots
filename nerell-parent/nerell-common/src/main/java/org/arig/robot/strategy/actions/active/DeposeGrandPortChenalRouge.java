package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPortChenalRouge extends AbstractAction {

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
        return "Dépose grand port chenal rouge";
    }

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
    public int order() {
        Chenaux chenauxFuture = rs.grandChenaux().with(rs.getPincesArriere(), null);
        int order = chenauxFuture.score() - rs.grandChenaux().score();
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && !rs.pincesArriereEmpty();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            mv.pathTo(entry);

            double xRef = 225;
            double yRef = entry.getY();
            if (rs.getTeam() == ETeam.BLEU) {
                if (!rs.pincesArriereEmpty()) {
                    mv.gotoPointMM(xRef, getYDepose(yRef,false), true, SensDeplacement.ARRIERE);
                    mv.gotoOrientationDeg(90);
                    pincesArriereService.deposeArriereChenal(ECouleurBouee.ROUGE); // TODO Dépose partiel
                    mv.gotoPointMM(xRef, yRef, false);
                }

            } else {
                xRef = 3000 - xRef;
                if (!rs.pincesArriereEmpty()) {
                    mv.gotoPointMM(xRef, getYDepose(yRef,false), true, SensDeplacement.ARRIERE);
                    mv.gotoOrientationDeg(-90);
                    pincesArriereService.deposeArriereChenal(ECouleurBouee.ROUGE);  // TODO Dépose partiel
                    mv.gotoPointMM(entry, false);
                }
            }
            completed = true;

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }

    private double getYDepose(double yRef, boolean avant) {
        int coef = avant ? 160 : 61; // de combien il faut reculer

        if (rs.getTeam() == ETeam.BLEU) {
            return yRef - coef;
        } else {
            return yRef + coef;
        }
    }
}
