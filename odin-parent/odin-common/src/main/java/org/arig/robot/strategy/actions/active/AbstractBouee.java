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
public abstract class AbstractBouee extends AbstractOdinAction {

    @Autowired
    protected IOdinIOService io;

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
        // 1 pt pour la bouee
        // 1 pt pour la bonne couleur
        // 2 pt pour la paire
        return 4 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && rs.boueePresente(bouee) && getPinceCible() != 0
                && rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime;
    }

    @Override
    public void execute() {
        try {
            rs.enablePincesAvant();
            rs.enablePincesArriere();

            final int pinceCible = getPinceCible();
            final double distanceApproche = IEurobotConfig.pathFindingTailleBouee / 2.0 + 10;
            final double offsetPince = getOffsetPince(pinceCible);

            GotoOption sens = rs.boueeCouleur(bouee) == ECouleurBouee.VERT ? GotoOption.AVANT : GotoOption.ARRIERE;
            log.info("Prise de la bouee {} {} dans la pince {} {}", bouee, rs.boueeCouleur(bouee), sens.name(), pinceCible);

            final Point entry = entryPoint();

            // Point d'approche quelque part autour de la bouée
            final Point pointApproche = tableUtils.eloigner(entry, -distanceApproche);
            final double offsetOrientation = Math.toDegrees(Math.sin(offsetPince / distanceApproche));

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(pointApproche, sens);

            // prise de la bouée
            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            mv.gotoPoint(tableUtils.getPointFromAngle(180, offsetOrientation), sens, GotoOption.SANS_ORIENTATION);
            group.boueePrise(bouee);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }

    private int getPinceCible() {
        if (rs.boueeCouleur(bouee) == ECouleurBouee.VERT){
            if (!io.presenceVentouseAvantGauche()) {
                return 1;
            }
            if (!io.presenceVentouseAvantDroit()) {
                return 2;
            }
        }
        if (rs.boueeCouleur(bouee) == ECouleurBouee.ROUGE) {
            if (!io.presenceVentouseArriereGauche()) {
                return 1;
            }
            if (!io.presenceVentouseArriereDroit()) {
                return 2;
            }
        }
        return 0;
    }

    private double getOffsetPince(int pinceCible) {
        return IConstantesOdinConfig.dstDeposeAvantX[pinceCible - 1];
    }
}
