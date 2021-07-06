package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractNerellBoueeBordure extends AbstractNerellBouee {

    @Autowired
    protected INerellIOService io;

    protected AbstractNerellBoueeBordure(int bouee) {
        super(bouee);
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            log.info("Prise de la bouee {} {}", bouee, rsNerell.boueeCouleur(bouee));

            final int pinceCible = getPinceCible();
            final Point boueePt = entryPoint();
            final double decallageX = getOffsetPince(pinceCible) * -1;
            final Point entry = new Point(boueePt.getX() + decallageX, 1700);

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry, GotoOption.AVANT);

            // prise de la bouée
            if (pinceCible <= 2) {
                pincesAvantService.setExpected(rsNerell.boueeCouleur(bouee), null);
            } else {
                pincesAvantService.setExpected(null, rsNerell.boueeCouleur(bouee));
            }

            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation());
            try {
                mv.gotoPoint(entry.getX(), 1840, GotoOption.AVANT);
            } catch (MovementCancelledException e) {
                // cas de blocage sur la bordure
            }

            group.boueePrise(bouee);
            complete();

            ThreadUtils.sleep(INerellConstantesConfig.WAIT_POMPES);
            mv.reculeMM(100);

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesAvantService.setExpected(null, null);
        }
    }

    @Override
    protected int getPinceCible() {
        if (rsNerell.boueeCouleur(bouee) == ECouleurBouee.ROUGE) {
            if (!io.presenceVentouse1()) {
                return 1;
            }
            if (!io.presenceVentouse2()) {
                return 2;
            }
        } else {
            if (!io.presenceVentouse4()) {
                return 4;
            }
            if (!io.presenceVentouse3()) {
                return 3;
            }
        }
        return 0;
    }
}
