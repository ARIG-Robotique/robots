package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.services.IOdinIOService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractOdinBouee extends AbstractOdinAction {

    @Autowired
    protected IOdinIOService io;

    @Autowired
    protected AbstractOdinPincesAvantService pincesAvant;

    @Autowired
    protected AbstractOdinPincesArriereService pincesArriere;

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
        return isTimeValid() && rsOdin.boueePresente(bouee) && getPinceCible() != -1
                && rsOdin.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime;
    }

    @Override
    public void refreshCompleted() {
        if (!rsOdin.boueePresente(bouee)) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            final int pinceCibleTemp = getPinceCible(); // 0-1 à l'avant 2-3 à l'arriere
            final int pinceCible = pinceCibleTemp <= 1 ? pinceCibleTemp : pinceCibleTemp - 2;
            final GotoOption sens = pinceCibleTemp <= 1 ? GotoOption.AVANT : GotoOption.ARRIERE;
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
            group.boueePrise(bouee);

            // prise de la bouée
            final Point pointPrise;
            if (sens == GotoOption.ARRIERE) {
                // Prise avec la face arrière, donc en mode mirroir
                mv.alignBackToAvecDecalage(entry, -offsetOrientation);
                pointPrise = tableUtils.eloigner(-distanceApproche * 1.5);
                pincesArriere.setExpected(rs.boueeCouleur(bouee), pinceCible);
            } else {
                mv.alignFrontToAvecDecalage(entry, offsetOrientation);
                pointPrise = tableUtils.eloigner(distanceApproche * 1.5);
                pincesAvant.setExpected(rs.boueeCouleur(bouee), pinceCible);
            }
            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation());
            mv.gotoPoint(pointPrise, sens, GotoOption.SANS_ORIENTATION);
            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesArriere.setExpected(null, -1);
            pincesAvant.setExpected(null, -1);
        }
    }

    protected int getPinceCible() {
        ECouleurBouee couleurBouee = rs.boueeCouleur(bouee);
        ECouleurBouee couleurInverse = couleurBouee == ECouleurBouee.VERT ? ECouleurBouee.ROUGE : ECouleurBouee.VERT;
        Point bouePt = rs.boueePt(bouee);

        // sens optimal
        GotoOption sens = Math.abs(tableUtils.angle(bouePt)) < 90 ? GotoOption.AVANT : GotoOption.ARRIERE;

        for (int i = 0; i < 2; i++) {
            switch (sens) {
                case AVANT:
                    if (rsOdin.pincesAvant()[0] == null && rsOdin.pincesAvant()[1] != couleurInverse) {
                        return 0; // AvG
                    }
                    if (rsOdin.pincesAvant()[1] == null && rsOdin.pincesAvant()[0] != couleurInverse) {
                        return 1; // AvD
                    }
                    break;
                case ARRIERE:
                    if (rsOdin.pincesArriere()[0] == null && rsOdin.pincesArriere()[1] != couleurInverse) {
                        return 2; // ArG
                    }
                    if (rsOdin.pincesArriere()[1] == null && rsOdin.pincesArriere()[0] != couleurInverse) {
                        return 3; // ArD
                    }
                    break;
            }

            // sens pas optimal
            sens = sens == GotoOption.AVANT ? GotoOption.ARRIERE : GotoOption.AVANT;
        }

        return -1;
    }

    protected double getOffsetPince(int pinceCible) {
        return IOdinConstantesConfig.dstDeposeX[pinceCible];
    }
}
