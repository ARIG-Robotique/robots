package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.services.IIOService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HautFond extends AbstractNerellAction {

    @Autowired
    protected IIOService io;

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
            return new Point(800, 2750);
        } else {
            return new Point(2200, 2750);
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
            final Point entry = entryPoint();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(entry);

            mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);

            if (rs.team() == ETeam.BLEU) {
                mv.gotoOrientationDeg(0);
            } else {
                mv.gotoOrientationDeg(180);
            }

            // on ratisse en laissant l'évitement actif
            mv.avanceMM(3000 - entry.getX() * 2);

            // TODO pas de todo en fait, quand il y aura le service de prise automatique

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
