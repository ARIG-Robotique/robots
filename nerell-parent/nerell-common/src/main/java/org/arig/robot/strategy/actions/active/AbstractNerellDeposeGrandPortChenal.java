package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
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
public abstract class AbstractNerellDeposeGrandPortChenal extends AbstractNerellAction {

    protected enum EPosition {
        NORD,
        SUD
    }

    @Autowired
    private AbstractNerellPincesArriereService pincesArriereService;

    @Autowired
    private AbstractNerellPincesAvantService pincesAvantService;

    protected abstract int getBoueeBloquante();

    protected abstract ECouleurBouee getCouleurChenal();

    protected abstract EPosition getPositionChenal();

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }
        return new Point(x, y);
    }

    @Override
    public int order() {
        int order;

        if (rsNerell.deposePartielleDone()) {
            order = 1000;

        } else if (rsNerell.deposePartielle()) {
            Chenaux chenauxFuture = rsNerell.cloneGrandChenaux();
            int currentScoreChenaux = chenauxFuture.score();

            for (ECouleurBouee couleur : rsNerell.pincesAvant()) {
                if (couleur == ECouleurBouee.ROUGE) {
                    chenauxFuture.addRouge(couleur);
                } else if (couleur == ECouleurBouee.VERT) {
                    chenauxFuture.addVert(couleur);
                }
            }
            for (ECouleurBouee couleur : rsNerell.pincesArriere()) {
                if (couleur == ECouleurBouee.ROUGE) {
                    chenauxFuture.addRouge(couleur);
                } else if (couleur == ECouleurBouee.VERT) {
                    chenauxFuture.addVert(couleur);
                }
            }
            order = chenauxFuture.score() - currentScoreChenaux;

        } else {
            Chenaux chenauxFuture = rsNerell.cloneGrandChenaux();
            int currentScoreChenaux = chenauxFuture.score();

            if (getCouleurChenal() == ECouleurBouee.ROUGE) {
                if (!rsNerell.pincesArriereEmpty()) {
                    chenauxFuture.addRouge(rsNerell.pincesArriere());
                }
                if (!rsNerell.pincesAvantEmpty() && (rsNerell.pincesArriereEmpty() || rsNerell.doubleDepose())) {
                    chenauxFuture.addRouge(rsNerell.pincesAvant());
                }
            } else {
                if (!rsNerell.pincesArriereEmpty()) {
                    chenauxFuture.addVert(rsNerell.pincesArriere());
                }
                if (!rsNerell.pincesAvantEmpty() && (rsNerell.pincesArriereEmpty() || rsNerell.doubleDepose())) {
                    chenauxFuture.addVert(rsNerell.pincesAvant());
                }
            }

            order = chenauxFuture.score() - currentScoreChenaux;
        }

        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid() || rsNerell.inPort()) {
            return false;

        } else if (rsNerell.deposePartielle()) {
            return !rsNerell.pincesArriereEmpty() && !rsNerell.pincesAvantEmpty();

        } else {
            return (!rsNerell.boueePresente(getBoueeBloquante()) || rsNerell.getRemainingTime() < IEurobotConfig.invalidPriseRemainingTime) &&
                    (!rsNerell.pincesArriereEmpty() || !rsNerell.pincesAvantEmpty());
        }
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            final Point entry = entryPoint();
            boolean onlyOne = !rsNerell.doubleDepose() || (rsNerell.pincesArriereEmpty() ^ rsNerell.pincesAvantEmpty());
            final Point entry2 = new Point(entry.getX(), getYDepose(entry.getY(), rsNerell.pincesArriereEmpty(), onlyOne));

            if (tableUtils.distance(entry2) > 100) {
                mv.pathTo(entry2);
                rsNerell.disableAvoidance();
            } else {
                rsNerell.disableAvoidance();
                mv.gotoPoint(entry2, GotoOption.SANS_ORIENTATION);
            }

            boolean deposeArriere = false;
            if (!rsNerell.pincesArriereEmpty()) {
                deposeArriere = true;
                mv.gotoOrientationDeg(getPositionChenal() == EPosition.NORD ? -90 : 90);
                pincesArriereService.deposeGrandChenal(getCouleurChenal(), rsNerell.deposePartielle());
            }

            if (!rsNerell.pincesAvantEmpty() && (!deposeArriere || rsNerell.doubleDepose())) {
                if (deposeArriere) {
                    mv.avanceMM(35); // FIXME nouvelle face avant
                    mv.gotoPoint(entry.getX(), getYDepose(entry.getY(), true, false), GotoOption.AVANT);
                } else {
                    mv.gotoOrientationDeg(getPositionChenal() == EPosition.NORD ? 90 : -90);
                }
                pincesAvantService.deposeGrandChenal(getCouleurChenal(), rsNerell.deposePartielle());
            }

            if (rsNerell.deposePartielle()) {
                rsNerell.deposePartielleDone(true);
            }

            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);
            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
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
