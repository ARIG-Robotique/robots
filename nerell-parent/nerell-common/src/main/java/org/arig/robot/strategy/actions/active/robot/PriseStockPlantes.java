package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPlantes;
import org.arig.robot.model.Team;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.arig.robot.services.BrasInstance.PRISE_PLANTE_SOL_Y;

@Slf4j
@Component
public class PriseStockPlantes extends AbstractNerellAction {

    final int DST_APPROCHE = 300;

    private StockPlantes stockPlantes;

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

        stockPlantes = rs.plantes().getClosest(mv.currentPositionMm());
        return stockPlantes != null;
    }

    @Override
    public int order() {
        return rs.potsInZoneDepart() > 1 ? 15 : 10; // TODO
    }

    @Override
    public Point entryPoint() {
        // FIXME meilleur système qui prend en compte le pathfinding
        // FIXME points d'approches débloqués si site voisin déjà pris
        List<Point> points = new ArrayList<>();
        switch (stockPlantes.getId()) {
            case STOCK_NORD:
                points.add(new Point(1500, 1200));
                points.add(new Point(getX(1200), 1600));
                break;
            case STOCK_SUD:
                points.add(new Point(1500, 200));
                if (rs.team() == Team.BLEU) {
                    points.add(new Point(1200, 400));
                }
                if (rs.team() == Team.JAUNE) {
                    points.add(new Point(1800, 400));
                }
                break;
            case STOCK_NORD_OUEST:
                points.add(new Point(1250, 1100));
                if (rs.team() == Team.BLEU) {
                    points.add(new Point(700, 1300));
                }
                break;
            case STOCK_SUD_OUEST:
                points.add(new Point(1250, 900));
                points.add(new Point(1000, 400));
                if (rs.team() == Team.BLEU) {
                    points.add(new Point(700, 700));
                }
                break;
            case STOCK_NORD_EST:
                points.add(new Point(3000 - 1250, 1100));
                if (rs.team() == Team.JAUNE) {
                    points.add(new Point(3000 - 700, 1300));
                }
                break;
            case STOCK_SUD_EST:
                points.add(new Point(3000 - 1250, 900));
                points.add(new Point(3000 - 1000, 400));
                if (rs.team() == Team.JAUNE) {
                    points.add(new Point(3000 - 700, 700));
                }
                break;
        }
        Point currentPositionMm = mv.currentPositionMm();
        return points.stream().min(Comparator.comparingDouble(pt -> pt.distance(currentPositionMm))).orElse(null);
    }

    @Override
    public void execute() {
        log.info("Prise stock plantes {}", stockPlantes.getId());

        try {
            final Point entry = entryPoint();

            mv.setVitessePercent(100, 100);
            mv.pathTo(entry);
            mv.alignFrontTo(stockPlantes);

            mv.setVitessePercent(20, 100);
            mv.setRampesDistancePercent(100, 10);

            final PointBras pointBrasApproche = new PointBras(195, 130, -90, null);

            // PREMIERE PRISE
            servos.groupeBloquePlanteOuvert(false);
            bras.setBrasAvant(pointBrasApproche);

            mv.gotoPoint(stockPlantes);

            // TODO les capteurs pinguent trop tot
            // TODO avancer un peu plus ? augmenter le temps d'intégration ?
            //rs.enableCalageBordure(TypeCalage.PRISE_PRODUIT_AVANT);
//            mv.gotoPoint(tableUtils.eloigner(stockPlantes, -100));

//            if (!rs.calageCompleted().contains(TypeCalage.PRISE_PRODUIT_AVANT)) {
//                log.info("Le stock plantes {} est vide", stockPlantes.getId());
//                rs.plantes().priseStock(stockPlantes.getId());
//                bras.setBrasAvant(PositionBras.INIT);
//                servos.groupeBloquePlanteFerme(false);
//                return;
//            }

            boolean gauche = io.presenceAvantGauche(true);
            boolean centre = io.presenceAvantCentre(true);
            boolean droite = io.presenceAvantDroite(true);

            if (!gauche && !centre && !droite) {
                log.warn("Le stock de plantes {} est vide", stockPlantes.getId());
                rs.plantes().priseStock(stockPlantes.getId());
                runAsync(() -> {
                    bras.setBrasAvant(PositionBras.INIT);
                });
                return;
            }

            servos.groupeBloquePlantePrisePlante(true);
            mv.setVitessePercent(100, 100);
            mv.reculeMM(50);
            servos.groupeBloquePlanteOuvert(true);
            servos.groupePinceAvantOuvert(false);
            mv.reculeMM(100);
            bras.setBrasAvant(PointBras.withY(PRISE_PLANTE_SOL_Y));

            servos.groupePinceAvantPrisePlante(true);
            ThreadUtils.sleep(500);

            gauche |= io.pinceAvantGauche(true);
            centre |= io.pinceAvantCentre(true);
            droite |= io.pinceAvantDroite(true);

            rs.bras().setAvant(
                    gauche ? new Plante(TypePlante.INCONNU) : null,
                    centre ? new Plante(TypePlante.INCONNU) : null,
                    droite ? new Plante(TypePlante.INCONNU) : null
            );

            rs.plantes().priseStock(stockPlantes.getId());

            runAsync(() -> {
                bras.setBrasAvant(PositionBras.INIT);
            });

            /*
            bras.brasAvantStockage();

            gauche = io.presenceStockGauche();
            centre = io.presenceStockCentre();
            droite = io.presenceStockDroite();

            // FIXME vrais transfert des états
            rs.setStock(
                    gauche ? TypePlante.INCONNU : null,
                    centre ? TypePlante.INCONNU : null,
                    droite ? TypePlante.INCONNU : null
            );
            rs.bras().setAvant(null, null, null);

            // SECONDE PRISE
            servos.groupeBloquePlanteOuvert(false);
            bras.setBrasAvant(pointBrasApproche);
            mv.gotoPoint(stockPlantes);

            gauche = io.presenceAvantGauche();
            centre = io.presenceAvantCentre();
            droite = io.presenceAvantDroite();

            servos.groupeBloquePlantePrisePlante(true);
            mv.reculeMM(50);
            servos.groupeBloquePlanteOuvert(true);
            servos.groupePinceAvantOuvert(false);
            mv.reculeMM(90);
            servos.groupeBloquePlanteFerme(false);
            bras.setBrasAvant(PointBras.withY(PRISE_PLANTE_SOL_Y));
            servos.groupePinceAvantFerme(true);
            bras.setBrasAvant(PositionBras.TRANSPORT);

            gauche |= io.pinceAvantGauche();
            centre |= io.pinceAvantCentre();
            droite |= io.pinceAvantDroite();

            rs.bras().setAvant(
                    gauche ? BrasListe.Contenu.PLANTE_INCONNU : null,
                    centre ? BrasListe.Contenu.PLANTE_INCONNU : null,
                    droite ? BrasListe.Contenu.PLANTE_INCONNU : null
            );

            rs.plantes().priseStock(stockPlantes.getId());
             */

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            servos.groupeBloquePlanteFerme(false);
        }
    }

}
