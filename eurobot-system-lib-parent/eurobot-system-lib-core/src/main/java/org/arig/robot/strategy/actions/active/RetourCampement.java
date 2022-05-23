package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Campement;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeRetour;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
public class RetourCampement extends AbstractCampement {

    protected final int X = 425;
    protected final int Y = 1555;

    @Override
    public Point entryPoint() {
        if (rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN) {
            position = Campement.Position.SUD;
            return new Point(getX(X), 1300 - (Y - 1300));
        } else {
            position = Campement.Position.NORD;
            return new Point(getX(X), Y);
        }
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_RETOUR_CAMPEMENT_PREFIX + robotName.id();
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public int order() {
        return 20;
    }

    @Override
    public boolean isValid() {
        boolean siteValid = rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN || !rs.siteDeRetourAutreRobot().isFouille();
        return siteValid && !timeBeforeRetourValid();
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();

            group.siteDeRetour(SiteDeRetour.WIP_CAMPEMENT);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            group.siteDeRetour(SiteDeRetour.CAMPEMENT);

            if (rs.stockTaille() > 0) {
                Supplier<Integer> taillePile = () -> {
                    return position == Campement.Position.NORD ? rs.tailleCampementBleuVertNord() : rs.tailleCampementBleuVertSud();
                };

                Supplier<Boolean> isValid = () -> {
                    return rs.getRemainingTime() > 2000;
                };

                Consumer<CouleurEchantillon> onDepose = (c) -> {
                    if (position == Campement.Position.NORD) {
                        group.deposeCampementBleuVertNord(c);
                    } else {
                        group.deposeCampementBleuVertSud(c);
                    }
                };

                deposePile(isValid, onDepose, taillePile);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            bras.safeHoming();

            if (!rs.siteDeRetour().isInSite() && !(e instanceof MovementCancelledException)) {
                group.siteDeRetour(SiteDeRetour.AUCUN);
            }
        }
    }

}
