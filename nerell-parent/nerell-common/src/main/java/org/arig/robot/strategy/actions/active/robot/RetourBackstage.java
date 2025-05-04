package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.BackstageState;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourBackstage extends AbstractNerellAction {

    private static final int FINAL_X = 350;
    private static final int FINAL_Y = 1775;
    private static final int ENTRY_X = FINAL_X;
    private static final int ENTRY_Y = 1400;

    @Override
    public String name() {
        return EurobotConfig.ACTION_RETOUR_BACKSTAGE;
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public int order() {
        return 10 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return ilEstTempsDeRentrer();
    }

    @Override
    public void execute() {
        mv.setVitessePercent(100, 100);

        try {
            log.info("Go backstage");
            groups.forEach(g -> g.backstage(BackstageState.IN_MOVE));

            mv.pathTo(entryPoint(), GotoOption.SANS_ARRET_PASSAGE_ONLY_PATH);
            groups.forEach(g -> g.backstage(BackstageState.TARGET_REACHED));
            complete(true);
            rs.disableAvoidance();

            // TODO : Attente sortie des PAMIs, ou déploiement tiroir.

            log.info("Arrivée au backstage, on rentre bien dedans");
            mv.setVitessePercent(50, 100);
            mv.gotoPoint(getX(FINAL_X), FINAL_Y);
            ThreadUtils.sleep((int) rs.getRemainingTime());

        } catch (NoPathFoundException | AvoidingException e) {
            log.warn("Impossible d'aller au backstage : {}", e.toString());
        }

        if (!isCompleted()) {
            log.error("Erreur d'exécution de l'action");
            updateValidTime();
            groups.forEach(g -> g.backstage(BackstageState.OUTSIDE));
        }
    }
}
