package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Galerie;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class DeposeStatuetteActivationVitrine extends AbstractEurobotAction {

    static final int ENTRY_X_DEPOSE_STATUETTE = 225;
    static final int ENTRY_Y_DEPOSE_STATUETTE = 1700;
    private static final int ENTRY_Y_FAR = 1500;

    @Autowired
    private PriseEchantillonCampement priseEchantillonCampement;

    @Autowired
    private BrasService bras;

    @Override
    public String name() {
        return EurobotConfig.ACTION_DEPOSE_STATUETTE;
    }

    @Override
    public int executionTimeMs() {
        return 5000; // Calage + dépose
    }

    @Override
    public int order() {
        int points = 0;
        if (io.presenceStatuette(true)) {
            points += 500; // 15 points pour la dépose de statuette
        }
        if (!rs.vitrineActive()) {
            points += 5; // 5 point de plus si vitrine inactive
        }
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.statuetteDepose() && rs.vitrineActive()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        boolean validStatuette = io.presenceStatuette(true);
        boolean validePeriodeGalerie = rs.periodeGalerieAutreRobot() != Galerie.Periode.BLEU;

        return isTimeValid() && timeBeforeRetourValid() && validStatuette && validePeriodeGalerie;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_ECHANTILLON_DISTRIBUTEUR_CAMPEMENT);
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X_DEPOSE_STATUETTE), ENTRY_Y_DEPOSE_STATUETTE);
    }

    private Point secondaryEntryPoint() {
        return new Point(getX(ENTRY_X_DEPOSE_STATUETTE), ENTRY_Y_FAR);
    }

    @Override
    public void execute() {
        boolean priseEnchantillonCampementFaite = false;
        try {
            Point entry = entryPoint();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());

            boolean isSecondaryEntryPoint = false;
            try {
                mv.pathTo(entry);
            } catch (NoPathFoundException e) {
                if (rs.tailleCampementRougeVertSud() == 0 && rs.tailleCampementRougeVertNord() == 0) {
                    entry = secondaryEntryPoint();
                    mv.pathTo(entry);
                    isSecondaryEntryPoint = true;
                } else {
                    throw e;
                }
            }

            rs.disableAvoidance(); // Zone interdite pour l'adversaire

            CompletableFuture<Void> task = null;
            if (priseEchantillonCampement.isValid()) {
                if (isSecondaryEntryPoint) {
                    mv.gotoPoint(entryPoint());
                }

                task = priseEchantillonCampement.execute(true);
                priseEnchantillonCampementFaite = true;

            } else {
                // Calage sur X
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 0 : 180);
                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMM(ENTRY_X_DEPOSE_STATUETTE - config.distanceCalageArriere() - 10);
                mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
                rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                mv.reculeMMSansAngle(100);
                checkRecalageXmm(rs.team() == Team.JAUNE ? config.distanceCalageArriere() : EurobotConfig.tableWidth - config.distanceCalageArriere(), TypeCalage.ARRIERE);
                checkRecalageAngleDeg(rs.team() == Team.JAUNE ? 0 : 180, TypeCalage.ARRIERE);
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.avanceMM(ENTRY_X_DEPOSE_STATUETTE - config.distanceCalageArriere());
            }

            // Calage sur Y
            mv.gotoOrientationDeg(-90);

            if (isSecondaryEntryPoint && !priseEnchantillonCampementFaite) {
                // si on est arrivé par le point secondaire
                mv.reculeMM(ENTRY_Y_DEPOSE_STATUETTE - entry.getY());
            }
            servos.fourcheStatuetteAttente(false);

            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMM(EurobotConfig.tableHeight - ENTRY_Y_DEPOSE_STATUETTE - config.distanceCalageArriere() - 10);
            mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
            rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
            mv.reculeMMSansAngle(100);
            checkRecalageYmm(EurobotConfig.tableHeight - config.distanceCalageArriere(), TypeCalage.ARRIERE);
            checkRecalageAngleDeg(-90, TypeCalage.ARRIERE);

            // Dépose
            servos.fourcheStatuettePriseDepose(true);
            group.statuetteDansVitrine();
            group.vitrineActive();

            mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
            mv.avanceMM(100);
            servos.fourcheStatuetteFerme(false);

            if (task != null) {
                task.join();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            servos.fourcheStatuetteFerme(false);
            if (priseEnchantillonCampementFaite) {
                bras.safeHoming();
            }
            refreshCompleted();
        }
    }
}
