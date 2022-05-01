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

import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;

@Slf4j
public abstract class AbstractPriseDistributeurCommun extends AbstractEurobotAction {

    private static final int DISTRIB_H = 102;
    private static final int TASSEAU_W = 11;

    protected static final int ENTRY_X = 1295;
    protected static final int ENTRY_Y = 1705;

    protected abstract boolean isDistributeurPris();

    protected abstract void setDistributeurPris();

    protected abstract int angleCallageX();

    protected abstract int anglePrise();

    @Autowired
    private BrasService brasService;

    @Override
    public int order() {
        int points = 3 * PTS_DEPOSE_PRISE;
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (isDistributeurPris()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        return !isDistributeurPris() && rs.stockDisponible() >= 3
                && rs.getRemainingTime() >= EurobotConfig.validPriseEchantillonRemainingTime
                && isTimeValid() && remainingTimeValid();
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            rs.disableAvoidance();

            // Calage sur X
            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.gotoOrientationDeg(angleCallageX());
            rs.enableCalageBordure(TypeCalage.AVANT);
            mv.avanceMM(1500 - ENTRY_X - TASSEAU_W - robotConfig.distanceCalageAvant() - 10);
            mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.AVANT);
            mv.avanceMM(100);

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.reculeMM(60);

            // Calage sur Y
            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.gotoOrientationDeg(90);
            rs.enableCalageBordure(TypeCalage.AVANT);
            mv.avanceMM(2000 - ENTRY_Y - DISTRIB_H - robotConfig.distanceCalageAvant() - 10);
            mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.AVANT);
            mv.avanceMM(100);

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation(50));
            mv.reculeMM(80);
            mv.gotoOrientationDeg(anglePrise());

            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
            brasService.initPrise(BrasService.TypePrise.DISTRIBUTEUR);

            for (int i = 0; i < 3; i++) {
                mv.avanceMM(20);

                if (brasService.processPrise(BrasService.TypePrise.DISTRIBUTEUR)) {
                    if (i == 2) {
                        mv.reculeMM(30);
                    }
                    CouleurEchantillon couleur = i == 0 ? CouleurEchantillon.ROCHER_BLEU :
                            i == 1 ? CouleurEchantillon.ROCHER_VERT :
                                    CouleurEchantillon.ROCHER_ROUGE;
                    brasService.stockagePrise(BrasService.TypePrise.DISTRIBUTEUR, couleur);
                }
            }

            brasService.finalizePrise();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.reculeMM(30);
            mv.gotoPoint(entry, GotoOption.ARRIERE);

            setDistributeurPris();

        } catch (NoPathFoundException | AvoidingException e) {
            if (e instanceof MovementCancelledException) {
                final double robotX = conv.pulseToMm(position.getPt().getX());
                final double robotY = conv.pulseToMm(position.getPt().getY());

                if (robotY >= 1650 && robotX >= 1230 && robotX <= 3000 - 1230) {
                    log.warn("Blocage détecté à proximité de {}", name());
                    setDistributeurPris(); // on désactive l'action
                    return;
                }
            }

            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            refreshCompleted();
        }
    }
}
