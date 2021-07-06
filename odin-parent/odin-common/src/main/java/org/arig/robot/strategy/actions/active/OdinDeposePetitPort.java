package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinDeposePetitPort extends AbstractOdinAction {

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_PETIT_PORT;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(1800), 620);
    }

    @Override
    public int order() {
        Chenaux chenauxFuture1 = rsOdin.clonePetitChenaux();
        Chenaux chenauxFuture2 = rsOdin.clonePetitChenaux();
        int currentScoreChenaux = chenauxFuture1.score();

        chenauxFuture1.addVert(rsOdin.pincesAvant());
        chenauxFuture1.addRouge(rsOdin.pincesArriere());
        chenauxFuture2.addVert(rsOdin.pincesArriere());
        chenauxFuture2.addRouge(rsOdin.pincesAvant());

        int order = Math.max(chenauxFuture1.score(), chenauxFuture2.score()) + -currentScoreChenaux;
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() &&
                rs.stepsPetitPort() > 0 && // TODO faire l'action "moustaches" ?
                (!rsOdin.pincesArriereEmpty() || !rsOdin.pincesAvantEmpty());
    }

    @Override
    public void execute() {
        boolean didSomething = false;

        try {
            rsOdin.enablePincesArriere();
            rsOdin.enablePincesAvant();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            final Point entry = entryPoint();
            final double entryX = entry.getX();
            final double baseYStep = 245;
            final double offsetX = 100;
            final boolean isReversed = isReversed();
            double y = baseYStep + rs.stepsPetitPort() * 70;

            if (!rsOdin.pincesAvantEmpty()) {
                double x = entryX + (isReversed ? offsetX : -offsetX);

                mv.pathTo(x, y, GotoOption.AVANT);
                mv.gotoOrientationDeg(-90);

                pincesAvantService.deposePetitChenal(isReversed ? ECouleurBouee.ROUGE : ECouleurBouee.VERT);

                didSomething = true;
            }

            if (!rsOdin.pincesArriereEmpty()) {
                double x = entryX + (isReversed ? -offsetX : offsetX);

                mv.pathTo(x, y, GotoOption.ARRIERE);
                mv.gotoOrientationDeg(90);

                pincesArriereService.deposePetitChenal(isReversed ? ECouleurBouee.VERT : ECouleurBouee.ROUGE);

                didSomething = true;
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            if (didSomething) {
                group.incStepsPetitPort();
            }
        }
    }

    private boolean isReversed() {
        Chenaux chenauxFuture1 = rsOdin.clonePetitChenaux();
        Chenaux chenauxFuture2 = rsOdin.clonePetitChenaux();

        chenauxFuture1.addVert(rsOdin.pincesAvant());
        chenauxFuture1.addRouge(rsOdin.pincesArriere());
        chenauxFuture2.addVert(rsOdin.pincesArriere());
        chenauxFuture2.addRouge(rsOdin.pincesAvant());

        return chenauxFuture2.score() > chenauxFuture1.score();
    }
}
