package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractEcueil extends AbstractNerellAction {

    @Getter
    private boolean firstExecution = true;

    @Autowired
    private IPincesArriereService pincesArriereService;

    protected abstract double orientationPourPrise();

    protected abstract ECouleurBouee[] bouees();

    protected abstract byte nbBoueesDispo();

    protected abstract void onComplete();

    protected abstract void onAgressiveMvtDone();

    public abstract Point aggressiveIntermediaryPoint();

    @Override
    public int order() {
        int order = nbBoueesDispo() * 2 + (int) Math.ceil(nbBoueesDispo() / 2.0) * 2; // Sur chenal, bien trié (X bouées, X/2 paires)
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        if (rs.getRemainingTime() < IConstantesNerellConfig.invalidPriseRemainingTime) {
            return false;
        }

        return isTimeValid() && !rs.inPort() && rs.pincesArriereEmpty() && nbBoueesDispo() > 0;
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            final double orientation = orientationPourPrise();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            if (rs.getStrategy() == EStrategy.AGGRESSIVE && firstExecution && aggressiveIntermediaryPoint() != null) {
                firstExecution = false;

                rs.enableAvoidance();
                mv.gotoPoint(aggressiveIntermediaryPoint());
                mv.gotoPoint(entry);

                onAgressiveMvtDone();

            } else {
                firstExecution = false;

                mv.pathTo(entry);
                rs.disableAvoidance();
            }

            mv.gotoOrientationDeg(orientation);

            pincesArriereService.preparePriseEcueil();
            mv.reculeMM(60);

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMMSansAngle(60);

            // on en profite pour recaller un axe
            if (orientation == -90) {
//                position.getPt().setY(conv.mmToPulse(2000 - IConstantesNerellConfig.dstCallageY));
            } else if (orientation == 180) {
//                position.getPt().setX(conv.mmToPulse(3000 - IConstantesNerellConfig.dstCallageY));
            } else if (orientation == 0) {
//                position.getPt().setX(conv.mmToPulse(IConstantesNerellConfig.dstCallageY));
            }

            pincesArriereService.finalisePriseEcueil(bouees());

            complete(); // Action terminé, on laisse le path finding reprendre la main pour le dégagement si on se fait bloqué
            onComplete();

            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            // blocage potentiel sur un gobelet devant le robot, la finalisation de prise permet de remonter l'ascenseur
            pincesArriereService.finalisePriseEcueil(bouees());

            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
