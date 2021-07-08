package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class NerellHautFond extends AbstractNerellAction {

    private static final int X = 800;
    private static final int Y = 1785;

    @Autowired
    protected INerellIOService io;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_HAUT_FOND;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                rsNerell.pincesAvantEmpty() &&
                (rsNerell.baliseEnabled() ? rs.hautFond().size() > 1 : !rs.hautFondPris());
    }

    @Override
    public Point entryPoint() {
        if (conv.pulseToMm(position.getPt().getX()) <= 1500) {
            return new Point(X, Y);
        } else {
            return new Point(3000 - X, Y);
        }
    }

    private Point finalPoint(double finalY) {
        if (conv.pulseToMm(position.getPt().getX()) <= 1500) {
            return new Point(3000 - 1130, finalY);
        } else {
            return new Point(1130, finalY);
        }
    }


    @Override
    public int order() {
        int libre = 0;
        if (!io.presenceVentouse1()) libre += 3;
        if (!io.presenceVentouse2()) libre += 3;
        if (!io.presenceVentouse3()) libre += 3;
        if (!io.presenceVentouse4()) libre += 3;
        int hautFond = rsNerell.baliseEnabled() ? rsNerell.hautFond().size() : 6;
        return Math.min(libre, hautFond) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();

            final Point entry = entryPoint();

            // calcule le Y qui permettra de rencontrer le plus de bouées
            final double finalY;

            if (rsNerell.baliseEnabled()) {
                // médiane : https://stackoverflow.com/a/49215170
                final int nbHautFond = rsNerell.hautFond().size();
                final double medianY = rsNerell.hautFond().stream().map(Bouee::pt).mapToDouble(Point::getY).sorted()
                        .skip((nbHautFond - 1) / 2).limit(2 - nbHautFond % 2).average().orElse(entry.getY());
                finalY = Math.min(entry.getY(), medianY);
            } else {
                finalY = entry.getY();
            }

            entry.setY(finalY);
            final Point finalPoint = finalPoint(finalY);

            // On y va
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            // on ratisse en laissant l'évitement actif
            if (finalPoint.getX() > entry.getX()) {
                mv.gotoOrientationDeg(0);
                servosNerell.moustacheGaucheOuvert(false);
            } else {
                mv.gotoOrientationDeg(180);
                servosNerell.moustacheDroiteOuvert(false);
            }

            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(finalPoint, GotoOption.AVANT);

            // on marque tout comme pris, l'information mise à jour sera fournie par la balise
            rsNerell.hautFond(Collections.emptyList());
            group.hautFondPris();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            updateValidTime();

            servosNerell.moustachesFerme(false);
        }
    }
}
