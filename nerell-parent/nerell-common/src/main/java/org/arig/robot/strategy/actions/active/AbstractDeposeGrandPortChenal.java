package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractNerellPincesArriereService;
import org.arig.robot.services.AbstractNerellPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDeposeGrandPortChenal extends AbstractNerellAction {

    protected enum EPosition {
        NORD,
        SUD
    }

    @Autowired
    private AbstractNerellPincesArriereService pincesArriereService;

    @Autowired
    private AbstractNerellPincesAvantService pincesAvantService;

    protected abstract Bouee getBoueeBloquante();

    protected abstract ECouleurBouee getCouleurChenal();

    protected abstract EPosition getPositionChenal();

    protected abstract Chenaux getChenauxFuture();

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rs.team()) {
            x = 3000 - x;
        }
        return new Point(x, y);
    }

    @Override
    public int order() {
        int order;

        if (rs.deposePartielleDone()) {
            order = 1000;

        } else if (rs.deposePartielle()) {
            Chenaux chenaux = rs.grandChenaux().with(null, null);
            for (ECouleurBouee couleur : rs.pincesAvant()) {
                if (couleur == ECouleurBouee.ROUGE) {
                    chenaux.addRouge(couleur);
                } else if (couleur == ECouleurBouee.VERT) {
                    chenaux.addVert(couleur);
                }
            }
            for (ECouleurBouee couleur : rs.pincesArriere()) {
                if (couleur == ECouleurBouee.ROUGE) {
                    chenaux.addRouge(couleur);
                } else if (couleur == ECouleurBouee.VERT) {
                    chenaux.addVert(couleur);
                }
            }
            order = chenaux.score() - rs.grandChenaux().score();

        } else {
            order = getChenauxFuture().score() - rs.grandChenaux().score();
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid() || rs.inPort()) {
            return false;

        } else if (rs.deposePartielle()) {
            return !rs.pincesArriereEmpty() && !rs.pincesAvantEmpty();

        } else {
            return (!getBoueeBloquante().presente() || rs.getRemainingTime() < IEurobotConfig.invalidPriseRemainingTime) &&
                    (!rs.pincesArriereEmpty() || !rs.pincesAvantEmpty());
        }
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            final Point entry = entryPoint();
            boolean onlyOne = !rs.doubleDepose() || (rs.pincesArriereEmpty() ^ rs.pincesAvantEmpty());
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
                pincesArriereService.deposeGrandChenal(getCouleurChenal(), rs.deposePartielle());
            }

            if (!rs.pincesAvantEmpty() && (!deposeArriere || rs.doubleDepose())) {
                if (deposeArriere) {
                    mv.avanceMM(35); // FIXME nouvelle face avant
                    mv.gotoPoint(entry.getX(), getYDepose(entry.getY(), true, false), GotoOption.AVANT);
                } else {
                    mv.gotoOrientationDeg(getPositionChenal() == EPosition.NORD ? 90 : -90);
                }
                pincesAvantService.deposeGrandChenal(getCouleurChenal(), rs.deposePartielle());
            }

            if (rs.deposePartielle()) {
                rs.deposePartielleDone(true);
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
        } else {
            coef -= 5;
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
