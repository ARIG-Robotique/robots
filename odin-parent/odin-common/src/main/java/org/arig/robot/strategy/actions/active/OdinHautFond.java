package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class OdinHautFond extends AbstractOdinAction {

    private static final int X = 800;
    private static final int Y = 1850;

    @Autowired
    protected IOdinIOService io;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_HAUT_FOND;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                (rsOdin.pincesAvantEmpty() || rsOdin.pincesArriereEmpty()) && !rs.hautFondPris();
    }

    @Override
    public Point entryPoint() {
        if (conv.pulseToMm(position.getPt().getX()) <= 1500) {
            return new Point(X, Y);
        } else {
            return new Point(3000 - X, Y);
        }
    }

    private Point finalPoint() {
        if (conv.pulseToMm(position.getPt().getX()) <= 1500) {
            return new Point(3000 - 1130, Y);
        } else {
            return new Point(1130, Y);
        }
    }

    private Point middlePoint() {
        return new Point(1500, Y);
    }

    @Override
    public int order() {
        int libre = 0;
        if (!io.presenceVentouseAvantGauche()) libre += 3;
        if (!io.presenceVentouseAvantDroit()) libre += 3;
        if (!io.presenceVentouseArriereGauche()) libre += 3;
        if (!io.presenceVentouseArriereDroit()) libre += 3;
        return Math.min(libre, rsOdin.hautFond().size()) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            final Point entry = entryPoint();
            final Point middlePoint = middlePoint();
            final Point finalPoint = finalPoint();

            // Détermination du sens d'entrée
            long sizeAvant = Stream.of(rsOdin.pincesAvant()).filter(Objects::nonNull).count();
            long sizeArriere = Stream.of(rsOdin.pincesArriere()).filter(Objects::nonNull).count();

            final GotoOption entrySens;
            final GotoOption finalSens;
            if (sizeAvant <= sizeArriere) {
                // Plus de place a l'avant. On rentre en avant, on fini en arriere
                entrySens = GotoOption.AVANT;
                finalSens = GotoOption.ARRIERE;
            } else {
                entrySens = GotoOption.ARRIERE;
                finalSens = GotoOption.AVANT;
            }

            // On y va
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry, entrySens);

            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(middlePoint, entrySens);

            if (entrySens == GotoOption.AVANT) {
                mv.reculeMM(100);
            } else {
                mv.avanceMM(100);
            }
            mv.tourneDeg(180);
            mv.gotoPoint(finalPoint, finalSens);

            group.hautFondPris();
            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            updateValidTime();
        }
    }
}
