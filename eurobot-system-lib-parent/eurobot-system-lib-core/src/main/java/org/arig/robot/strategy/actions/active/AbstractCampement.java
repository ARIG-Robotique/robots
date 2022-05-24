package org.arig.robot.strategy.actions.active;

import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Campement;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Team;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractCampement extends AbstractEurobotAction {

    @Autowired
    protected BrasService bras;

    protected Campement.Position position = null;

    protected void deposePile(
            Supplier<Boolean> isValid,
            Consumer<CouleurEchantillon> onDepose,
            Supplier<Integer> taillePile
    ) throws AvoidingException {

        boolean isNord = position == Campement.Position.NORD;

        CompletableFuture<Void> task = runAsync(() -> {
            try {
                mv.gotoOrientationDeg(isNord && rs.team() == Team.JAUNE ? -150 :
                        !isNord && rs.team() == Team.JAUNE ? 150 :
                                isNord && rs.team() == Team.VIOLET ? -30 : 30);
            } catch (AvoidingException e) {
                throw new CompletionException(e);
            }
        });

        // si l'autre est à coté on doit attendre la rotation
        if (rs.otherCampement() != null) {
            task.join();
        }

        bras.setBrasHaut(PositionBras.HORIZONTAL);

        task.join();

        while (rs.stockTaille() > 0 && isValid.get() && taillePile.get() < Campement.MAX_DEPOSE) {
            if (bras.destockageBas()) {
                bras.setBrasBas(PositionBras.solDepose(taillePile.get()));
                CouleurEchantillon couleur = rs.ventouseBas();
                bras.waitReleaseVentouseBas();
                onDepose.accept(couleur);
            }
        }

        runAsync(() -> bras.repos());
    }

}
