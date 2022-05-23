package org.arig.robot.strategy.actions.disabled;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeRetour;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;

@Slf4j
@Component
public class RetourAuSiteDeFouille extends AbstractEurobotAction {

    private static final int CENTER_X = 975;
    private static final int CENTER_Y = 625;
    private static final int OFFSET = 175;

    private SiteDeRetour gotoSite;
    private SiteDeRetour destSite;

    @Override
    public String name() {
        return EurobotConfig.ACTION_RETOUR_SITE_DE_FOUILLE_PREFIX + robotName.id().name();
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        if (!rs.twoRobots()) {
            // un seul robot : on va au centre
            gotoSite = SiteDeRetour.WIP_FOUILLE_CENTRE;
            destSite = SiteDeRetour.FOUILLE_CENTRE;
            return pointCentre();

        } else if (rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN) {
            // le premier robot calcule le chemin le plus court pour l'autre robot
            final double distanceNord = rs.otherPosition().distance(pointNord());
            final double distanceSud = rs.otherPosition().distance(pointSud());
            final double distanceEst = rs.otherPosition().distance(pointEst());
            final double distanceOuest = rs.otherPosition().distance(pointOuest());

            final double distanceMin = Math.min(distanceNord, Math.min(distanceSud, Math.min(distanceEst, distanceOuest)));
            if (distanceMin == distanceSud) {
                gotoSite = SiteDeRetour.WIP_FOUILLE_NORD;
                destSite = SiteDeRetour.FOUILLE_NORD;
                return pointNord();
            } else if (distanceMin == distanceNord) {
                gotoSite = SiteDeRetour.WIP_FOUILLE_SUD;
                destSite = SiteDeRetour.FOUILLE_SUD;
                return pointSud();
            } else if (distanceMin == distanceOuest) {
                gotoSite = SiteDeRetour.WIP_FOUILLE_EST;
                destSite = SiteDeRetour.FOUILLE_EST;
                return pointEst();
            } else {
                gotoSite = SiteDeRetour.WIP_FOUILLE_OUEST;
                destSite = SiteDeRetour.FOUILLE_OUEST;
                return pointOuest();
            }

        } else {
            // le second robot va a l'emplacement correspondant
            switch (rs.siteDeRetourAutreRobot()) {
                case FOUILLE_NORD:
                case WIP_FOUILLE_NORD:
                    gotoSite = SiteDeRetour.WIP_FOUILLE_SUD;
                    destSite = SiteDeRetour.FOUILLE_SUD;
                    return pointSud();
                case FOUILLE_SUD:
                case WIP_FOUILLE_SUD:
                    gotoSite = SiteDeRetour.WIP_FOUILLE_NORD;
                    destSite = SiteDeRetour.FOUILLE_NORD;
                    return pointNord();
                case FOUILLE_EST:
                case WIP_FOUILLE_EST:
                    gotoSite = SiteDeRetour.WIP_FOUILLE_OUEST;
                    destSite = SiteDeRetour.FOUILLE_OUEST;
                    return pointOuest();
                case FOUILLE_OUEST:
                case WIP_FOUILLE_OUEST:
                    gotoSite = SiteDeRetour.WIP_FOUILLE_EST;
                    destSite = SiteDeRetour.FOUILLE_EST;
                    return pointEst();
                default:
                    throw new IllegalArgumentException("Etat incohérent, l'autre robot est au campement");
            }
        }
    }

    @Override
    public int order() {
        return 20; // C'est 20 point et puis c'est tout
    }

    @Override
    public boolean isValid() {
        return (rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN || rs.siteDeRetourAutreRobot().isFouille())
                && !remainingTimeBeforeRetourSiteValid();
    }

    @Override
    public void execute() {
        try {
            // L'entry point calcul le chemin le plus court et défini gotoSite et destSite
            final Point entry = entryPoint();

            if (rs.siteDeRetourAutreRobot().isInSite()) {
                final Point pointDestAutreRobot;
                switch (destSite) {
                    case FOUILLE_NORD:
                        pointDestAutreRobot = pointSud();
                        break;
                    case FOUILLE_SUD:
                        pointDestAutreRobot = pointNord();
                        break;
                    case FOUILLE_EST:
                        pointDestAutreRobot = pointOuest();
                        break;
                    default:
                        pointDestAutreRobot = pointEst();
                        break;
                }

                // On ignore la zone de la ou se trouve l'autre robot
                tableUtils.addDynamicDeadZone(
                        new Rectangle2D.Double(
                                pointDestAutreRobot.getX() - 100,
                                pointDestAutreRobot.getY() - 100,
                                200,
                                200
                        )
                );
            }

            log.info("Go site de fouille : {}", gotoSite);
            group.siteDeRetour(gotoSite);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
            group.siteDeRetour(destSite);
            log.info("Arrivée au site de fouille : {}", destSite);

            boolean alt = false;
            mv.setVitesse(config.vitesse(), config.vitesseOrientation(50));
            do {
                alt = !alt;
                mv.tourneDeg(alt ? 90 : 45);
            } while (rs.getRemainingTime() > 100);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (!rs.siteDeRetour().isInSite() && !(e instanceof MovementCancelledException)) {
                group.siteDeRetour(SiteDeRetour.AUCUN);
            }
        }
    }

    private Point pointCentre() {
        return new Point(getX(CENTER_X), CENTER_Y);
    }

    private Point pointNord() {
        return new Point(getX(CENTER_X), CENTER_Y + OFFSET);
    }

    private Point pointSud() {
        return new Point(getX(CENTER_X), CENTER_Y - OFFSET);
    }

    private Point pointEst() {
        return new Point(getX(CENTER_X) + OFFSET, CENTER_Y);
    }

    private Point pointOuest() {
        return new Point(getX(CENTER_X) - OFFSET, CENTER_Y);
    }
}
