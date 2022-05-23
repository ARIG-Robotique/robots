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

    protected final int X = 290;
    protected final int Y = 1477;

    @Autowired
    private BrasService bras;

    @Autowired
    private DeposeGalerie actionDeposeGalerie;

    @Override
    public Point entryPoint() {
        Point pointNord = new Point(getX(X), Y);
        Point pointSud = new Point(getX(X), 1300 - (Y - 1300));

        if (rs.otherCampement() == null) {
            if (rs.tailleCampementRougeVertSud() == 0) {
                position = Campement.Position.SUD;
                return pointNord;
            } else {
                position = Campement.Position.NORD;
                return pointSud;
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
        return 3000 * rs.stockTaille();
    }

    @Override
    public int order() {
        final int nbEchantillons = Math.min(rs.stockTaille(), Campement.MAX_DEPOSE);

        return (int) Math.round(nbEchantillons * 1.5) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        final boolean campementValid;
        if (rs.otherCampement() == null) {
            campementValid = (rs.tailleCampementRougeVertNord() == 0 || rs.tailleCampementRougeVertSud() == 0);
        } else if (rs.otherCampement() == Campement.Position.NORD) {
            return rs.tailleCampementRougeVertSud() == 0;
        } else {
            return rs.tailleCampementRougeVertNord() == 0;
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

        // pos premier nord rouge vert x=302 y=1477
        // pos second nord rouge vert x=436 y=1555

        try {
            group.positionCampement(position);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            Supplier<Integer> taillePile = () -> {
                return position == Campement.Position.NORD ? rs.tailleCampementRougeVertNord() : rs.tailleCampementRougeVertSud();
            };

            Supplier<Boolean> isValid = () -> {
                if (actionDeposeGalerie.isValid() && !EurobotConfig.ACTION_DEPOSE_GALERIE.equals(rs.otherCurrentAction())) {
                    log.info("Annulation de la dépose campement car la galerie est dispo");
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
