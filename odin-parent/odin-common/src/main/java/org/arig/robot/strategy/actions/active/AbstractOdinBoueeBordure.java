package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractOdinBoueeBordure extends AbstractOdinBouee {

    @Autowired
    protected IOdinIOService io;

    protected AbstractOdinBoueeBordure(int bouee) {
        super(bouee);
    }

    protected abstract Point beforeEntry();

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            GotoOption sens = rsOdin.boueeCouleur(bouee) == ECouleurBouee.VERT ? GotoOption.AVANT : GotoOption.ARRIERE;

            final int pinceCible = getPinceCible();
            final double offsetPince = getOffsetPince(pinceCible);

            log.info("Prise de la bouee {} {} dans la pince {} {}",
                    bouee, rsOdin.boueeCouleur(bouee), sens.name(), pinceCible);

            final Point boueePt = entryPoint();
            final double decallageX = offsetPince * (sens == GotoOption.AVANT ? -1 : 1);
            final Point entry = new Point( boueePt.getX() + decallageX,1690);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            Point beforeEntry = beforeEntry();
            if (beforeEntry != null) {
                mv.pathTo(beforeEntry);
                mv.gotoPoint(entry, sens);
            } else {
                mv.pathTo(entry, sens);
            }

            // prise de la bouée
            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry.getX(), 1830, sens);
            group.boueePrise(bouee);
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
            if (sens == GotoOption.AVANT) {
                mv.reculeMM(100);
            } else {
                mv.avanceMM(100);
            }
            if (beforeEntry != null) {
                mv.gotoPoint(beforeEntry, GotoOption.SANS_ORIENTATION);
            } else {
                mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);
            }

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
