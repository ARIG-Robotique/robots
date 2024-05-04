package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MatchDoneException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.SiteDeCharge;
import org.arig.robot.model.Team;
import org.arig.robot.model.ZoneDepose;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.arig.robot.services.BrasInstance.DEPOSE_SOL_Y;
import static org.arig.robot.services.BrasInstance.SORTIE_POT_POT_Y;

@Slf4j
@Component
public class RetourSiteDeCharge extends AbstractNerellAction {

    private static final int CENTER_X = 450;
    private static final int CENTER_Y = 1000;
    private static final int OFFSET = 550;

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
        return candidates().get(0).getRight();
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
        var candidates = candidates();

        mv.setVitessePercent(100, 100);

        for (var candidate : candidates) {
            final var destSite = candidate.getLeft();
            final var gotoSite = candidate.getMiddle();
            final var entry = candidate.getRight();

            try {
                log.info("Go site de charge : {}", gotoSite);
                group.siteDeCharge(gotoSite);

                mv.pathTo(entry, GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
                group.siteDeCharge(destSite);
                log.info("Arrivée au site de charge : {}", destSite);

                complete(true);
                rs.disableAvoidance();

                mv.setVitessePercent(50, 100);

                // TODO stock avant

                boolean isEnAvant = false;
                if (!rs.bras().avantLibre() && !rs.bras().arriereLibre()) {
                    mv.gotoOrientationDeg(destSite.getAngleDeposeAvant());
                    checkMatchDone();
                    mv.avanceMM(200);
                    checkMatchDone();
                    isEnAvant = true;
                } else if (!rs.bras().avantLibre()) {
                    mv.gotoOrientationDeg(destSite.getAngleDeposeAvant());
                    checkMatchDone();
                    mv.avanceMM(100);
                    checkMatchDone();
                    isEnAvant = true;
                } else if (!rs.bras().arriereLibre()) {
                    mv.gotoOrientationDeg(destSite.getAngleDeposeAvant() + 180);
                    checkMatchDone();
                    mv.reculeMM(100);
                    checkMatchDone();
                }

                if (!rs.bras().avantLibre()) {
                    log.info("Dépose des bras avant");

                    bras.setBrasAvant(new PointBras(215, DEPOSE_SOL_Y, -90, null));
                    checkMatchDone();
                    servos.groupePinceAvantOuvert(true);
                    zoneDepose(destSite).add(rs.bras().getAvant());
                    ThreadUtils.sleep(500);
                    checkMatchDone();

                    bras.setBrasAvant(PointBras.withY(SORTIE_POT_POT_Y));
                    checkMatchDone();
                    servos.groupePinceAvantFerme(false);
                    mv.reculeMM(100);
                    checkMatchDone();
                    bras.setBrasAvant(PositionBras.INIT);
                    checkMatchDone();
                }

                if (!rs.bras().arriereLibre()) {
                    log.info("Dépose des bras arrière");

                    if (isEnAvant) {
                        mv.gotoOrientationDeg(destSite.getAngleDeposeAvant() + 180);
                        checkMatchDone();
                    }

                    bras.setBrasArriere(new PointBras(215, DEPOSE_SOL_Y, -90, null));
                    checkMatchDone();
                    servos.groupePinceArriereOuvert(true);
                    zoneDepose(destSite).add(rs.bras().getArriere());
                    ThreadUtils.sleep(500);
                    checkMatchDone();

                    bras.setBrasArriere(PointBras.withY(SORTIE_POT_POT_Y));
                    checkMatchDone();
                    servos.groupePinceArriereFerme(false);
                    mv.avanceMM(100);
                    checkMatchDone();
                    bras.setBrasArriere(PositionBras.INIT);
                }

                break;

            } catch (NoPathFoundException | AvoidingException e) {
                log.warn("Impossible d'aller au site " + destSite.name() + ": {}", e.toString());
            } catch (MatchDoneException e) {
                log.info("La fin de match est survenue pendant l'action");
                return;
            }
        }

        if (!isCompleted()) {
            log.error("Erreur d'exécution de l'action");
            updateValidTime();
            group.siteDeCharge(SiteDeCharge.AUCUN);
        }
    }

    private Point pointMilieu() {
        return new Point(tableUtils.getX(rs.team() == Team.BLEU, CENTER_X), CENTER_Y);
    }

    private Point pointNord() {
        return new Point(getX(CENTER_X), CENTER_Y + OFFSET);
    }

    private Point pointSud() {
        return new Point(getX(CENTER_X), CENTER_Y - OFFSET);
    }

    private List<Triple<SiteDeCharge, SiteDeCharge, Point>> candidates() {
        final List<Triple<SiteDeCharge, SiteDeCharge, Point>> candidates = new ArrayList<>();

        if (rs.siteDeDepart() != SiteDeCharge.BLEU_NORD && rs.siteDeDepart() != SiteDeCharge.JAUNE_NORD) {
            candidates.add(Triple.of(
                    rs.team() == Team.BLEU ? SiteDeCharge.BLEU_NORD : SiteDeCharge.JAUNE_NORD,
                    rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_NORD : SiteDeCharge.WIP_JAUNE_NORD,
                    pointNord()
            ));
        }
        if (rs.siteDeDepart() != SiteDeCharge.BLEU_MILIEU && rs.siteDeDepart() != SiteDeCharge.JAUNE_MILIEU) {
            candidates.add(Triple.of(
                    rs.team() == Team.BLEU ? SiteDeCharge.BLEU_MILIEU : SiteDeCharge.JAUNE_MILIEU,
                    rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_MILIEU : SiteDeCharge.WIP_JAUNE_MILIEU,
                    pointMilieu()
            ));
        }
        if (rs.siteDeDepart() != SiteDeCharge.BLEU_SUD && rs.siteDeDepart() != SiteDeCharge.JAUNE_SUD) {
            candidates.add(Triple.of(
                    rs.team() == Team.BLEU ? SiteDeCharge.BLEU_SUD : SiteDeCharge.JAUNE_SUD,
                    rs.team() == Team.BLEU ? SiteDeCharge.WIP_BLEU_SUD : SiteDeCharge.WIP_JAUNE_SUD,
                    pointSud()
            ));
        }

        final Point currentPosition = mv.currentPositionMm();
        candidates.sort(Comparator.comparing((Triple<SiteDeCharge, SiteDeCharge, Point> c) -> currentPosition.distance(c.getRight())));
        return candidates;
    }

    private ZoneDepose zoneDepose(SiteDeCharge site) {
        switch (site) {
            case JAUNE_MILIEU:
            case BLEU_MILIEU:
                return rs.aireDeDeposeMilieu();
            case JAUNE_NORD:
            case BLEU_NORD:
                return rs.aireDeDeposeNord();
            default:
                return rs.aireDeDeposeSud();
        }
    }

}
