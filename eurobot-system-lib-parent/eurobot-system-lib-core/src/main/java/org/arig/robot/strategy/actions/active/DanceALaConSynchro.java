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
@Component
public class DanceALaConSynchro extends AbstractEurobotAction {

    @Autowired
    private BrasService bras;

    @Autowired
    private CarreFouilleReader carreFouilleReader;

    private boolean alternate = true;
    private boolean firstRun = true;

    @Override
    public String name() {
        return "Dance à la con synchro";
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

            if (firstRun) {
                firstRun = false;
                // 0-8s
                // 1 mouvement toute les deux secondes (4 mouvement)
                // 1 coup a droite, 1 coup a gauche
                mv.tourneDeg(40);
                while(rs.getElapsedTime() < 2000) {
                    ThreadUtils.sleep(1);
                }
                mv.tourneDeg(-80);
                while(rs.getElapsedTime() < 4000) {
                    ThreadUtils.sleep(1);
                }
                mv.tourneDeg(80);
                while(rs.getElapsedTime() < 6000) {
                    ThreadUtils.sleep(1);
                }
                mv.tourneDeg(-40);
                // delay 650
                while (rs.getElapsedTime() < 8650) {
                    ThreadUtils.sleep(1);
                }

                // 8,650 : 17 mouvements de 460ms
                alternate = true;
                while (rs.getElapsedTime() < 16470) {
                    if (alternate) {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                    } else {
                        bras.setBrasBas(PositionBras.ECHANGE);
                        bras.setBrasHaut(PositionBras.ECHANGE);
                    }
                    alternate = !alternate;
                }

                runAsync(() -> {
                    bras.setBrasHaut(PositionBras.HORIZONTAL);
                    bras.setBrasBas(PositionBras.INIT);
                    bras.setBrasHaut(PositionBras.INIT);
                });

                // Mouvement de 4 seconde dans un sens, puis 4s dans l'autre sens.

                while (rs.getElapsedTime() < 16470 + 4000) {
                    mv.tourneDeg(10);
                }
                while (rs.getElapsedTime() < 16470 + 8000) {
                    mv.tourneDeg(-10);
                }

                // Free Style
                mv.gotoOrientationDeg(0);
            }

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
                mv.avanceMM(300);
                mv.tourneDeg(10 * 360);
            } else {
                mv.reculeMM(300);
                mv.tourneDeg(-10 * 360);
            }
            alternate = !alternate;
        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
