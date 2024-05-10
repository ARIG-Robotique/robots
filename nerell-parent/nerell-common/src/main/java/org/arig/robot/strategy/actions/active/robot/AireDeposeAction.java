package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.AireDepose;
import org.arig.robot.model.Plante;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AireDeposeAction extends AbstractNerellAction {

    @Override
    public String name() {
        return "Aide de dépose";
    }

    private AireDepose getAire() {
        if (rs.bras().avantLibre() && rs.stockLibre()) {
            return null;
        }

        if (ilEstTempsDeRentrer()) {
            // si il est temps de rentrer, on privilégie l'aire qui va bien, même si un seul rang
            if (rs.aireDeDeposeNord().siteDeCharge(rs.team()) != rs.siteDeDepart() && !rs.aireDeDeposeNord().rang2()) {
                return rs.aireDeDeposeNord();
            }
            if (rs.aireDeDeposeSud().siteDeCharge(rs.team()) != rs.siteDeDepart() && !rs.aireDeDeposeSud().rang2()) {
                return rs.aireDeDeposeSud();
            }
            if (rs.aireDeDeposeMilieu().siteDeCharge(rs.team()) != rs.siteDeDepart() && !rs.aireDeDeposeMilieu().rang2()) {
                return rs.aireDeDeposeMilieu();
            }

            return null;
        }

        if (!rs.bras().avantLibre() && !rs.stockLibre()) {
            // on veut faire deux déposes
            if (!rs.aireDeDeposeNord().rang1()) {
                return rs.aireDeDeposeNord();
            }
            if (!rs.aireDeDeposeSud().rang1()) {
                return rs.aireDeDeposeSud();
            }
            if (!rs.aireDeDeposeMilieu().rang1()) {
                return rs.aireDeDeposeMilieu();
            }
        }

        // on veut faire une dépose, ou plus assez de place nulle part
        if (!rs.aireDeDeposeNord().rang2()) {
            return rs.aireDeDeposeNord();
        }
        if (!rs.aireDeDeposeSud().rang2()) {
            return rs.aireDeDeposeSud();
        }
        if (!rs.aireDeDeposeMilieu().rang2()) {
            return rs.aireDeDeposeMilieu();
        }
        return null;
    }

    @Override
    public boolean isValid() {
        AireDepose aire = getAire();

        return isTimeValid()
                && aire != null;
    }

    @Override
    public int executionTimeMs() {
        return 0; // TODO
    }

    @Override
    public int order() {
        AireDepose zone = getAire();

        if (zone == null) {
            return 0;
        }

        AireDepose next = zone.clone();

        if (!rs.bras().avantLibre()) {
            next.add(rs.bras().getAvant());

            if (!rs.stockLibre() && !zone.rang2()) {
                next.add(rs.stock());
            }
        } else {
            next.add(rs.stock());
        }

        int bonusRetour = 0;
        if (ilEstTempsDeRentrer() && zone.siteDeCharge(rs.team()) != rs.siteDeDepart()) {
            bonusRetour = 10;
        }

        return next.score() - zone.score() + bonusRetour + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        AireDepose aire = getAire();

        int x;
        if (!aire.rang1()) {
            x = 250;
        } else {
            x = 400;
        }

        if (aire == rs.aireDeDeposeNord()) {
            return new Point(getX(x), 1680);
        }
        if (aire == rs.aireDeDeposeMilieu()) {
            return new Point(tableUtils.getX(rs.team() == Team.BLEU, x), 900);
        }

        // sud
        return new Point(getX(x), 300);
    }

    private int getAngle(AireDepose aire) {
        if (aire == rs.aireDeDeposeMilieu()) {
            return rs.team() == Team.BLEU ? 0 : 180;
        } else {
            return rs.team() == Team.BLEU ? 180 : 0;
        }
    }

    private void destockage() {
        bras.brasAvantDestockage();

        Plante[] stock = rs.stock();
        rs.bras().setAvant(stock[0], stock[1], stock[2]);
        rs.setStock(null, null, null);
    }

    private void depose(AireDepose aire) throws AvoidingException {
        bras.setBrasAvant(new PointBras(195, 60, -90, null));
        ThreadUtils.sleep(200);
        servos.groupePinceAvantOuvert(true);
        ThreadUtils.sleep(300);
        bras.setBrasAvant(PointBras.withY(130));
        mv.reculeMM(150);

        aire.add(rs.bras().getAvant());
        rs.bras().setAvant(null, null, null);
        if (!aire.rang1()) {
            aire.rang1(true);
        } else {
            aire.rang2(true);
        }
    }

    @Override
    public void execute() {
        final Point entry = entryPoint();
        final AireDepose aire = getAire();

        try {
            mv.setVitessePercent(100, 100);
            mv.pathTo(entry);
            mv.gotoOrientationDeg(getAngle(aire));

            boolean fromStock = false;
            if (rs.bras().avantLibre()) {
                destockage();
                fromStock = true;
            } else {
                //bras.refreshPincesAvant().join();
            }

            depose(aire);

            if (!aire.rang2() && !rs.stockLibre()) {
                destockage();

                depose(aire);
            }

            servos.groupePinceAvantFerme(false);
            runAsync(() -> bras.brasAvantInit());

            // on tasse
            mv.setVitessePercent(30, 100);
            mv.setRampesDistancePercent(100, 20);

            if (aire == rs.aireDeDeposeMilieu()) {
                mv.gotoPoint(tableUtils.getX(rs.team() == Team.BLEU, 200), mv.currentYMm(), GotoOption.SANS_ORIENTATION);
            } else {
                mv.gotoPoint(getX(200), mv.currentYMm(), GotoOption.SANS_ORIENTATION);
            }
            mv.reculeMM(150);

            if (ilEstTempsDeRentrer() && aire.siteDeCharge(rs.team()) != rs.siteDeDepart()) {
                log.info("On est déjà dans un site de charge");
                rs.siteDeCharge(aire.siteDeCharge(rs.team()));
                ThreadUtils.sleep((int) rs.getRemainingTime());
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }

    }
}
