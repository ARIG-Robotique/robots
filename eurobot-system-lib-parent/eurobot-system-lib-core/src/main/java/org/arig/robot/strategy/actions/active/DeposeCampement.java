package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Campement;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.services.BrasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
public class DeposeCampement extends AbstractCampement {

    protected final int ENTRY_X = 425;
    protected final int ENTRY_Y = 1555;

    protected final int X = 290;
    protected final int Y = 1490;

    protected final int CENTER_X = 1300;

    @Autowired
    private BrasService bras;

    @Autowired
    private DeposeGalerie actionDeposeGalerie;

    @Override
    public Point entryPoint() {
        Point pointNord = new Point(getX(ENTRY_X), ENTRY_Y);
        Point pointSud = new Point(getX(ENTRY_X), CENTER_X - (ENTRY_Y - CENTER_X));

        if (rs.otherCampement() == null) {
            if (rs.galerieComplete() && rs.tailleCampementRougeVertNord() == 0) {
                position = Campement.Position.NORD;
                return pointNord;
            } else if (rs.tailleCampementRougeVertSud() == 0) {
                position = Campement.Position.SUD;
                return pointSud;
            } else {
                position = Campement.Position.NORD;
                return pointNord;
            }
        } else if (rs.otherCampement() == Campement.Position.NORD) {
            position = Campement.Position.SUD;
            return pointSud;
        } else {
            position = Campement.Position.NORD;
            return pointNord;
        }
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_DEPOSE_CAMPEMENT_PREFIX + robotName.id();
    }

    @Override
    public int executionTimeMs() {
        return 1500 * Math.min(rs.stockTaille(), Campement.MAX_DEPOSE);
    }

    @Override
    public int order() {
        final int nbEchantillons = Math.min(rs.stockTaille(), Campement.MAX_DEPOSE);

        return (int) Math.round(nbEchantillons * 1.5) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        // si la galerie est dispo on interdit la dépose au campement
        if (actionDeposeGalerie.isValid() && !EurobotConfig.ACTION_DEPOSE_GALERIE.equals(rs.otherCurrentAction())) {
            return false;
        }

        final boolean campementValid;
        // on ne peut pas poser au nord si l'autre fait la galerie
        if (rs.otherCampement() == null) {
            if (EurobotConfig.ACTION_DEPOSE_GALERIE.equals(rs.otherCurrentAction())) {
                campementValid = rs.tailleCampementRougeVertSud() == 0;
            } else {
                campementValid = (rs.tailleCampementRougeVertNord() == 0 || rs.tailleCampementRougeVertSud() == 0);
            }
        } else if (rs.otherCampement() == Campement.Position.NORD) {
            campementValid = rs.tailleCampementRougeVertSud() == 0;
        } else if (!EurobotConfig.ACTION_DEPOSE_GALERIE.equals(rs.otherCurrentAction())) {
            campementValid = rs.tailleCampementRougeVertNord() == 0;
        } else {
            campementValid = false;
        }

        return rs.stockTaille() > 0 && campementValid && isTimeValid() && timeBeforeRetourValid();
    }

    @Override
    public void refreshCompleted() {
        if (rs.tailleCampementRougeVertNord() > 0 && rs.tailleCampementRougeVertSud() > 0) {
            complete();
        }
    }

    @Override
    public void execute() {
        Point entry = entryPoint();

        try {
            group.positionCampement(position);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            rs.disableAvoidance();
            if (position == Campement.Position.NORD) {
                mv.gotoPoint(getX(X), Y);
            } else {
                mv.gotoPoint(getX(X), CENTER_X - (Y - CENTER_X));
            }

            Supplier<Integer> taillePile = () -> {
                return position == Campement.Position.NORD ? rs.tailleCampementRougeVertNord() : rs.tailleCampementRougeVertSud();
            };

            Supplier<Boolean> isValid = () -> {
                if (actionDeposeGalerie.isValid()
                        && !EurobotConfig.ACTION_DEPOSE_GALERIE.equals(rs.otherCurrentAction())
                        && rs.getRemainingTime() > 15000) {
                    log.info("Annulation de la dépose campement car la galerie est dispo");
                    updateValidTime();
                    return false;
                } else {
                    return true;
                }
            };

            Consumer<CouleurEchantillon> onDepose = (c) -> {
                if (position == Campement.Position.NORD) {
                    group.deposeCampementRougeVertNord(c);
                } else {
                    group.deposeCampementRougeVertSud(c);
                }
            };

            deposePile(isValid, onDepose, taillePile);

            mv.reculeMM(100);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();

        } finally {
            refreshCompleted();
            group.positionCampement(null);
        }
    }
}
