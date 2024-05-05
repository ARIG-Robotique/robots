package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPlantes;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.pathfinding.PathFinder;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.arig.robot.services.BrasInstance.PRISE_PLANTE_SOL_Y;

@Slf4j
@Component
public class PriseStockPlantes extends AbstractNerellAction {

    final int DST_APPROCHE = EurobotConfig.PATHFINDER_STOCK_PLANTES_SIZE / 2 + 5;

    private StockPlantes stockPlantes;

    @Autowired
    private PathFinder pathFinder;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_SITE_DE_PLANTES;
    }

    @Override
    public int executionTimeMs() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        boolean isValid = isTimeValid()
                && rs.bras().avantLibre()
                && rs.stockLibre()
                && rs.getRemainingTime() > EurobotConfig.validTimePrisePlantes;

        if (!isValid) {
            return false;
        }

        return rs.plantes().stocksPresents().findAny().isPresent();
    }

    @Override
    public int order() {
        return 18;
    }

    @Override
    public Point entryPoint() {
        stockPlantes = rs.plantes().getClosestStock(mv.currentPositionMm());
        List<Point> points = new ArrayList<>();
        switch (stockPlantes.getId()) {
            case STOCK_NORD:
                points.add(tableUtils.eloigner(new Point(1500, 1500), -DST_APPROCHE));
                break;
            case STOCK_SUD:
                points.add(tableUtils.eloigner(new Point(1500, 500), -DST_APPROCHE));
                break;
            case STOCK_NORD_OUEST:
                points.add(new Point(1000 - DST_APPROCHE, 1300));
                points.add(new Point(1000 + DST_APPROCHE, 1300));
                break;
            case STOCK_SUD_OUEST:
                points.add(new Point(1000 - DST_APPROCHE, 700));
                points.add(new Point(1000 + DST_APPROCHE, 700));
                break;
            case STOCK_NORD_EST:
                points.add(new Point(2000 - DST_APPROCHE, 1300));
                points.add(new Point(2000 + DST_APPROCHE, 1300));
                break;
            case STOCK_SUD_EST:
                points.add(new Point(2000 - DST_APPROCHE, 700));
                points.add(new Point(2000 + DST_APPROCHE, 700));
                break;
        }
        Point currentPositionMm = mv.currentPositionMm();
        return points.stream().min(Comparator.comparingDouble(pt -> pt.distance(currentPositionMm))).orElse(null);
    }

    @Override
    public void execute() {
        Point entry = entryPoint();
        log.info("Prise stock plantes {}", stockPlantes.getId());

        try {
            // pour les stocks nord et sud application du l'algo de recherche de point proche
            if (stockPlantes.getId() == Plante.ID.STOCK_NORD || stockPlantes.getId() == Plante.ID.STOCK_SUD) {
                Point entryCm = new Point(entry.getX() / 10, entry.getY() / 10);
                pathFinder.setObstacles(new ArrayList<>());
                if (pathFinder.isBlocked(entryCm)) {
                    Point fromCm = new Point(mv.currentXMm() / 10, mv.currentYMm() / 10);
                    entryCm = pathFinder.getNearestPoint(fromCm, entryCm);
                    if (entryCm == null) {
                        throw new NoPathFoundException(NoPathFoundException.ErrorType.NO_PATH_FOUND);
                    }
                    entry = new Point(entryCm.getX() * 10, entryCm.getY() * 10);
                }
            }

            mv.setVitessePercent(100, 100);
            mv.pathTo(entry);
            mv.alignFrontTo(stockPlantes);

            mv.setVitessePercent(20, 100);
            mv.setRampesDistancePercent(100, 10);

            final PointBras pointBrasApproche = new PointBras(195, 130, -90, null);

            // PREMIERE PRISE
            servos.groupeBloquePlanteOuvert(false);
            bras.setBrasAvant(pointBrasApproche);

            rs.enableCalageBordure(TypeCalage.PRISE_PRODUIT_AVANT);
            mv.gotoPoint(tableUtils.eloigner(stockPlantes, -100));

            if (!rs.calageCompleted().contains(TypeCalage.PRISE_PRODUIT_AVANT)) {
                onCancel();
                return;
            }

            boolean gauche = io.presenceAvantGauche(true);
            boolean centre = io.presenceAvantCentre(true);
            boolean droite = io.presenceAvantDroite(true);

            mv.setVitessePercent(100, 100);

            servos.groupeBloquePlantePrisePlante(true);
            mv.reculeMM(50);
            servos.groupeBloquePlanteOuvert(true);
            servos.groupePinceAvantOuvert(false);
            mv.reculeMM(100);
            bras.setBrasAvant(PointBras.withY(PRISE_PLANTE_SOL_Y));
            servos.groupePinceAvantPrisePlante(true);
            ThreadUtils.sleep(500);

            bras.brasAvantStockage();

            boolean stockgauche = io.presenceStockGauche(true);
            boolean stockcentre = io.presenceStockCentre(true);
            boolean stockdroite = io.presenceStockDroite(true);

            // le stockage à foiré
            if (gauche != stockgauche || centre != stockcentre || droite != stockdroite) {
                log.warn("Le stockage à foiré, dégagement");
                bras.setBrasAvant(PositionBras.TRANSPORT);
                mv.tourneDeg(360);
            }

            rs.setStock(
                    stockgauche ? TypePlante.INCONNU : null,
                    stockcentre ? TypePlante.INCONNU : null,
                    stockdroite ? TypePlante.INCONNU : null
            );

            // SECONDE PRISE
            servos.groupeBloquePlanteOuvert(false);
            bras.setBrasAvant(pointBrasApproche);

            mv.setVitessePercent(20, 100);
            mv.setRampesDistancePercent(100, 10);

            rs.enableCalageBordure(TypeCalage.PRISE_PRODUIT_AVANT);
            mv.gotoPoint(tableUtils.eloigner(stockPlantes, 50));

            if (!rs.calageCompleted().contains(TypeCalage.PRISE_PRODUIT_AVANT)) {
                onCancel();
                return;
            }

            mv.setVitessePercent(100, 100);

            servos.groupeBloquePlantePrisePlante(true);
            mv.reculeMM(50);
            servos.groupeBloquePlanteOuvert(true);
            servos.groupePinceAvantOuvert(false);
            mv.reculeMM(100);
            bras.setBrasAvant(PointBras.withY(PRISE_PLANTE_SOL_Y));
            servos.groupePinceAvantPrisePlante(true);
            ThreadUtils.sleep(500);

            bras.setBrasAvant(PositionBras.TRANSPORT);

            // FIXME
            gauche = true;//io.pinceAvantGaucheAverage(true);
            centre = true;//io.pinceAvantCentreAverage(true);
            droite = true;//io.pinceAvantDroiteAverage(true);

            rs.bras().setAvant(
                    gauche ? new Plante(TypePlante.INCONNU) : null,
                    centre ? new Plante(TypePlante.INCONNU) : null,
                    droite ? new Plante(TypePlante.INCONNU) : null
            );

            rs.plantes().priseStock(stockPlantes.getId());

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (e instanceof NoPathFoundException) {
                stockPlantes.setTimevalid(System.currentTimeMillis());
            }
        } finally {
            servos.groupeBloquePlanteFerme(false);
        }
    }

    private void onCancel() {
        log.warn("Le stock de plantes {} est vide", stockPlantes.getId());
        rs.plantes().priseStock(stockPlantes.getId());
        runAsync(() -> bras.brasAvantInit());
        servos.groupePinceAvantFerme(false);
        servos.groupeBloquePlanteFerme(false);
    }

}
