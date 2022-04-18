package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeRetour;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;

@Slf4j
@Component
public class RetourAuSiteDeFouille extends AbstractEurobotAction {

    private static final int CENTER_X_RAW = 975;
    private static final int CENTER_Y = 625;
    private static final int OFFSET = 175;

    private SiteDeRetour gotoSite;
    private SiteDeRetour destSite;

    @Override
    public String name() {
        return EurobotConfig.ACTION_RETOUR_SITE_DE_FOUILLE_PREFIX + robotName.id().name();
    }

    @Override
    public Point entryPoint() {
        if (rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN) {
            gotoSite = SiteDeRetour.WIP_FOUILLE_CENTRE;
            destSite = SiteDeRetour.FOUILLE_CENTRE;
            return pointCentre();
        }

        final double distanceNord = tableUtils.distance(pointNord());
        final double distanceSud = tableUtils.distance(pointSud());
        final double distanceEst = tableUtils.distance(pointEst());
        final double distanceOuest = tableUtils.distance(pointOuest());

        final double distanceMin = Math.min(distanceNord, Math.min(distanceSud, Math.min(distanceEst, distanceOuest)));
        if (distanceMin == distanceNord) {
            gotoSite = SiteDeRetour.WIP_FOUILLE_NORD;
            destSite = SiteDeRetour.FOUILLE_NORD;
            return pointNord();
        } else if (distanceMin == distanceSud) {
            gotoSite = SiteDeRetour.WIP_FOUILLE_SUD;
            destSite = SiteDeRetour.FOUILLE_SUD;
            return pointSud();
        } else if (distanceMin == distanceEst) {
            gotoSite = SiteDeRetour.WIP_FOUILLE_EST;
            destSite = SiteDeRetour.FOUILLE_EST;
            return pointEst();
        } else {
            gotoSite = SiteDeRetour.WIP_FOUILLE_OUEST;
            destSite = SiteDeRetour.FOUILLE_OUEST;
            return pointOuest();
        }
    }

    @Override
    public int order() {
        return 20; // C'est 20 point et puis c'est tout
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !remainingTimeValid();
    }

    @Override
    public void execute() {
        try {
            // On ignore toute la zone de fouille
            tableUtils.addDynamicDeadZone(new Rectangle2D.Double(pointCentre().getX() - OFFSET, pointCentre().getY() - OFFSET, 2 * OFFSET, 2 * OFFSET));

            final Point entry = entryPoint();
            log.info("Go site de fouille : {}", gotoSite);
            group.siteDeRetour(gotoSite);

            rs.enableAvoidance();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
            group.siteDeRetour(destSite);
            log.info("Arrivé site de fouille : {}", destSite);

            if (rs.twoRobots() && rs.siteDeRetour() == SiteDeRetour.FOUILLE_CENTRE) {
                // Premier (allé au centre), on attend que le second dise ou il va pour aller à l'opposé.
                SiteDeRetour autre;
                do {
                    ThreadUtils.sleep(10);
                    autre = rs.siteDeRetourAutreRobot();
                } while (autre == SiteDeRetour.AUCUN);
                log.info("L'autre robot va au site de fouille : {}", autre);

                // L'autre robot a choisis sa destination
                if (autre == SiteDeRetour.WIP_FOUILLE_NORD || autre == SiteDeRetour.FOUILLE_NORD) {
                    log.info("On va au sud");
                    mv.gotoPoint(pointSud());
                    group.siteDeRetour(SiteDeRetour.FOUILLE_SUD);
                } else if (autre == SiteDeRetour.WIP_FOUILLE_SUD || autre == SiteDeRetour.FOUILLE_SUD) {
                    log.info("On va au nord");
                    mv.gotoPoint(pointNord());
                    group.siteDeRetour(SiteDeRetour.FOUILLE_NORD);
                } else if (autre == SiteDeRetour.WIP_FOUILLE_EST || autre == SiteDeRetour.FOUILLE_EST) {
                    log.info("On va a l'ouest");
                    mv.gotoPoint(pointOuest());
                    group.siteDeRetour(SiteDeRetour.FOUILLE_OUEST);
                } else {
                    log.info("On va a l'est");
                    mv.gotoPoint(pointEst());
                    group.siteDeRetour(SiteDeRetour.FOUILLE_EST);
                }
            }

            log.info("Danse de la fouille !!!!");
            boolean alt = false;
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation(50));
            do {
                alt = !alt;
                mv.tourneDeg(alt ? 90 : 45);
            } while (rs.getRemainingTime() > 100);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (!rs.siteDeRetour().isInSite()) {
                group.siteDeRetour(SiteDeRetour.AUCUN);
            }
        }
    }

    private Point pointCentre() {
        return new Point(getX(CENTER_X_RAW), CENTER_Y);
    }

    private Point pointNord() {
        return new Point(getX(CENTER_X_RAW), CENTER_Y + OFFSET);
    }

    private Point pointSud() {
        return new Point(getX(CENTER_X_RAW), CENTER_Y - OFFSET);
    }

    private Point pointEst() {
        return new Point(getX(CENTER_X_RAW) + OFFSET, CENTER_Y);
    }

    private Point pointOuest() {
        return new Point(getX(CENTER_X_RAW) - OFFSET, CENTER_Y);
    }
}
