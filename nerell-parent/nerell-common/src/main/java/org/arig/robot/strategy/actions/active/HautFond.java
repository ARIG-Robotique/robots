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
public class HautFond extends AbstractNerellAction {

    private static final int X = 800;
    private static final int Y = 1785;

    @Autowired
    protected INerellIOService io;

    @Override
    public String name() {
        return "Haut fond";
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.hautFondEmpty() && rsNerell.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime;
    }

    @Override
    public Point entryPoint() {
        if (position.getPt().getX() <= 1500) {
            return new Point(X, Y);
        } else {
            return new Point(3000 - X, Y);
        }
    }

    private Point finalPoint(double finalY) {
        if (position.getPt().getX() <= 1500) {
            return new Point(3000 - 1130, finalY);
        } else {
            return new Point(1130, finalY);
        }
    }


    @Override
    public int order() {
        // TODO : Calcule de point avec les paires
        int libre = 0;
        if (!io.presenceVentouse1()) libre++;
        if (!io.presenceVentouse2()) libre++;
        if (!io.presenceVentouse3()) libre++;
        if (!io.presenceVentouse4()) libre++;
        return Math.min(libre, rsNerell.hautFond().size()) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();

            final Point entry = entryPoint();

            // calcule le Y qui permettra de rencontrer le plus de bouées
            // médiane : https://stackoverflow.com/a/49215170
            final int nbHautFond = rsNerell.hautFond().size();
            final double medianY = rsNerell.hautFond().stream().map(Bouee::pt).mapToDouble(Point::getY).sorted()
                    .skip((nbHautFond - 1) / 2).limit(2 - nbHautFond % 2).average().orElse(entry.getY());
            final double finalY = Math.min(entry.getY(), medianY);

            entry.setY(finalY);
            final Point finalPoint = finalPoint(finalY);

            // On y va
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            // on ratisse en laissant l'évitement actif
            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(finalPoint, GotoOption.AVANT);

            // on marque tout comme pris, l'information mise à jour sera fournie par la balise
            rsNerell.hautFond(Collections.emptyList());

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            updateValidTime();
        }
    }
}
