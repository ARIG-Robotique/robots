package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
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
            final Point entry = new Point(boueePt.getX() + decallageX, 1690);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry, sens);

            // prise de la bouée
            if (sens == GotoOption.ARRIERE) {
                pincesArriere.setExpected(rs.boueeCouleur(bouee), pinceCible);
            } else {
                pincesAvant.setExpected(rs.boueeCouleur(bouee), pinceCible);
            }

            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation());
            try {
                mv.gotoPoint(entry.getX(), 1830, sens);
            } catch (MovementCancelledException e) {
                // cas de blocage sur la bordure
            }

            group.boueePrise(bouee);
            complete();

            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
            if (sens == GotoOption.AVANT) {
                mv.reculeMM(100);
            } else {
                mv.avanceMM(100);
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesArriere.setExpected(null, 0);
            pincesAvant.setExpected(null, 0);
        }
    }

    @Override
    protected int getPinceCible() {
        if (rsOdin.boueeCouleur(bouee) == ECouleurBouee.VERT) {
            if (!io.presenceVentouseAvantDroit()) {
                return 2;
            }
            if (!io.presenceVentouseAvantGauche()) {
                return 1;
            }
        }
        if (rsOdin.boueeCouleur(bouee) == ECouleurBouee.ROUGE) {
            if (!io.presenceVentouseArriereDroit()) {
                return 2;
            }
            if (!io.presenceVentouseArriereGauche()) {
                return 1;
            }
        }
        return 0;
    }
}
