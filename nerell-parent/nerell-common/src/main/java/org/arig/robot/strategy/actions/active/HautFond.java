package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HautFond extends AbstractNerellAction {

    private static final int X = 800;
    private static final int Y = 2000 - 175; // TODO distance minimale de rasage du bord nord

    @Autowired
    protected INerellIOService io;

    @Override
    public String name() {
        return "Haut fond";
    }

    @Override
    public boolean isValid() {
        return !rs.hautFond().isEmpty();
    }

    @Override
    public Point entryPoint() {
        if (rs.team() == ETeam.BLEU) {
            return new Point(X, Y);
        } else {
            return new Point(3000 - X, Y);
        }
    }

    @Override
    public int order() {
        int libre = 0;
        if (!io.presencePinceAvantSup1()) libre++;
        if (!io.presencePinceAvantSup2()) libre++;
        if (!io.presencePinceAvantSup3()) libre++;
        if (!io.presencePinceAvantSup4()) libre++;
        return Math.min(libre, rs.hautFond().size()) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            rs.enablePincesAvant();

            final Point entry = entryPoint();

            // calcule le Y qui permettra de rencontrer le plus de bouées
            // médiane : https://stackoverflow.com/a/49215170
            final int nbHautFond = rs.hautFond().size();
            final double medianY = rs.hautFond().stream().map(Bouee::pt).mapToDouble(Point::getY).sorted()
                    .skip((nbHautFond - 1) / 2).limit(2 - nbHautFond % 2).average().orElse(entry.getY());

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry.getX(), Math.min(entry.getY(), medianY));

            mv.setVitesse(robotConfig.vitesse(1), robotConfig.vitesseOrientation());

            if (rs.team() == ETeam.BLEU) {
                mv.gotoOrientationDeg(0);
            } else {
                mv.gotoOrientationDeg(180);
            }

            // on ratisse en laissant l'évitement actif
            mv.avanceMM(3000 - X * 2);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}