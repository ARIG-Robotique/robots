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
import org.arig.robot.model.enums.GotoOption;
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

    public static final int DST_APPROCHE = 350;

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

            mv.setVitessePercent(30, 100);
            mv.setRampesDistancePercent(100, 20);

            final PointBras pointBrasApproche = new PointBras(220, 145, -100, null);
            final PointBras pointBrasPrise = new PointBras(195, PRISE_PLANTE_SOL_Y, -90, null);

            // PREMIERE PRISE
            servos.groupeBloquePlanteOuvert(false);
            bras.setBrasAvant(pointBrasApproche);

            rs.enableCalageBordure(TypeCalage.PRISE_PRODUIT_AVANT);
            mv.gotoPoint(stockPlantes, GotoOption.SANS_ORIENTATION);

            if (!rs.calageCompleted().contains(TypeCalage.PRISE_PRODUIT_AVANT)) {
                onCancel(true);
                servos.groupePinceAvantFerme(false);
                return;
            }

//            boolean gauche = io.presenceAvantGauche(true);
//            boolean centre = io.presenceAvantCentre(true);
//            boolean droite = io.presenceAvantDroite(true);

            mv.setVitessePercent(100, 100);

            servos.groupeBloquePlantePrisePlante(true);
            mv.reculeMM(50);
            servos.groupeBloquePlanteOuvert(true);
            servos.groupePinceAvantOuvert(false);
            mv.reculeMM(100);
            bras.setBrasAvant(pointBrasPrise);
            servos.groupePinceAvantPrisePlante(true);

            boolean[] result = waitCapteursPinces(1000, true);
            boolean gauche = result[0];
            boolean centre = result[1];
            boolean droite = result[2];

            if (rs.stockage()) {
                bras.brasAvantStockage();

                boolean stockgauche = io.presenceStockGauche(true);
                boolean stockcentre = io.presenceStockCentre(true);
                boolean stockdroite = io.presenceStockDroite(true);

                // le stockage à foiré
                if (gauche && !stockgauche || centre && !stockcentre || droite && !stockdroite) {
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
                servos.groupePinceAvantOuvert(false);
                bras.setBrasAvant(pointBrasApproche);

                mv.setVitessePercent(30, 100);
                mv.setRampesDistancePercent(100, 20);

                rs.enableCalageBordure(TypeCalage.PRISE_PRODUIT_AVANT);
                mv.gotoPoint(tableUtils.eloigner(stockPlantes, 100), GotoOption.SANS_ORIENTATION);

                servos.groupeBloquePlantePrisePlante(true);

                gauche = io.presenceAvantGauche(true);
                centre = io.presenceAvantCentre(true);
                droite = io.presenceAvantDroite(true);

                if (!gauche && !centre && !droite) {
                    onCancel(true);
                    servos.groupePinceAvantFerme(false);
                    return;
                }

                mv.setVitessePercent(100, 100);

                servos.groupeBloquePlanteOuvert(true);
                mv.reculeMM(100);
                bras.setBrasAvant(pointBrasPrise);
                servos.groupePinceAvantPrisePlante(true);

                result = waitCapteursPinces(1000, true);
                gauche = result[0];
                centre = result[1];
                droite = result[2];

                rs.bras().setAvant(
                        gauche ? new Plante(TypePlante.INCONNU) : null,
                        centre ? new Plante(TypePlante.INCONNU) : null,
                        droite ? new Plante(TypePlante.INCONNU) : null
                );

                bras.setBrasAvant(PointBras.withY(80));

            } else {
                rs.bras().setAvant(
                        gauche ? new Plante(TypePlante.INCONNU) : null,
                        centre ? new Plante(TypePlante.INCONNU) : null,
                        droite ? new Plante(TypePlante.INCONNU) : null
                );

                servos.groupeBloquePlanteFerme(false);
                runAsync(() -> {
                    bras.setBrasAvant(PointBras.withY(80));
                    bras.brasAvantInit();
                });

                mv.tourneDeg(180);
                bras.setBrasArriere(pointBrasApproche);

                mv.setVitessePercent(20, 100);
                mv.setRampesDistancePercent(100, 20);

                rs.enableCalageBordure(TypeCalage.PRISE_PRODUIT_ARRIERE);
                mv.gotoPoint(stockPlantes, GotoOption.SANS_ORIENTATION);

                if (!rs.calageCompleted().contains(TypeCalage.PRISE_PRODUIT_ARRIERE)) {
                    onCancel(false);
                    runAsync(() -> bras.setBrasArriere(PositionBras.INIT));
                    servos.groupePinceArriereFerme(false);
                    return;
                }

                gauche = io.presenceArriereGauche(true);
                centre = io.presenceArriereCentre(true);
                droite = io.presenceArriereDroite(true);

                mv.setVitessePercent(100, 100);

                servos.groupePinceArriereOuvert(false);
                mv.avanceMM(100);
                bras.setBrasArriere(pointBrasPrise);
                servos.groupePinceArrierePrisePlante(true);

                result = waitCapteursPinces(1000, false);
                gauche = result[0];
                centre = result[1];
                droite = result[2];

                rs.bras().setArriere(
                        gauche ? new Plante(TypePlante.INCONNU) : null,
                        centre ? new Plante(TypePlante.INCONNU) : null,
                        droite ? new Plante(TypePlante.INCONNU) : null
                );

                bras.setBrasArriere(PointBras.withY(80));
            }

            rs.plantes().priseStock(stockPlantes.getId());

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

            if (e instanceof NoPathFoundException) {
                stockPlantes.setTimevalid(System.currentTimeMillis());
            }
        } finally {
            runAsync(() -> {
                bras.brasAvantInit();
                bras.setBrasArriere(PositionBras.INIT);
            });
            servos.groupeBloquePlanteFerme(false);
        }
    }

    private void onCancel(boolean enAvant) throws AvoidingException {
        log.warn("Le stock de plantes {} est vide", stockPlantes.getId());
        if (enAvant) mv.reculeMM(100);
        else mv.avanceMM(100);
        rs.plantes().priseStock(stockPlantes.getId());
    }

    private boolean[] waitCapteursPinces(int maxTimeMs, boolean avant) {
        long endTimeMs = System.currentTimeMillis() + maxTimeMs;
        boolean[] result = new boolean[]{ false, false, false };

        do {
            if (avant) {
                result[0] = result[0] || io.pinceAvantGauche(true);
                result[1] = result[1] || io.pinceAvantCentre(true);
                result[2] = result[2] || io.pinceAvantDroite(true);
            } else {
                result[0] = result[0] || io.pinceArriereGauche(true);
                result[1] = result[1] || io.pinceArriereCentre(true);
                result[2] = result[2] || io.pinceArriereDroite(true);
            }

            if (result[0] && result[1] && result[2]) {
                break;
            }

            ThreadUtils.sleep(config.i2cReadTimeMs());

        } while (System.currentTimeMillis() < endTimeMs);

        return result;
    }

}
