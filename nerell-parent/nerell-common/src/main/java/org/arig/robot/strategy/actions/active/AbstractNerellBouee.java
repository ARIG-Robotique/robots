package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellIOService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractNerellBouee extends AbstractNerellAction {

    @Autowired
    protected INerellIOService io;

    @Autowired
    protected INerellPincesAvantService pincesAvantService;

    protected int bouee;

    protected AbstractNerellBouee(int bouee) {
        this.bouee = bouee;
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + bouee;
    }

    @Override
    public Point entryPoint() {
        return rsNerell.boueePt(bouee);
    }

    @Override
    public int order() {
        // 1 pt pour la bouee
        // 1 pt pour la bonne couleur
        // 2 pt pour la paire
        return 4 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rsNerell.boueePresente(bouee) && getPinceCible() != 0 && rsNerell.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime;
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();

            final int pinceCible = getPinceCible();
            final double distanceApproche = IEurobotConfig.pathFindingTailleBouee / 2.0 + 10;
            final double offsetPince = getOffsetPince(pinceCible);

            log.info("Prise de la bouee {} {} dans la pince avant {}", bouee, rsNerell.boueeCouleur(bouee), pinceCible);

            final Point entry = entryPoint();

            // Point d'approche quelque part autour de la bouée
            final Point pointApproche = tableUtils.eloigner(entry, -distanceApproche);
            final double offsetOrientation = Math.toDegrees(Math.sin(offsetPince / distanceApproche));

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(pointApproche, GotoOption.AVANT);

            // prise de la bouée
            if (pinceCible <= 2) {
                pincesAvantService.setExpected(rsNerell.boueeCouleur(bouee), null);
            } else {
                pincesAvantService.setExpected(null, rsNerell.boueeCouleur(bouee));
            }

            mv.alignFrontTo(entry);
            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation());
            mv.gotoPoint(tableUtils.getPointFromAngle(distanceApproche * 2, offsetOrientation), GotoOption.AVANT);
            group.boueePrise(bouee);
            ThreadUtils.sleep(INerellConstantesConfig.WAIT_POMPES);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesAvantService.setExpected(null, null);
        }
    }

    protected int getPinceCible() {
        if (rsNerell.boueeCouleur(bouee) == ECouleurBouee.ROUGE) {
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

    protected double getOffsetPince(int pinceCible) {
        return INerellConstantesConfig.dstDeposeAvantX[pinceCible - 1];
    }
}
