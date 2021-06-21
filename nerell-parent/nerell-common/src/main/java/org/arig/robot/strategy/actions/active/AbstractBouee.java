package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractBouee extends AbstractNerellAction {

    @Autowired
    protected INerellIOService io;

    protected int bouee;

    protected AbstractBouee(int bouee) {
        this.bouee = bouee;
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_PREFIXE + bouee;
    }

    @Override
    public Point entryPoint() {
        return rs.boueePt(bouee);
    }

    @Override
    public int order() {
        return 1 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.boueePresente(bouee) && getPinceCible() != 0 && rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime;
    }

    @Override
    public void execute() {
        try {
            rs.enablePincesAvant();

            final int pinceCible = getPinceCible();
            final double distanceApproche = IEurobotConfig.pathFindingTailleBouee / 2.0 + 10;
            final double offsetPince = getOffsetPince(pinceCible);

            log.info("Prise de la bouee {} {} dans la pince avant {}", bouee, rs.boueeCouleur(bouee), pinceCible);

            final Point entry = entryPoint();

            // point d'approche quelque part autour de la bouée
            // FIXME : si on fait le tour à cause d'un évittement ça fout tout en vrac
            final Point pointApproche = tableUtils.eloigner(entry, -distanceApproche);

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(pointApproche, GotoOption.AVANT);

            // aligne la bonne pince sur la bouée
            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

            final double offsetOrientation = Math.toDegrees(Math.sin(offsetPince / distanceApproche));
            mv.alignFrontToAvecDecalage(entry.getX(), entry.getY(), offsetOrientation);

            // prise
            mv.avanceMM(180);
            group.boueePrise(bouee);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }

    private int getPinceCible() {
        // FIXME obsolète avec le capteur couleur ?
        if (rs.boueeCouleur(bouee) == ECouleurBouee.ROUGE) {
            if (!io.presenceVentouse2()) {
                return 2;
            }
            if (!io.presenceVentouse1()) {
                return 1;
            }
        } else {
            if (!io.presenceVentouse3()) {
                return 3;
            }
            if (!io.presenceVentouse4()) {
                return 4;
            }
        }
        return 0;
    }

    private double getOffsetPince(int pinceCible) {
        return IConstantesNerellConfig.dstDeposeAvantX[pinceCible - 1];
    }
}
