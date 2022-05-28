package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Echantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PriseSiteDeFouilleAdverse extends AbstractPriseSiteDeFouille {

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_SITE_FOUILLE_ADVERSE;
    }

    @Override
    public int executionTimeMs() {
        return 4000 * 3; // TODO: A quantifier
    }

    @Override
    public boolean isValid() {
        if (!rs.siteDeFouille() || rs.strategy() == Strategy.BASIC) {
            return false;
        }

        return isTimeValid() && timeBeforeRetourValid()
                && !rs.siteDeFouilleAdversePris() && rs.stockDisponible() > 0;
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteDeFouilleAdversePris() || !rs.siteDeFouille()) {
            complete();
        }
    }

    @Override
    public int order() {
        int stock = rs.stockDisponible();
        return Math.min(stock, 3) * EurobotConfig.PTS_DEPOSE_PRISE + tableUtils.alterOrder(entryPoint());
    }

    @Override
    protected Echantillon.ID siteDeFouille() {
        return rs.team() == Team.JAUNE ? Echantillon.ID.SITE_FOUILLE_VIOLET : Echantillon.ID.SITE_FOUILLE_JAUNE;
    }

    @Override
    protected void notifySitePris() {
        group.siteDeFouilleAdversePris();
    }

    @Override
    public Point entryPoint() {
        final List<Echantillon> currentEchantillons = echantillonsSite(siteDeFouille());

        // Calcul point d'approche du site de fouille
        if (!currentEchantillons.isEmpty()) {
            final Echantillon echantillonPlusProche = currentEchantillons.get(0);
            final Point centreSiteDeFouille = new Point(rs.team() == Team.JAUNE ? 3000 - CENTRE_FOUILLE_X_JAUNE : CENTRE_FOUILLE_X_JAUNE, CENTRE_FOUILLE_Y);
            return new Point(
                    centreSiteDeFouille.getX() -
                            Math.signum(centreSiteDeFouille.getX() - echantillonPlusProche.getX()) * EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE / 2,
                    centreSiteDeFouille.getY() -
                            Math.signum(centreSiteDeFouille.getY() - echantillonPlusProche.getY()) * EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE / 2);
        }

        // Pas d'entry point
        return new Point(0,0);
    }

}
