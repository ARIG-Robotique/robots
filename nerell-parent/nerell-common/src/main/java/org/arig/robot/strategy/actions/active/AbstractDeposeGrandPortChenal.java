package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDeposeGrandPortChenal extends AbstractNerellAction {

    protected enum EPosition {
        NORD,
        SUD
    }

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private IPincesAvantService pincesAvantService;

    protected abstract Bouee getBoueeBloquante();

    protected abstract ECouleurBouee getCouleurChenal();

    protected abstract EPosition getPositionChenal();

    protected abstract Chenaux getChenauxFuture();

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }
        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = getChenauxFuture().score() - rs.grandChenaux().score();
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && !getBoueeBloquante().presente() &&
                (!rs.pincesArriereEmpty() || !rs.pincesAvantEmpty() && rs.isDoubleDepose());
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            boolean onlyOne = !rs.isDoubleDepose() || (rs.pincesArriereEmpty() ^ rs.pincesAvantEmpty());
            final Point entry2 = new Point(entry.getX(), getYDepose(entry.getY(), rs.pincesArriereEmpty(), onlyOne));

            if (tableUtils.distance(entry2) > 100) {
                mv.pathTo(entry2);
                rs.disableAvoidance();
            } else {
                rs.disableAvoidance();
                mv.gotoPoint(entry2, GotoOption.SANS_ORIENTATION);
            }

            boolean deposeArriere = false;
            if (!rs.pincesArriereEmpty()) {
                deposeArriere = true;
                mv.gotoOrientationDeg(getPositionChenal() == EPosition.NORD ? -90 : 90);
                pincesArriereService.deposeGrandChenal(getCouleurChenal()); // TODO Dépose partiel
            }

            if (!rs.pincesAvantEmpty() && rs.isDoubleDepose()) {
                if (deposeArriere) {
                    mv.avanceMM(35);
                    mv.gotoPoint(entry.getX(), getYDepose(entry.getY(), true, false), GotoOption.AVANT);
                } else {
                    mv.gotoOrientationDeg(getPositionChenal() == EPosition.NORD ? 90 : -90);
                }
                pincesAvantService.deposeGrandChenal(getCouleurChenal()); // TODO Dépose partiel
            }

            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);
            pincesAvantService.finaliseDepose();
            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }

    private double getYDepose(double yRef, boolean avant, boolean onlyOne) {
        // offset de base par rapport au milieu du port
        int coef = 93;
        // offset pour dépose avant
        if (avant) {
            coef += 30;
        }
        // offset si c'est la seule dépose
        if (onlyOne) {
            coef += avant ? 30 : -30;
        }

        if (getPositionChenal() == EPosition.NORD) {
            return yRef + coef;
        } else {
            return yRef - coef;
        }
    }
}
