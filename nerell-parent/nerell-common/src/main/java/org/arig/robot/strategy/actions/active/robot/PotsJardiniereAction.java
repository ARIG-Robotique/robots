package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bras;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.StockPots;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.arig.robot.services.BrasInstance.*;

@Slf4j
@Component
public class PotsJardiniereAction extends AbstractNerellAction {

    @Autowired(required = false)
    private JardiniereMilieuAction jardiniereMilieuAction;

    @Autowired(required = false)
    private PoussePlanteNord actionPoussePlante;

    @Override
    public String name() {
        return "Pots jardinière";
    }

    private StockPots stockPots() {
        return rs.stocksPots().get(rs.team() == Team.JAUNE ? StockPots.ID.JAUNE_NORD : StockPots.ID.BLEU_NORD);
    }

    @Override
    public int executionTimeMs() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        return rs.prisePots()
                && isTimeValid()
                && rs.bras().arriereLibre()
                && rs.getRemainingTime() > EurobotConfig.validTimePrisePots
                && stockPots().isPresent() && !stockPots().isBloque();
    }

    @Override
    public int order() {
        int nbDeposesPlantes = 0;
        if (jardiniereMilieuAction != null) {
            if (!rs.bras().avantLibre()) {
                nbDeposesPlantes += 1;
            }
            if (!rs.bras().arriereLibre()) {
                nbDeposesPlantes += 1;
            }
            if (!rs.stockLibre()) {
                nbDeposesPlantes += 1;
            }
        }
        return 15 * Math.min(2, nbDeposesPlantes);
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(315), stockPots().getY());
    }

    private void s() {
        ThreadUtils.sleep(200);
    }

    @Override
    public void execute() {

        try {
            final Point pointApproche = new Point(getX(450), 1775);
            final Point entry = entryPoint();
            StockPots stockPots = stockPots();
            log.info("Prise stock pots {}", stockPots.getId());

            if (actionPoussePlante != null && actionPoussePlante.isValid() && !actionPoussePlante.isCompleted()) {
                actionPoussePlante.execute(pointApproche);
                mv.setVitessePercent(100, 100);
                mv.gotoPoint(entry);
            } else if (rs.getElapsedTime() > 30000) {
                mv.setVitessePercent(100, 100);
                mv.pathTo(pointApproche);
                mv.gotoPoint(entry);
            } else {
                mv.setVitessePercent(100, 100);
                mv.pathTo(entry);
            }

            rs.disableAvoidance();

            mv.setVitessePercent(50, 100);

            mv.gotoOrientationDeg(stockPots.getEntryAngle());
            bras.setBrasArriere(new PointBras(195, 80, -90, null));

            // prise pot gauche
            mv.tourneDeg(12);
            bras.setBras(Bras.ARRIERE_GAUCHE, PointBras.withY(PRISE_POT_SOL_Y), 30, true);
            servos.pinceArriereGauchePrisePotInterieur(true);
            s();
            bras.setBras(Bras.ARRIERE_GAUCHE, PointBras.withY(SORTIE_POT_POT_Y), 30, true);
            // prise pot droite
            mv.tourneDeg(-24);
            bras.setBras(Bras.ARRIERE_DROIT, PointBras.withY(PRISE_POT_SOL_Y), 30, true);
            servos.pinceArriereDroitPrisePotInterieur(true);
            s();
            bras.setBrasArriere(PointBras.withY(SORTIE_POT_POT_Y));
            // prise pot centre
            mv.gotoOrientationDeg(stockPots.getEntryAngle());
            mv.reculeMM(85);
            bras.setBras(Bras.ARRIERE_CENTRE, PointBras.withY(PRISE_POT_POT_Y), 30, true);
            servos.pinceArriereCentrePrisePotInterieur(true);
            s();
            bras.setBras(Bras.ARRIERE_CENTRE, PointBras.withY(SORTIE_POT_POT_Y), 30, true);

            // callage sur inductifs
            mv.setVitessePercent(20, 100);
            rs.enableCalageBordure(TypeCalage.PRISE_ELECTROAIMANT, TypeCalage.FORCE);
            mv.reculeMM(getX((int) mv.currentXMm()) - 130);

            if (!rs.calageCompleted().contains(TypeCalage.PRISE_ELECTROAIMANT)) {
                stockPots.bloque();
                servos.groupePinceArriereFerme(false);
                mv.avanceMM(150);
                bras.setBrasArriere(PositionBras.INIT);
                complete();
                return;
            }

            // lache rangée 1
            bras.setBrasArriere(new PointBras(230, 80, -90, null));
            bras.setBrasArriere(new PointBras(210, 70, -95, null));
            servos.groupePinceArriereFerme(true);
            bras.setBrasArriere(new PointBras(195, 90, -90, null));

            rs.jardiniereMilieu().add(new Plante[]{
                    new Plante(TypePlante.AUCUNE, true),
                    new Plante(TypePlante.AUCUNE, true),
                    new Plante(TypePlante.AUCUNE, true)
            });

            // prise rangée 2
            mv.avanceMM(100);
            bras.setBrasArriere(PointBras.withY(PRISE_POT_SOL_Y));
            servos.groupePinceArrierePrisePotInterieur(true);
            s();
            bras.setBrasArriere(PointBras.withY(130));

            // callage bordure
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.CONTACT_INDUCTIF, TypeCalage.FORCE);
            mv.reculeMM(getX((int) mv.currentXMm()) - config.distanceCalageArriere() - 10);

            if (rs.calageCompleted().contains(TypeCalage.CONTACT_INDUCTIF)) {
                // il y a des pots devant
                io.enableElectroAimant();
                ThreadUtils.sleep(200);
                mv.avanceMM(50);
                mv.gotoOrientationDeg(-90);
                mv.reculeMM(50);
                io.disableElectroAimant();
                ThreadUtils.sleep(200);
                mv.avanceMM(50);
                mv.gotoOrientationDeg(stockPots.getEntryAngle());

                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMM(getX((int) mv.currentXMm()) - config.distanceCalageArriere() - 10);
            }

            if (rs.calageCompleted().contains(TypeCalage.FORCE)) {
                // il y a des plantes devant ?
                stockPots.bloque();
                servos.groupePinceArriereFerme(false);
                mv.avanceMM(150);
                bras.setBrasArriere(PositionBras.INIT);
                complete();
                return;
            }

            rs.enableCalageBordure(TypeCalage.ARRIERE);
            mv.setVitessePercent(0, 100);
            mv.reculeMMSansAngle(40);
            checkRecalageXmm(getX((int) config.distanceCalageArriere()));
            checkRecalageAngleDeg(stockPots.getEntryAngle());

            mv.setVitessePercent(50, 100);

            // lache rangée 2
            bras.setBrasArriere(new PointBras(245, 120, -85, null));
            bras.setBrasArriere(new PointBras(238, 96, -90, null));
            servos.groupePinceArriereFerme(true);
            bras.setBrasArriere(PointBras.withY(120));

            // fin
            mv.avanceMM(150);
            bras.setBrasArriere(PositionBras.INIT);

            stockPots.pris();
            rs.jardiniereMilieu().add(new Plante[]{
                    new Plante(TypePlante.AUCUNE, true),
                    new Plante(TypePlante.AUCUNE, true),
                    new Plante(TypePlante.AUCUNE, true)
            });
            complete(true);

            if (jardiniereMilieuAction != null && (!rs.bras().avantLibre() || !rs.bras().arriereLibre() || !rs.stockLibre())) {
                jardiniereMilieuAction.execute();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
