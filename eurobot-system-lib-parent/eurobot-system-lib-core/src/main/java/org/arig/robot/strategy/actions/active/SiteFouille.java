package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class SiteFouille extends AbstractEurobotAction {

    private static final int ENTRY_X_1 = 620;
    private static final int ENTRY_X_2 = 1330;
    private static final int Y_1 = 750;
    private static final int Y_2 = 500;

    @Autowired
    private BrasService bras;

    private boolean isReverse() {
        if (rs.team() == Team.JAUNE) {
            return mv.currentXMm() > 970;
        } else {
            return mv.currentXMm() < 3000 - 970;
        }
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(!isReverse() ? ENTRY_X_1 : ENTRY_X_2), Y_1);
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_SITE_FOUILLE_EQUIPE;
    }

    @Override
    public int order() {
        int stock = rs.stockDisponible();
        return Math.min(stock, 3) * EurobotConfig.PTS_DEPOSE_PRISE + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && remainingTimeBeforeRetourSiteValid()
                && !rs.siteDeFouillePris() && rs.stockDisponible() > 0
                && rs.getRemainingTime() > EurobotConfig.invalidPriseEchantillonRemainingTime;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_DECOUVERTE_CARRE_FOUILLE);
    }

    @Override
    public void refreshCompleted() {
        if (rs.siteDeFouillePris() || rs.strategy() == Strategy.BASIC) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            boolean isReverse = isReverse();

            // point d'entrée
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entryPoint());

            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());

            int prises = doPrise(0, !isReverse ? new Point(getX(ENTRY_X_2), Y_1) : new Point(getX(ENTRY_X_1), Y_1));
            group.siteDeFouillePris();
            if (prises < 3) {
                prises = doPrise(prises, !isReverse ? new Point(getX(ENTRY_X_2), Y_2) : new Point(getX(ENTRY_X_1), Y_2));
            }
            if (prises < 3) {
                doPrise(prises, !isReverse ? new Point(getX(ENTRY_X_1), Y_2) : new Point(getX(ENTRY_X_2), Y_2));
            }

            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            bras.safeHoming();
        }
    }

    private int doPrise(int prises, Point target) throws AvoidingException {
        CompletableFuture<?> task = null;

        while (true) {
            rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON);
            mv.gotoPoint(target, GotoOption.AVANT);

            if (task != null) task.join();

            if (rs.calageCompleted().contains(TypeCalage.PRISE_ECHANTILLON)) {
                mv.avanceMM(20); // pour vraiment etre en contact

                io.enableLedCapteurCouleur();
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.SOL_PRISE);

                if (bras.waitEnableVentouseBas(CouleurEchantillon.INCONNU)) {
                    bras.setBrasBas(PositionBras.SOL_DEPOSE_2);

                    task = runAsync(() -> {
                        bras.stockageBas();
                        bras.repos();
                    });

                    prises++;
                    if (prises == 3) {
                        break;
                    }

                } else {
                    task = runAsync(() -> bras.repos());
                }
            } else {
                break;
            }
        }

        if (task != null) task.join();

        return prises;
    }

}
