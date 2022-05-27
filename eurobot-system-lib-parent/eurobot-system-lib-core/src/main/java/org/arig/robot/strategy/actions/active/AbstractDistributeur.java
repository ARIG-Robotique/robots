package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractDistributeur extends AbstractEurobotAction {

    @Autowired
    protected BrasService bras;

    protected CompletableFuture<Void> prepare() {
        return runAsync(() -> {
            io.enableLedCapteurCouleur();
            bras.setBrasHaut(PositionBras.HORIZONTAL);
            bras.setBrasBas(PositionBras.DISTRIBUTEUR_PRISE_1);
        });
    }

    protected CompletableFuture<Void> prise() throws AvoidingException {
        mv.setVitesse(config.vitesse(10), config.vitesseOrientation());

        for (int i = 0; i < 3; i++) {
            CouleurEchantillon couleur = i == 0 ? CouleurEchantillon.ROCHER_BLEU :
                    i == 1 ? CouleurEchantillon.ROCHER_VERT :
                            CouleurEchantillon.ROCHER_ROUGE;

            io.enablePompeVentouseBas();
            rs.enableCalageBordure(TypeCalage.VENTOUSE_BAS, TypeCalage.FORCE);
            mv.avanceMM(25);

            if (bras.waitEnableVentouseBas(couleur)) {
                if (i == 2) {
                    mv.reculeMM(30);
                }

                if (EurobotConfig.ECHANGE_PRISE) {
                    if (bras.echangeBasHaut()) {
                        bras.setBrasBas(i < 2 ? PositionBras.distribPrise(i + 1) : PositionBras.HORIZONTAL);
                        bras.stockageHaut();
                        bras.setBrasHaut(PositionBras.HORIZONTAL);

                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(i < 2 ? PositionBras.distribPrise(i + 1) : PositionBras.STOCK_ENTREE);
                    }

                } else {
                    bras.stockageBas();
                    if (i < 2) {
                        bras.setBrasBas(PositionBras.distribPrise(i + 1));
                    }
                }
            } else {
                log.warn("Echec de prise dans {}", name());
                if (i == 2) {
                    io.releasePompeVentouseBas();
                }
            }

            if (!timeBeforeRetourValid()) {
                break;
            }
        }

        return runAsync(() -> bras.repos());
    }

    protected boolean priseAuSolEtEjecte(CouleurEchantillon couleur, double angle) throws AvoidingException {
        log.info("On évacue la mine");

        bras.setBrasHaut(PositionBras.HORIZONTAL);
        bras.setBrasBas(PositionBras.STOCK_ENTREE);
        bras.setBrasBas(PositionBras.SOL_PRISE);

        mv.setVitesse(config.vitesse(50), config.vitesseOrientation());

        boolean ok = bras.waitEnableVentouseBas(couleur);
        if (ok) {
            mv.reculeMM(10);
            bras.setBrasBas(PositionBras.SOL_DEPOSE_5);
            mv.gotoOrientationDeg(angle);
            bras.waitReleaseVentouseBas();
        }

        bras.repos();

        return ok;
    }
}
