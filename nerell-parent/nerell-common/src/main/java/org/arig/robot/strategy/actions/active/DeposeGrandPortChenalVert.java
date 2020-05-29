package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeGrandPortChenalVert extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private NerellStatus rs;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    private SensDeplacement sensEntry = SensDeplacement.AUTO;

    @Override
    public String name() {
        return "Dépose grand port chenal vert";
    }

    @Override
    protected Point entryPoint() {
        double x = 460;
        double y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }
        final Point central = new Point(x, y);

        if (rs.getTeam() == ETeam.BLEU && rs.bouee(1).prise()) {
            // Point entrée ISO phare
            final Point phare = new Point(Phare.ENTRY_X, Phare.ENTRY_Y);

            double distanceCentral = tableUtils.distance(central);
            double distancePhare = tableUtils.distance(phare);
            sensEntry = distanceCentral < distancePhare ? SensDeplacement.ARRIERE : SensDeplacement.AVANT;
            return distanceCentral < distancePhare ? central : phare;
        }

        sensEntry = SensDeplacement.ARRIERE;
        return central;
    }

    @Override
    public int order() {
        Chenaux chenauxFuture = rs.grandChenaux().with(null, rs.pincesArriere());
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
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            if (tableUtils.distance(entry) > 100) {
                rs.enableAvoidance();
                mv.pathTo(entry, false);
            }
            rs.disableAvoidance();

            double xRef = 225;
            double yRef = 1200;
            if (rs.getTeam() == ETeam.BLEU) {
                if (!rs.pincesArriereEmpty()) {
                    mv.gotoPointMM(xRef, getYDepose(yRef,false), false, sensEntry);
                    mv.gotoOrientationDeg(-90);
                    pincesArriereService.deposeGrandChenal(ECouleurBouee.VERT); // TODO Dépose partiel
                    mv.gotoPointMM(xRef, yRef, false);
                }

            } else {
                xRef = 3000 - xRef;
                if (!rs.pincesArriereEmpty()) {
                    mv.gotoPointMM(xRef, getYDepose(yRef,false), false, sensEntry);
                    mv.gotoOrientationDeg(90);
                    pincesArriereService.deposeGrandChenal(ECouleurBouee.VERT);  // TODO Dépose partiel
                    mv.gotoPointMM(xRef, yRef, false);
                }
            }
            completed = true;

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }

    private double getYDepose(double yRef, boolean avant) {
        int coef = avant ? 160 : 61; // Offset pour Y en fonction du type de dépose

        if (rs.getTeam() == ETeam.BLEU) {
            return yRef + coef;
        } else {
            return yRef - coef;
        }
    }
}
