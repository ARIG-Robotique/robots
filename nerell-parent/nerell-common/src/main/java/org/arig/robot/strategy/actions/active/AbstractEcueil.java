package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractEcueil extends AbstractNerellAction {

    @Autowired
    protected NerellRobotStatus rs;

    @Autowired
    protected TableUtils tableUtils;

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Getter
    private boolean completed = false;

    protected abstract double orientationPourPrise();

    protected abstract ECouleurBouee[] bouees();

    protected abstract byte nbBoueesDispo();

    protected abstract void onComplete();

    protected void onStart() {}

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
    public final void execute() {
        try {
            onStart();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            mv.pathTo(entry);
            rs.disableAvoidance();
            mv.gotoOrientationDeg(orientationPourPrise());

            pincesArriereService.preparePriseEcueil();
            mv.reculeMM(60);

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMMSansAngle(60);
            pincesArriereService.finalisePriseEcueil(bouees());

            completed = true; // Action terminé, on laisse le path finding reprendre la main pour le dégagement si on se fait bloqué
            onComplete();

            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.gotoPoint(entry, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}
