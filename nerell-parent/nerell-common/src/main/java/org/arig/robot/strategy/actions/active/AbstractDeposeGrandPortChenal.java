package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDeposeGrandPortChenal extends AbstractNerellAction {

    protected enum EPosition {
        NORD,
        SUD
    }

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    protected NerellRobotStatus rs;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    private GotoOption sensEntry = GotoOption.AUTO;

    protected abstract ECouleurBouee getCouleurChenal();

    protected abstract EPosition getPositionChenal();

    protected abstract Bouee getBoueeAlternateEntry();

    protected abstract Point getPointAlternateEntry();

    protected abstract Chenaux getChenauxFuture();

    @Override
    protected Point entryPoint() {
        double x = 460;
        double y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }
        final Point central = new Point(x, y);

        if (getBoueeAlternateEntry().prise()) {
            final Point alternateEntry = getPointAlternateEntry();

            if (tableUtils.distance(alternateEntry) < tableUtils.distance(central)) {
                sensEntry = GotoOption.AVANT;
                return alternateEntry;
            }
        }

        sensEntry = GotoOption.ARRIERE;
        return central;
    }

    @Override
    public int order() {
        int order = getChenauxFuture().score() - rs.grandChenaux().score();
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && (!rs.pincesArriereEmpty() || !rs.pincesAvantEmpty());
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            if (tableUtils.distance(entry) > 100) {
                mv.pathTo(entry);
            }
            rs.disableAvoidance();

            double xRef = 225;
            double yRef = 1200;
            if (rs.getTeam() == ETeam.JAUNE) {
                xRef = 3000 - xRef;
            }

            boolean deposeArriere = false;
            if (!rs.pincesArriereEmpty()) {
                deposeArriere = true;
                mv.gotoPoint(xRef, getYDepose(yRef, false), sensEntry, GotoOption.SANS_ORIENTATION);
                mv.gotoOrientationDeg(getPositionChenal() == EPosition.NORD ? -90 : 90);
                pincesArriereService.deposeGrandChenal(getCouleurChenal()); // TODO Dépose partiel
            }

            if (!rs.pincesAvantEmpty()) {
                if (deposeArriere) {
                    mv.avanceMM(35);
                }
                mv.gotoPoint(xRef, getYDepose(yRef, true), GotoOption.AVANT);
                pincesAvantService.deposeGrandChenal(getCouleurChenal()); // TODO Dépose partiel
            }

            mv.gotoPoint(xRef, yRef, GotoOption.SANS_ORIENTATION);
            pincesAvantService.finaliseDepose();
            completed = true;

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }

    private double getYDepose(double yRef, boolean avant) {
        int coef = 61 + 32; // Offset pour Y en fonction du type de dépose
        if (avant) {
            coef += 30;
        }

        if (getPositionChenal() == EPosition.NORD) {
            return yRef + coef;
        } else {
            return yRef - coef;
        }
    }
}
