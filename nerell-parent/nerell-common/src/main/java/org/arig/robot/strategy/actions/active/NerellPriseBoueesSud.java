package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ENerellStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellPriseBoueesSud extends AbstractNerellAction {

    @Autowired
    private NerellBouee8 bouee8;

    @Autowired
    private NerellBouee9 bouee9;

    private boolean firstExecution = true;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_SUD;
    }

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        if (rsNerell.strategy() == ENerellStrategy.BASIC_SUD && firstExecution) {
            return 1000;
        }
        return 6 + (rsNerell.ecueilEquipePris() ? 0 : 10) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        final boolean boueePresente;
        if (rsNerell.team() == ETeam.BLEU) {
            boueePresente = rsNerell.boueePresente(1) || rsNerell.boueePresente(2);
        } else {
            boueePresente = rsNerell.boueePresente(13) || rsNerell.boueePresente(14);
        }

        return rsNerell.pincesAvantEmpty() && boueePresente &&
                rsNerell.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                (rsNerell.team() == ETeam.JAUNE && rsNerell.grandChenalVertEmpty() || rsNerell.grandChenalRougeEmpty());
    }

    @Override
    public void execute() {
        firstExecution = false;
        try {
            rsNerell.enablePincesAvant();

            final Point entry = entryPoint();
            final int pctVitessePriseBouee = 20;
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            if (rsNerell.strategy() != ENerellStrategy.BASIC_SUD && tableUtils.distance(entry) > 100) {
                mv.pathTo(entry);
            } else {
                // Le path active l'évitement en auto, pas de path, pas d'évitement
                rsNerell.enableAvoidance();
            }

            double targetx = getX(434);
            double targety = 1200 - 570;
            final Point target = new Point(targetx, targety);

            if (rsNerell.team() == ETeam.BLEU) {
                if (rsNerell.strategy() != ENerellStrategy.BASIC_SUD) {
                    mv.gotoPoint(220, 1110);
                    mv.gotoOrientationDeg(-66);
                }

                mv.setVitesse(robotConfig.vitesse(pctVitessePriseBouee), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                group.boueePrise(3, 4);

                // en cas d'erreur sur bouee 9 ou 8
                complete();

                if (bouee8.isValid()) {
                    bouee8.execute();
                }
                if (bouee9.isValid()) {
                    bouee9.execute();
                }

            } else {
                if (rsNerell.strategy() != ENerellStrategy.BASIC_SUD) {
                    mv.gotoPoint(3000 - 220, 1110);
                    mv.gotoOrientationDeg(-180 + 66);
                }

                mv.setVitesse(robotConfig.vitesse(pctVitessePriseBouee), robotConfig.vitesseOrientation());
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                group.boueePrise(15, 16);

                // en cas d'erreur sur bouee 9 ou 8
                complete();

                if (bouee9.isValid()) {
                    bouee9.execute();
                }
                if (bouee8.isValid()) {
                    bouee8.execute();
                }
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            complete();
        }
    }
}
