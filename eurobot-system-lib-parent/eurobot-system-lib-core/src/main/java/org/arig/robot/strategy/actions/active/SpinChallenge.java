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
public class SpinChallenge extends AbstractEurobotAction {

    @Autowired
    private BrasService bras;

    @Autowired
    private CarreFouilleReader carreFouilleReader;

    private boolean alternate = true;
    private boolean firstRun = true;

    @Override
    public String name() {
        return "Spin Challenge";
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

            mv.tourneDeg(-20 * 360);
            disableLigthTask.set(true);
            ligths.join();
            complete();
        } catch (AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
