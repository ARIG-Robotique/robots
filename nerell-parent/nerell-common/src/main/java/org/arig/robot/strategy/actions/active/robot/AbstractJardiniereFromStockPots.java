package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.BrasListe;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.ZoneDepose;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import static org.arig.robot.services.BrasInstance.*;

@Slf4j
@Component
public abstract class AbstractJardiniereFromStockPots extends AbstractNerellAction {

    @Override
    public Point entryPoint() {
        return rs.positionPotsZoneDepart();
    }

    @Override
    public boolean isValid() {
        return isTimeValid()
                && !ilEstTempsDeRentrer()
                //&& rs.potsInZoneDepart() > 0
                && !rs.bras().avantLibre();
    }

    private void s() {
        ThreadUtils.sleep(500);
    }

    protected void gotoAndTake() throws AvoidingException, NoPathFoundException {
        final Point entry = entryPoint();

        mv.setVitessePercent(100, 100);
        mv.pathTo(entry);

        mv.gotoOrientationDeg(rs.team() == Team.BLEU ? 180 : 0);

        rs.disableAvoidance();

        // pose des plantes dans les pots
        bras.setBrasAvant(new PointBras(215, SORTIE_POT_POT_Y, -90, null));
        mv.setVitessePercent(20, 100);
        mv.setRampesDistancePercent(100, 30);
        mv.gotoOrientationDeg(90);
        if (rs.potsInZoneDepart() == 2) {
            mv.avanceMM(230);
            mv.reculeMM(120);
        } else {
            mv.avanceMM(250);
            mv.reculeMM(120);
        }
        servos.groupePinceAvantOuvert(true);
        s();
        // prise des pots
        // FIXME reactiver la prise d'un seul pot
//        if (rs.potsInZoneDepart() == 2) {
//            bras.setBrasAvant(PointBras.withY(PRISE_POT_POT_Y));
//        } else {
        bras.setBrasAvant(PointBras.withY(PRISE_POT_SOL_Y));
        // }
        s();
        servos.groupePinceAvantPrisePot(true);
        bras.setBrasAvant(PointBras.translated(0, -4));
        s();
        bras.setBrasAvant(PointBras.withY(SORTIE_POT_POT_Y));
        s();

        if (rs.potsInZoneDepart() == 2) {
            mv.reculeMM(100);
        }

        BrasListe.Contenu[] contenuBras = rs.bras().getAvant();
        rs.bras().setAvant(
                contenuBras[0].withPot(),
                contenuBras[1].withPot(),
                contenuBras[2].withPot()
        );

        // FIXME
        //rs.potsInZoneDepart(rs.potsInZoneDepart() - 1);
        rs.potsInZoneDepart(0);
    }

    protected void depose(ZoneDepose jardiniere, boolean fast) throws AvoidingException {
        // FIXME check capteurs pinces

        rs.disableAvoidance();

        // depose jardinière
        if (!fast) {
            bras.setBrasAvant(new PointBras(190, PRISE_POT_POT_Y, -90, null));
        }
        servos.groupePinceAvantOuvert(true);
        s();

        jardiniere.setFromBras(rs.bras().getAvant());
        rs.bras().setAvant(null, null, null);

        // fin
        bras.setBrasAvant(new PointBras(215, SORTIE_POT_POT_Y, -90, null));
        servos.groupePinceAvantFerme(false);
        mv.setVitessePercent(100, 100);
        mv.reculeMM(150);
        runAsync(() -> bras.setBrasAvant(PositionBras.INIT));

        complete(true);
    }
}
