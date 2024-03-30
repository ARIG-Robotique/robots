package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeCharge;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourSiteDeCharge extends AbstractEurobotAction {

    private static final int CENTER_X = 450;
    private static final int CENTER_Y = 1000;
    private static final int OFFSET = 550;

    private SiteDeCharge gotoSite;
    private SiteDeCharge destSite;

    @Override
    public String name() {
        return EurobotConfig.ACTION_RETOUR_SITE_DE_CHARGE;
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        /*final double distanceNord = rs.otherPosition().distance(pointNord());
        final double distanceMilieu = rs.otherPosition().distance(pointMilieu());
        final double distanceSud = rs.otherPosition().distance(pointSud());

        final double distanceMin = Math.min(distanceNord, Math.min(distanceSud, distanceMilieu));
        if (distanceMin == distanceNord) {
            gotoSite = rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_NORD : SiteDeCharge.WIP_JAUNE_NORD;
            destSite = rs.team() == Team.BLEU ? SiteDeCharge.BLEU_NORD : SiteDeCharge.JAUNE_NORD;
            return pointNord();
        } else if (distanceMin == distanceSud) {
            gotoSite = rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_SUD : SiteDeCharge.WIP_JAUNE_SUD;
            destSite = rs.team() == Team.BLEU ? SiteDeCharge.BLEU_SUD : SiteDeCharge.JAUNE_SUD;
            return pointSud();
        } else {
            gotoSite = rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_MILIEU : SiteDeCharge.WIP_JAUNE_MILIEU;
            destSite = rs.team() == Team.BLEU ? SiteDeCharge.BLEU_MILIEU : SiteDeCharge.JAUNE_MILIEU;
            return pointMilieu();
        }*/

        gotoSite = rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_SUD : SiteDeCharge.WIP_JAUNE_SUD;
        destSite = rs.team() == Team.BLEU ? SiteDeCharge.BLEU_SUD : SiteDeCharge.JAUNE_SUD;
        return pointSud();
    }

    @Override
    public int order() {
        return 10; // C'est 10 points et puis c'est tout
    }

    @Override
    public boolean isValid() {
        return ilEstTempsDeRentrer();
    }

    @Override
    public void execute() {
        try {
            // L'entry point calcul le chemin le plus court et défini gotoSite et destSite
            final Point entry = entryPoint();

            log.info("Go site de charge : {}", gotoSite);
            group.siteDeCharge(gotoSite);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
            group.siteDeCharge(destSite);
            log.info("Arrivée au site de charge : {}", destSite);
            group.siteDeCharge(gotoSite);

            boolean alt = false;
            mv.setVitesse(config.vitesse(), config.vitesseOrientation(50));
            do {
                alt = !alt;
                mv.tourneDeg(alt ? 90 : 45);
            } while (rs.getRemainingTime() > 100);

            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (!rs.siteDeCharge().isEnCharge() && !(e instanceof MovementCancelledException)) {
                group.siteDeCharge(SiteDeCharge.AUCUN);
            }
        }
    }

    private Point pointMilieu() {
        return new Point(getX(CENTER_X), CENTER_Y);
    }

    private Point pointNord() {
        return new Point(getX(CENTER_X), CENTER_Y + OFFSET);
    }

    private Point pointSud() {
        return new Point(getX(CENTER_X), CENTER_Y - OFFSET);
    }
}
