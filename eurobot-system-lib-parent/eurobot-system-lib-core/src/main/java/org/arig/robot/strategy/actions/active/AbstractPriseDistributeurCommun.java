package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;

@Slf4j
public abstract class AbstractPriseDistributeurCommun extends AbstractEurobotAction {

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

    @Autowired
    private BrasService bras;

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
        return isTimeValid() && remainingTimeBeforeRetourSiteValid()
                && !isDistributeurPris() && !isDistributeurBloque() && rs.stockDisponible() >= 3
                && rs.getRemainingTime() >= EurobotConfig.invalidPriseEchantillonRemainingTime;
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

            CompletableFuture<?> task = bras.initPrise(BrasService.TypePrise.DISTRIBUTEUR);

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation(50));
            mv.reculeMM(80);
            mv.gotoOrientationDeg(anglePrise());

            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());

            task.get();

            for (int i = 0; i < 3; i++) {
                io.enablePompeVentouseBas();
                rs.enableCalageBordure(TypeCalage.VENTOUSE_BAS, TypeCalage.FORCE);
                mv.avanceMM(25);

                if (bras.processPrise(BrasService.TypePrise.DISTRIBUTEUR).get()) {
                    if (i == 2) {
                        mv.reculeMM(30);
                    }
                    CouleurEchantillon couleur = i == 0 ? CouleurEchantillon.ROCHER_BLEU :
                            i == 1 ? CouleurEchantillon.ROCHER_VERT :
                                    CouleurEchantillon.ROCHER_ROUGE;
                    bras.stockagePrise(BrasService.TypePrise.DISTRIBUTEUR, couleur, false).get();
                }
            }

            task = bras.finalizePrise();

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.reculeMM(30);
            mv.gotoPoint(entry, GotoOption.ARRIERE);

            task.get();

            setDistributeurPris();

        } catch (NoPathFoundException | AvoidingException | ExecutionException | InterruptedException e) {
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

        } finally {
            bras.safeHoming();
            refreshCompleted();
        }
    }
}
