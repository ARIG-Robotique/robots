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

    protected final int CENTER_X = 1300;

    private SiteDeRetour gotoSite;
    private SiteDeRetour destSite;

    @Override
    public Point entryPoint() {
        if (!rs.twoRobots()) {
            return entrySud();
        }

        if (rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN) {
            // l'autre fait une dépose
            if (rs.otherCampement() == Campement.Position.NORD) {
                return entrySud();
            }
            if (rs.otherCampement() == Campement.Position.SUD) {
                return entryNord();
            }

            // on est déjà sur site
            int currentX = getX((int) mv.currentXMm());
            int currentY = (int) mv.currentYMm();
            if (currentX <= 450 && currentY >= 950 && currentY <= 1650) {
                if (currentY > 1300) {
                    Point pt = entryNord();
                    gotoSite = SiteDeRetour.CAMPEMENT_NORD;
                    return pt;
                } else {
                    Point pt = entrySud();
                    gotoSite = SiteDeRetour.CAMPEMENT_SUD;
                    return pt;
                }
            }

            if (rs.galerieComplete()) {
                return entryNord();
            } else {
                return entrySud();
            }
        }

        if (rs.siteDeRetourAutreRobot() == SiteDeRetour.CAMPEMENT_SUD || rs.siteDeRetourAutreRobot() == SiteDeRetour.WIP_CAMPEMENT_SUD) {
            return entryNord();
        } else {
            return entrySud();
        }
    }

    private Point entryNord() {
        position = Campement.Position.NORD;
        gotoSite = SiteDeRetour.WIP_CAMPEMENT_NORD;
        destSite = SiteDeRetour.CAMPEMENT_NORD;
        return new Point(getX(X), Y);
    }

    private Point entrySud() {
        position = Campement.Position.SUD;
        gotoSite = SiteDeRetour.WIP_CAMPEMENT_SUD;
        destSite = SiteDeRetour.CAMPEMENT_SUD;
        return new Point(getX(X), CENTER_X - (Y - CENTER_X));
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
        boolean siteValid = rs.siteDeRetourAutreRobot() == SiteDeRetour.AUCUN || rs.siteDeRetourAutreRobot().isCampement();
        return siteValid && !timeBeforeRetourValid();
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();

            group.siteDeRetour(gotoSite);

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            group.siteDeRetour(destSite);

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

            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            bras.safeHoming();

            if (!rs.siteDeRetour().isInSite() && !(e instanceof MovementCancelledException)) {
                group.siteDeRetour(SiteDeRetour.AUCUN);
            }
        }
    }

}
