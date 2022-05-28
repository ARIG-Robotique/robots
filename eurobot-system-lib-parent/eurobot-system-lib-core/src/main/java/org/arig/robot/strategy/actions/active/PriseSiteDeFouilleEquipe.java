package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Echantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;

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
        if (rs.strategy() == Strategy.BASIC && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            return 3000;
        }
        return 3000 * 3; // TODO: A quantifier
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid()
                && !rs.siteDeFouillePris() && rs.stockDisponible() > 0;
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteDeFouillePris()) {
            complete();
        }
    }

    @Override
    public int order() {
        if ((rs.strategy() == Strategy.BASIC || rs.strategy() == Strategy.FINALE_2)
                && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            return 1000;
        }

        int stock = rs.stockDisponible();
        return Math.min(stock, 3) * EurobotConfig.PTS_DEPOSE_PRISE + tableUtils.alterOrder(entryPoint());
    }

    @Override
    protected Echantillon.ID siteDeFouille() {
        return rs.team() == Team.JAUNE ? Echantillon.ID.SITE_FOUILLE_JAUNE : Echantillon.ID.SITE_FOUILLE_VIOLET;
    }

    @Override
    protected void notifySitePris() {
        group.siteDeFouillePris();
    }

    @Override
    public Point entryPoint() {
        final List<Echantillon> currentEchantillons = echantillonsSite(siteDeFouille());

        // Calcul point d'approche du site de fouille
        if (!currentEchantillons.isEmpty()) {
            final Echantillon echantillonPlusProche = currentEchantillons.get(0);
            final Point centreSiteDeFouille = new Point(getX(CENTRE_FOUILLE_X_JAUNE), CENTRE_FOUILLE_Y);
            return new Point(
                    centreSiteDeFouille.getX() -
                            Math.signum(centreSiteDeFouille.getX() - echantillonPlusProche.getX()) * EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE / 2,
                    centreSiteDeFouille.getY() -
                            Math.signum(centreSiteDeFouille.getY() - echantillonPlusProche.getY()) * EurobotConfig.PATHFINDER_SITE_FOUILLE_SIZE / 2);
        }

        // Pas d'entry point
        return new Point(0, 0);
    }

    @Override
    public void execute() {
        super.execute();

        if ((rs.strategy() == Strategy.BASIC || rs.strategy() == Strategy.FINALE_2)
                && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            notifySitePris();
            complete();
        }
    }

    @Override
    protected List<Echantillon> echantillonsSite(Echantillon.ID site) {
        if ((rs.strategy() == Strategy.BASIC || rs.strategy() == Strategy.FINALE_2)
                && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            return Arrays.asList(super.echantillonsSite(site).get(0));
        } else {
            return super.echantillonsSite(site);
        }
    }
}
