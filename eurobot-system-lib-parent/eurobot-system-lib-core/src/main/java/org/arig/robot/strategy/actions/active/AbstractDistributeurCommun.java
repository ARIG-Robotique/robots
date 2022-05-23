package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;

import java.util.concurrent.CompletableFuture;

import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;

@Slf4j
public abstract class AbstractDistributeurCommun extends AbstractDistributeur {

    private static final int DISTRIB_H = 102;
    private static final int TASSEAU_W = 11;

    protected static final int ENTRY_X = 1295;
    protected static final int ENTRY_Y = 1705;

    protected abstract boolean isDistributeurPris();

    protected abstract boolean isDistributeurBloque();

    protected abstract void setDistributeurPris();

    protected abstract void setDistributeurBloque();

    protected abstract int angleCallageX();

    protected abstract int anglePrise();

    @Override
    public int executionTimeMs() {
        int executionTime = 4000; // Calage
        executionTime += 2000 * 3; // 2 sec par échantillon

        return executionTime;
    }

    @Override
    public int order() {
        int points = 3 * PTS_DEPOSE_PRISE;
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (isDistributeurPris() || isDistributeurBloque()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid()
                && !isDistributeurPris() && !isDistributeurBloque() && rs.stockDisponible() >= 3;
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            rs.disableAvoidance();

            // Calage sur X
            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.gotoOrientationDeg(angleCallageX());
            rs.enableCalageBordure(TypeCalage.AVANT_BAS);
            mv.avanceMM(1500 - ENTRY_X - TASSEAU_W - config.distanceCalageAvant() - 10);
            mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.AVANT_BAS);
            mv.avanceMM(100);

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.reculeMM(55);

            // Calage sur Y
            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.gotoOrientationDeg(90);
            rs.enableCalageBordure(TypeCalage.AVANT_BAS);
            mv.avanceMM(2000 - ENTRY_Y - DISTRIB_H - config.distanceCalageAvant() - 10);
            mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.AVANT_BAS);
            mv.avanceMM(100);

            CompletableFuture<?> task = prepare();

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation(50));
            mv.reculeMM(80);
            mv.gotoOrientationDeg(anglePrise());

            task.join();
            prise();

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.reculeMM(30);
            mv.gotoPoint(entry, GotoOption.ARRIERE);

            setDistributeurPris();
            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            if (e instanceof MovementCancelledException) {
                final double robotX = mv.currentXMm();
                final double robotY = mv.currentYMm();

                if (robotY >= 1650 && robotX >= 1230 && robotX <= 3000 - 1230) {
                    log.warn("Blocage détecté à proximité de {}", name());
                    // FIXME Tenter de récupérer l'échantillon et le déposer derrière nous
                    setDistributeurBloque(); // on désactive l'action
                    return;
                }
            }

            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();
        }
    }
}
