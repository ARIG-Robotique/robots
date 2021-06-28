package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellIOService;
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

            final Point boueePt = entryPoint();
            final double decallageX = getOffsetPince(getPinceCible()) * -1;
            final Point entry = new Point( boueePt.getX() + decallageX,1600);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            Point beforeEntry = beforeEntry();
            if (beforeEntry != null) {
                mv.pathTo(beforeEntry, GotoOption.AVANT);
                mv.gotoPoint(entry, GotoOption.AVANT);
            } else {
                mv.pathTo(entry, GotoOption.AVANT);
            }

            // prise de la bouée
            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry.getX(), 1840, GotoOption.AVANT);
            group.boueePrise(bouee);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
