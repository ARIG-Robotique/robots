package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractOdinBouee extends AbstractOdinAction {

    @Autowired
    protected IOdinIOService io;

    protected int bouee;

    protected AbstractOdinBouee(int bouee) {
        this.bouee = bouee;
    }

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + bouee;
    }

    @Override
    public Point entryPoint() {
        return rsOdin.boueePt(bouee);
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
        return isTimeValid() && rsOdin.boueePresente(bouee) && getPinceCible() != 0
                && rsOdin.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime;
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            final GotoOption sens = rsOdin.boueeCouleur(bouee) == ECouleurBouee.VERT ? GotoOption.AVANT : GotoOption.ARRIERE;

            final int pinceCible = getPinceCible();
            final double offsetPince = getOffsetPince(pinceCible);
            final double distanceApproche = IEurobotConfig.pathFindingTailleBouee / 2.0 + 10;

            log.info("Prise de la bouee {} {} dans la pince {} {}",
                    bouee, rsOdin.boueeCouleur(bouee), sens.name(), pinceCible);

            final Point entry = entryPoint();

            // Point d'approche quelque part autour de la bouée
            final Point pointApproche = tableUtils.eloigner(entry, -distanceApproche);
            final double offsetOrientation = Math.toDegrees(Math.sin(offsetPince / distanceApproche));

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(pointApproche, sens);

            // prise de la bouée
            final Point pointPrise;
            if (sens == GotoOption.ARRIERE) {
                // Prise avec la face arrière, donc en mode mirroir
                pointPrise = tableUtils.getPointFromAngle(-distanceApproche, -offsetOrientation);
            } else {
                pointPrise = tableUtils.getPointFromAngle(distanceApproche, offsetOrientation);
            }
            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(pointPrise, sens);
            group.boueePrise(bouee);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }

    protected int getPinceCible() {
        if (rsOdin.boueeCouleur(bouee) == ECouleurBouee.VERT){
            if (!io.presenceVentouseAvantGauche()) {
                return 1;
            }
            if (!io.presenceVentouseAvantDroit()) {
                return 2;
            }
        }
        if (rsOdin.boueeCouleur(bouee) == ECouleurBouee.ROUGE) {
            if (!io.presenceVentouseArriereGauche()) {
                return 1;
            }
            if (!io.presenceVentouseArriereDroit()) {
                return 2;
            }
        }
        return 0;
    }

    protected double getOffsetPince(int pinceCible) {
        return IConstantesOdinConfig.dstDeposeX[pinceCible - 1];
    }
}
