package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
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

    protected abstract Point beforeEntry();

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            log.info("Prise de la bouee {} {}", bouee, rsNerell.boueeCouleur(bouee));

            final int pinceCible = getPinceCible();
            final Point boueePt = entryPoint();
            final double decallageX = getOffsetPince(pinceCible) * -1;
            final Point entry = new Point(boueePt.getX() + decallageX, 1600);
            final Point beforeEntry = beforeEntry();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (beforeEntry != null) {
                mv.pathTo(beforeEntry, GotoOption.AVANT);
                mv.gotoPoint(entry, GotoOption.AVANT);
            } else {
                mv.pathTo(entry, GotoOption.AVANT);
            }

            // prise de la bouée
            if (pinceCible <= 2) {
                pincesAvantService.setExpected(rsNerell.boueeCouleur(bouee), null);
            } else {
                pincesAvantService.setExpected(null, rsNerell.boueeCouleur(bouee));
            }

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry.getX(), 1840, GotoOption.AVANT);
            group.boueePrise(bouee);
            complete();
            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_POMPES);
            mv.reculeMM(100);
            if (beforeEntry != null) {
                mv.gotoPoint(beforeEntry, GotoOption.SANS_ORIENTATION);
            } else {
                mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesAvantService.setExpected(null, null);
        }
    }
}
