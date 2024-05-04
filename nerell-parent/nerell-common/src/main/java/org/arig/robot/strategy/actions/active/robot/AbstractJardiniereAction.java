package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Jardiniere;
import org.arig.robot.model.Plante;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import static org.arig.robot.services.BrasInstance.PRISE_POT_POT_Y;
import static org.arig.robot.services.BrasInstance.SORTIE_POT_POT_Y;

@Slf4j
@Component
public abstract class AbstractJardiniereAction extends AbstractNerellAction {

    @Override
    public boolean isValid() {
        return isTimeValid()
                && !ilEstTempsDeRentrer()
                && !jardiniere().rang2()
                && (!rs.bras().avantLibre() || !rs.stockLibre());
    }

    abstract protected Jardiniere jardiniere();

    @Override
    public int order() {
        Jardiniere next = jardiniere().clone();

        if (!jardiniere().rang1()) {
            if (!rs.bras().avantLibre()) {
                next.add(rs.bras().getAvant());
                if (!rs.stockLibre()) {
                    next.add(rs.stock());
                }
            } else {
                next.add(rs.stock());
            }
        } else {
            if (!rs.bras().avantLibre()) {
                next.add(rs.bras().getAvant());
            } else {
                next.add(rs.stock());
            }
        }

        return next.score() - jardiniere().score();
    }

    protected void prepareBras() {
        if (rs.bras().avantLibre()) {
            bras.brasAvantDestockage();
            Plante[] stock = rs.stock();
            rs.bras().setAvant(stock[0], stock[1], stock[2]);
            rs.setStock(null, null, null);
        }

        if (!jardiniere().rang1()) {
            bras.setBrasAvant(new PointBras(225, 155, -90, null));
        } else {
            bras.setBrasAvant(new PointBras(240, 175, -90, null));
        }
    }

    protected void depose(boolean callageForce) throws AvoidingException {
        // FIXME check capteurs pinces

        rs.disableAvoidance();

        if (callageForce) {
            servos.groupePinceAvantOuvert(true);
            ThreadUtils.sleep(200);

            jardiniere().rang1(true);
            jardiniere().rang2(true);

        } else if (!jardiniere().rang1()) {
            bras.setBrasAvant(new PointBras(190, PRISE_POT_POT_Y, -90, null));
            ThreadUtils.sleep(200);
            servos.groupePinceAvantOuvert(true);
            ThreadUtils.sleep(200);

            jardiniere().rang1(true);
        } else {
            bras.setBrasAvant(new PointBras(235, 100, -90, null));
            ThreadUtils.sleep(200);
            servos.groupePinceAvantOuvert(true);
            ThreadUtils.sleep(200);

            jardiniere().rang2(true);
        }

        jardiniere().add(rs.bras().getAvant());
        rs.bras().setAvant(null, null, null);

        // fin
        bras.setBrasAvant(new PointBras(215, SORTIE_POT_POT_Y, -90, null));
        servos.groupePinceAvantFerme(false);

        mv.setVitessePercent(100, 100);
        rs.enableAvoidance();
        mv.reculeMM(150);

        if (jardiniere().rang2()) {
            complete(true);
        }
    }
}
