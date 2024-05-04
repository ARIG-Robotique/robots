package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.BrasListe;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import static org.arig.robot.services.BrasInstance.*;

@Slf4j
@Component
public class PriseStockPots extends AbstractNerellAction {

    final int ENTRY_DELTA = 340;
    final int ACTION_DELTA = 300;

    private StockPots stockPots;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_STOCK_POTS;
    }

    @Override
    public int executionTimeMs() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        boolean isValid = rs.strategy() != Strategy.BASIC
                && isTimeValid()
                && rs.bras().arriereLibre()
                && rs.getRemainingTime() > EurobotConfig.validTimePrisePots;

        if (!isValid) {
            return false;
        }

        stockPots = rs.stocksPots().getClosest(mv.currentPositionMm());
        return stockPots != null;
    }

    @Override
    public int order() {
        return rs.getElapsedTime() < 5000 ? 1000 : 0; // TODO
    }

    @Override
    public Point entryPoint() {
        switch (stockPots.getId()) {
            case BLEU_NORD:
            case BLEU_MILIEU:
                return new Point(ENTRY_DELTA, stockPots.getY());
            case JAUNE_NORD:
            case JAUNE_MILIEU:
                return new Point(3000 - ENTRY_DELTA, stockPots.getY());
            default:
                return new Point(stockPots.getX(), ENTRY_DELTA);
        }
    }

    private void s() {
        ThreadUtils.sleep(500);
    }

    @Override
    public void execute() {
        log.info("Prise stock pots {}", stockPots.getId());

        try {
            final Point entry = entryPoint();

            mv.setVitessePercent(100, 100);
            mv.pathTo(entry);

            if (!stockPots.isPresent()) {
                log.info("Le stock pots {} à été pris entre temps", stockPots.getId());
                updateValidTime();
                return;
            }

            // EMPILEMENT DES POTS

            mv.gotoOrientationDeg(stockPots.getEntryAngle());
            switch (stockPots.getId()) {
                case BLEU_NORD:
                case BLEU_MILIEU:
                    mv.gotoPoint(ACTION_DELTA, stockPots.getY());
                    break;
                case JAUNE_NORD:
                case JAUNE_MILIEU:
                    mv.gotoPoint(3000 - ACTION_DELTA, stockPots.getY());
                    break;
                default:
                    mv.gotoPoint(stockPots.getX(), ACTION_DELTA);
                    break;
            }
            mv.gotoOrientationDeg(stockPots.getEntryAngle());

            rs.disableAvoidance();

            mv.setVitessePercent(20, 100);
            mv.setRampesDistancePercent(100, 10);

            servos.setPortePotGlissiereSorti(false);
            io.enableElectroAimant();
            bras.setBrasArriere(new PointBras(220, SORTIE_POT_POT_Y - 4, -90, true));
//            s();
            rs.enableCalageBordure(TypeCalage.PRISE_ELECTROAIMANT);
            mv.reculeMM(ACTION_DELTA - 220); // 230 = y min pour prendre les pots
            s();
            if (!rs.calageCompleted().contains(TypeCalage.PRISE_ELECTROAIMANT)) {
                log.info("Impossible de prendre le stock de pots {}", stockPots.getId());
                stockPots.bloque();
                safeExit();
                return;
            }

            mv.avanceMM(20);

            servos.setPortePotHaut(true);

            rs.enableCalageTempo(2000);
            mv.reculeMM(130); // FIXME tempo et distance à revoir
            rs.disableCalageBordure();
            // ici on est à y=150 environ

            mv.setVitessePercent(50, 100);

            mv.avanceMM(20);
            io.disableElectroAimant();
            s();
            mv.avanceMM(90);
            servos.setPortePotBas(false);
            servos.setPortePotGlissiereRentre(true);

            // PRISE

            // se met au dessus des pots
            servos.groupePinceArriereOuvert(true);
            bras.setBrasArriere(PointBras.withY(100));
//            s();
            // attrape le pot du haut
            bras.setBrasArriere(PointBras.withY(73));
            s();
            servos.groupePinceArrierePrisePot(true);
            s();
            // décalle un peu vers le robot
            bras.setBrasArriere(PointBras.translated(-35, 10));
            // lache le pot du haut
            servos.groupePinceArriereOuvert(true);
            s();
            // attrape le pot du bas
            bras.setBrasArriere(PointBras.withY(PRISE_POT_SOL_Y - 5));
            servos.groupePinceArrierePrisePot(true);
            s();

            stockPots.pris();

            // FIN

            if (stockPots.getId() == StockPots.ID.JAUNE_NORD || stockPots.getId() == StockPots.ID.BLEU_NORD) {
                // stockage dans la zone de départ
                deposeZoneDepart();

            } else {
                bras.setBrasArriere(PositionBras.TRANSPORT);

                rs.bras().setArriere(
                        new Plante(TypePlante.AUCUNE, true),
                        new Plante(TypePlante.AUCUNE, true),
                        new Plante(TypePlante.AUCUNE, true)
                );

                // stockage dans la zone de départ
                if (rs.potsInZoneDepart() == 0) {
                    mv.setVitessePercent(100, 100);
                    mv.pathTo(getX(330), 1390);

                    deposeZoneDepart();
                }
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {

        }
    }

    private void deposeZoneDepart() throws AvoidingException {
        mv.gotoOrientationDeg(-90);
        bras.setBrasArriere(new PointBras(215, PRISE_POT_SOL_Y, -90, null));
        servos.groupePinceArriereOuvert(true);
        s();
        bras.setBrasArriere(PointBras.withY(SORTIE_POT_POT_Y));
        servos.groupePinceArriereFerme(false);
        runAsync(() -> {
            bras.setBrasArriere(PositionBras.INIT);
        });

        rs.bras().setArriere(null, null, null);
        rs.potsInZoneDepart(2);
        rs.positionPotsZoneDepart(mv.currentPositionMm());
    }

    private void safeExit() throws AvoidingException {
        io.disableElectroAimant();
        rs.enableAvoidance();
        mv.avanceMM(150);
        servos.setPortePotBas(false);
        servos.setPortePotGlissiereRentre(false);
        bras.setBrasArriere(PositionBras.INIT);
    }
}
