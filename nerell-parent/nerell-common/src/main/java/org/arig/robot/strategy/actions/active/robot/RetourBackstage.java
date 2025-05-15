package org.arig.robot.strategy.actions.active.robot;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.BackstageState;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.Team;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetourBackstage extends AbstractNerellAction {

    private static final int FINAL_X_GRADIN = 350;
    private static final int FINAL_X_FREE = 190;
    private static final int ENTRY_Y = 1400;
    private final Position position;

    public RetourBackstage(Position position) {
        super();
        this.position = position;
    }

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
        GradinBrut.ID gradinId = rs.team() == Team.JAUNE ? GradinBrut.ID.JAUNE_HAUT_GAUCHE : GradinBrut.ID.BLEU_HAUT_DROITE;
        final int entryX = rs.gradinBrutStocks().get(gradinId).present() ? FINAL_X_GRADIN : FINAL_X_FREE;
        return new Point(getX(entryX), ENTRY_Y);
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

            mv.pathTo(entryPoint());
            if (position.getAngle() > 0) {
                // Face Avant
                mv.gotoOrientationDeg(90);
                servosNerell.tiroirAvantDepose(false);
            } else {
                // Face Arrière
                mv.gotoOrientationDeg(-90);
                servosNerell.tiroirArriereDepose(false);
            }
            log.info("Arrivée au backstage");
            groups.forEach(g -> g.backstage(BackstageState.TARGET_REACHED));
            complete(true);
            rs.disableAvoidance();

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
