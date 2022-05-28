package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Echantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Rectangle;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.arig.robot.constants.EurobotConfig.ECHANTILLON_SIZE;

@Slf4j
public abstract class AbstractPriseEchantillon extends AbstractEurobotAction {
    @Autowired
    protected BrasService bras;

    private Rectangle zoneDistributeur = new Rectangle(1200, 1750, 600, 250);

    protected final Echantillon echantillonPerdu() {
        final Point positionCourante = new Point(mv.currentXMm(), mv.currentYMm());
        return rs.echantillons().getEchantillons().stream()
                .filter(e -> {
                    if (zoneDistributeur.contains(e)) {
                        return false;
                    }
                    if (e.getId() == null) {
                        return true;
                    }
                    if (rs.siteDeFouillePris() && ((e.getId() == Echantillon.ID.SITE_FOUILLE_JAUNE && rs.team() == Team.JAUNE)
                            || (e.getId() == Echantillon.ID.SITE_FOUILLE_VIOLET && rs.team() == Team.VIOLET))) {
                        return true;
                    }
                    if (rs.siteDeFouilleAdversePris() && ((e.getId() == Echantillon.ID.SITE_FOUILLE_VIOLET && rs.team() == Team.JAUNE)
                            || (e.getId() == Echantillon.ID.SITE_FOUILLE_JAUNE && rs.team() == Team.VIOLET))) {
                        return true;
                    }
                    return false;
                })
                .sorted(Comparator.comparing(e -> e.distance(positionCourante)))
                .map(Echantillon::clone)
                .findFirst().orElse(null);
    }

    protected List<Echantillon> echantillonsSite(Echantillon.ID site) {
        final Point positionCourante = new Point(mv.currentXMm(), mv.currentYMm());
        return rs.echantillons().getEchantillons().stream()
                .filter(e -> e.getId() != null)
                .filter(e -> e.getId().equals(site))
                .sorted(Comparator.comparing(e -> e.distance(positionCourante)))
                .limit(3)
                .map(Echantillon::clone)
                .collect(Collectors.toList());
    }
}
