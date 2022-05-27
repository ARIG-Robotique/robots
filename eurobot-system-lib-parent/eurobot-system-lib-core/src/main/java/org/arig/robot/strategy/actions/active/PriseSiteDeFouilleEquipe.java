package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PriseSiteDeFouilleEquipe extends AbstractPriseSiteDeFouille {

    private static final int CENTRE_FOUILLE_X = 975;
    private static final int CENTRE_FOUILLE_Y = 625;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_SITE_FOUILLE_EQUIPE;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_DECOUVERTE_CARRE_FOUILLE);
    }

    @Override
    public int executionTimeMs() {
        return 4000 * 3; // TODO: A quantifier
    }

    @Override
    public boolean isValid() {
        if (!rs.siteDeFouille()) {
            return false;
        }

        return isTimeValid() && timeBeforeRetourValid()
                && !rs.siteDeFouillePris() && rs.stockDisponible() > 0;
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteDeFouillePris() || !rs.siteDeFouille()) {
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
        return rs.team() == Team.JAUNE ? Echantillon.ID.SITE_FOUILLE_JAUNE : Echantillon.ID.SITE_FOUILLE_VIOLET;
    }

    @Override
    public Point entryPoint() {
        final List<Echantillon> currentEchantillons = echantillonsSite(siteDeFouille());

        // Calcul point d'approche du site de fouille
        if (!currentEchantillons.isEmpty()) {
            final Echantillon echantillonPlusProche = currentEchantillons.get(0);
            final Point centreSiteDeFouille = new Point(getX(CENTRE_FOUILLE_X), CENTRE_FOUILLE_Y);
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
