package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractNerellPincesArriereService;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractNerellEcueil extends AbstractNerellAction {

    @Autowired
    private AbstractNerellPincesArriereService pincesArriereService;

    protected abstract double orientationPourPrise();

    protected abstract INerellPincesArriereService.EEcueil ecueil();

    protected abstract ECouleurBouee[] bouees();

    protected abstract byte nbBoueesDispo();

    protected abstract void onComplete();

    @Override
    public int order() {
        int order = nbBoueesDispo() * 2 + (int) Math.ceil(nbBoueesDispo() / 2.0) * 2; // Sur chenal, bien trié (X bouées, X/2 paires)
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        if (rsNerell.getRemainingTime() < IEurobotConfig.invalidPriseRemainingTime) {
            return false;
        }

        return isTimeValid() && !rsNerell.inPort() && rsNerell.pincesArriereEmpty() && nbBoueesDispo() > 0;
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            final double orientation = orientationPourPrise();

            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            mv.pathTo(entry);
            rsNerell.disableAvoidance();

            mv.gotoOrientationDeg(orientation);
            pincesArriereService.preparePriseEcueil();

            rsNerell.enableCalageBordure();
            mv.reculeMM(60);

            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            rsNerell.enableCalageBordure();
            mv.reculeMMSansAngle(120);

            // on en profite pour recaller un axe
            final double robotX = position.getPt().getX();
            final double robotY = position.getPt().getY();
            if (orientation == -90) {
                final double realY = conv.mmToPulse(2000 - INerellConstantesConfig.dstCallage);
                if (Math.abs(realY - robotY) > conv.mmToPulse(10)) {
                    log.warn("RECALAGE REQUIS : yRobot = {} ; yReel = {}",
                            conv.pulseToMm(robotY), conv.pulseToMm(realY));

                    position.getPt().setY(realY);
                    position.setAngle(conv.degToPulse(orientation));
                }

            } else if (orientation == 180 || orientation == 0) {
                final double realX = orientation == 180
                        ? conv.mmToPulse(3000 - INerellConstantesConfig.dstCallage)
                        : conv.mmToPulse(INerellConstantesConfig.dstCallage);

                if (Math.abs(realX - robotX) > conv.mmToPulse(10)) {
                    log.warn("RECALAGE REQUIS : xRobot = {} ; xReel = {}",
                            conv.pulseToMm(robotX), conv.pulseToMm(realX));
                    position.getPt().setX(realX);
                    position.setAngle(conv.degToPulse(orientation));
                }
            }

            pincesArriereService.finalisePriseEcueil(ecueil(), bouees());

            complete(); // Action terminé, on laisse le path finding reprendre la main pour le dégagement si on se fait bloqué
            onComplete();

            rsNerell.enableAvoidance();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            // dégagement en cas de blocage
            servosNerell.ascenseurArriereHaut(true);
            servosNerell.pincesArriereFerme(false);
            servosNerell.pivotArriereFerme(false);

            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }
}
