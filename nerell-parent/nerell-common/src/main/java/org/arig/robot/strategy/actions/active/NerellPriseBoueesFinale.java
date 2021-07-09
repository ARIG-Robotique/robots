package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NerellPriseBoueesFinale extends AbstractNerellAction {

    @Override
    public String name() {
        return IEurobotConfig.ACTION_PRISE_BOUEE_FINALE;
    }

    @Override
    public Point entryPoint() {
        // Dans la zone de départ on est a 245 (moduloTeam), 1090
        // Face à cet entry
        return new Point(getX(700), 1270);
    }

    @Override
    public int order() {
        return rsNerell.strategy() == EStrategy.FINALE ? 1000 : -1000;
    }

    @Override
    public boolean isValid() {
        return rsNerell.strategy() == EStrategy.FINALE;
    }

    @Override
    public void refreshCompleted() {
        if (rsNerell.strategy() != EStrategy.FINALE) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            mv.gotoPoint(entryPoint(), GotoOption.AVANT, GotoOption.SANS_ARRET, GotoOption.SANS_ORIENTATION);

            // On est sorti du port on active l'évitement
            rsNerell.enableAvoidance();

            // On passe entre les bouées 6 et 7 (10 et 11 en jaune)
            mv.gotoPoint(getX(1119), 1429, GotoOption.AVANT, GotoOption.SANS_ORIENTATION);

            // Ouverture des moustaches
            servosNerell.moustacheDroiteOuvert(false);
            servosNerell.moustacheGaucheOuvert(false);

            // Go to the stuff everything fucking zone
            mv.gotoPoint(getX(1282), 1736, GotoOption.AVANT, GotoOption.SANS_ORIENTATION);

            // On ne retentera plus le haut fond, mais il peut y avoir du déchets !!
            group.hautFondPris();

            // Coordonnée de l'ecueil adverse
            final double ecueilAdverseX;
            if (rs.team() == ETeam.BLEU) {
                ecueilAdverseX = NerellEcueilCommunJaune.ENTRY_X;
            } else {
                ecueilAdverseX = NerellEcueilCommunBleu.ENTRY_X;
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation(25));
            mv.alignFrontTo(ecueilAdverseX, 1736);
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(ecueilAdverseX + 80, 1736);

        } catch (AvoidingException e) {
            if ((position.getPt().getX() > 1500 && rs.team() == ETeam.BLEU)
                || (position.getPt().getX() < 1500 && rs.team() == ETeam.JAUNE)) {
                // On a dépassé le mileu de la table, on invalide l'ecueil adverse
                group.ecueilCommunAdversePris();
            }

            try {
                log.warn("Echappement action car obstacle");
                mv.alignBackTo(entryPoint());
            } catch (AvoidingException e2) {
                log.error("Pas de bol même sur l'échappement");
            }
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            try {
                log.warn("Finally action");
                mv.reculeMM(80);
            } catch (AvoidingException e2) {
                log.error("Pas de bol sur le finally");
            }
            servosNerell.moustachesFerme(false);
            complete();

            // C'est la finale, en face normalement ce ne sont pas des branquignole ;-)
            if (rs.team() == ETeam.BLEU) {
                group.boueePrise(11, 12);
            } else {
                group.boueePrise(5, 6);
            }
        }
    }
}
