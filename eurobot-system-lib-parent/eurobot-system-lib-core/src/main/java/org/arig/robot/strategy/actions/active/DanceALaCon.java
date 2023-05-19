package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
//@Component
public class DanceALaCon extends AbstractEurobotAction {

    @Autowired
    private BrasService bras;

    @Autowired
    private CarreFouilleReader carreFouilleReader;

    private boolean alternate = false;

    @Override
    public String name() {
        return "Dance à la con";
    }

    @Override
    public int executionTimeMs() {
        return 0;
    }

    @Override
    public Point entryPoint() {
        return null;
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());

            CompletableFuture<Void> task = null;
            CompletableFuture<Void> ligths = null;

            AtomicBoolean disableLigthTask = new AtomicBoolean(false);
            ligths = runAsync(() -> {
                CouleurEchantillon couleur1 = CouleurEchantillon.BLEU;
                CouleurEchantillon couleur2 = CouleurEchantillon.ROUGE;
                while(!disableLigthTask.get()) {
                    try {
                        carreFouilleReader.printStateStock(couleur1, couleur2, couleur1, couleur2, couleur1, couleur2);
                        carreFouilleReader.printStateVentouse(couleur1, couleur2);
                    } catch (I2CException e) {}
                    ThreadUtils.sleep(500);

                    couleur1 = CouleurEchantillon.ROUGE;
                    couleur2 = CouleurEchantillon.BLEU;
                }
            });

            mv.tourneDeg(30);

            task = runAsync(() -> {
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.HORIZONTAL);
            });

            mv.tourneDeg(-60);
            task.join();

            task = runAsync(() -> {
                bras.setBrasHaut(PositionBras.ECHANGE);
                bras.setBrasBas(PositionBras.ECHANGE);
            });

            mv.tourneDeg(30 + 90);
            task.join();

            task = runAsync(() -> {
                bras.setBrasHaut(PositionBras.HORIZONTAL);
                bras.setBrasBas(PositionBras.INIT);
                bras.setBrasHaut(PositionBras.INIT);
            });

            servos.carreFouilleOhmmetreMesure(true);
            servos.carreFouillePoussoirPoussette(true);
            ThreadUtils.sleep(500);
            task.join();

            task = runAsync(() -> {
                servos.carreFouillePoussoirFerme(true);
                servos.carreFouilleOhmmetreFerme(false);
            });

            mv.tourneDeg(-90);
            task.join();

            disableLigthTask.set(true);
            ligths.join();

            if (alternate) {
                mv.avanceMM(400);
                mv.tourneDeg(10 * 360);
            } else {
                mv.reculeMM(400);
                mv.tourneDeg(-10 * 360);
            }
            alternate = !alternate;


        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
