package org.arig.robot.strategy.actions.active;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBouee extends AbstractNerellAction {

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private IIOService io;

    private final int numeroBouee;
    private Bouee bouee;

    @PostConstruct
    public void init() {
        bouee = rs.bouee(numeroBouee);
    }

    @Override
    public String name() {
        return "Bouee " + numeroBouee;
    }

    @Override
    public Point entryPoint() {
        return bouee.pt();
    }

    @Override
    public int order() {
        return 1 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && bouee.presente() && getPinceCible() != 0 && rs.getRemainingTime() > IConstantesNerellConfig.invalidPriseRemainingTime;
    }

    @Override
    public void execute() {
        try {
            final int pinceCible = getPinceCible();
            final double distanceAproche = IConstantesNerellConfig.pathFindingTailleBouee / 2.0 + 10;
            final double offsetPince = getOffsetPince(pinceCible);

            log.info("Prise de la bouee {} {} dans la pince avant {}", numeroBouee, bouee.couleur(), pinceCible);

            final Point entry = entryPoint();

            // point d'approche quelque part autour de la bouée
            // FIXME : si on fait le tour à cause d'un évittement ça fout tout en vrac
            final Point pointApproche = tableUtils.eloigner(entry, -distanceAproche);

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.pathTo(pointApproche, GotoOption.AVANT);

            // active les pinces
            pincesAvantService.setEnabled(pinceCible == 1, pinceCible == 2, pinceCible == 3, pinceCible == 4);
            pincesAvantService.setExpected(bouee.couleur() == ECouleurBouee.ROUGE ? Side.LEFT : Side.RIGHT, bouee.couleur(), pinceCible);
            rs.enablePincesAvant();

            // aligne la bonne pince sur la bouée
            mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);

            final double offsetOrientation = Math.toDegrees(Math.sin(offsetPince / distanceAproche));
            mv.alignFrontToAvecDecalage(entry.getX(), entry.getY(), offsetOrientation);

            // prise
            mv.avanceMM(180);
            bouee.setPrise();

            complete();
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            rs.disablePincesAvant();
        }
    }

    private int getPinceCible() {
        if (bouee.couleur() == ECouleurBouee.ROUGE) {
            if (!io.presencePinceAvantSup2()) {
                return 2;
            }
            if (!io.presencePinceAvantSup1()) {
                return 1;
            }
        } else {
            if (!io.presencePinceAvantSup3()) {
                return 3;
            }
            if (!io.presencePinceAvantSup4()) {
                return 4;
            }
        }
        return 0;
    }

    private double getOffsetPince(int pinceCible) {
        return IConstantesNerellConfig.dstDeposeAvantX[pinceCible - 1];
    }
}
