package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.GrandChenaux;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class NerellEchangeEcueil extends AbstractNerellAction {

    @Autowired
    private INerellPincesArriereService pincesArriereService;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_ECHANGE_ECUEIL;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_MANCHE_A_AIR
    );

    @Override
    public Rectangle blockingZone() {
        return rs.team() == ETeam.BLEU ? IEurobotConfig.ZONE_ECUEIL_EQUIPE_BLEU : IEurobotConfig.ZONE_ECUEIL_EQUIPE_JAUNE;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsNerell.inPort() && rs.echangeEcueil() && rs.groupOk() &&
                rs.mancheAAir1() && rs.mancheAAir2() && !rsNerell.pincesArriereEmpty() &&
                rs.getRemainingTime() > 35000 &&
                Arrays.deepEquals(rsNerell.pincesArriere(), rs.couleursEcueilEquipe());
    }

    @Override
    public void refreshCompleted() {
        if (!rs.echangeEcueil() || !rs.groupOk()) {
            complete();
        }
    }

    @Override
    public int order() {
        return 500;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(330), 400);
    }

    @Override
    public void execute() {
        try {
            rsNerell.enablePincesAvant();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entryPoint());

            mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

            final boolean deposeExtremite;
            if (rs.team() == ETeam.BLEU) {
                deposeExtremite = rs.getGrandChenalRouge(GrandChenaux.Line.A).get(4) == null;

                mv.gotoOrientationDeg(45);

                servosNerell.pivotArriereOuvert(true);
                servosNerell.ascenseurArriereTable(true);

                if (!deposeExtremite) {
                    servosNerell.pinceArriereOuvert(0, false);
                }
                servosNerell.pinceArriereOuvert(1, false);
                servosNerell.pinceArriereOuvert(3, true);

                mv.avanceMM(300);

                servosNerell.pinceArriereOuvert(2, false);
                servosNerell.pinceArriereOuvert(4, true);

                servosNerell.ascenseurArriereHaut(true);
                servosNerell.pivotArriereFerme(false);
                servosNerell.pinceArriereFerme(1, false);
                servosNerell.pinceArriereFerme(2, false);
                servosNerell.pinceArriereFerme(3, false);
                servosNerell.pinceArriereFerme(4, false);

                group.echangeReady();

                mv.gotoPoint(580, 685, GotoOption.AVANT);

                if (deposeExtremite) {
                    servosNerell.pivotArriereOuvert(false);
                    mv.gotoOrientationDeg(-15);

                    servosNerell.ascenseurArriereTable(true);
                    servosNerell.pinceArriereOuvert(0, true);

                    group.deposeGrandChenalRouge(GrandChenaux.Line.A, 4, ECouleurBouee.ROUGE);
                }

            } else {
                deposeExtremite = rs.getGrandChenalVert(GrandChenaux.Line.A).get(4) == null;

                mv.gotoOrientationDeg(135);

                servosNerell.pivotArriereOuvert(true);
                servosNerell.ascenseurArriereTable(true);

                if (!deposeExtremite) {
                    servosNerell.pinceArriereOuvert(4, false);
                }
                servosNerell.pinceArriereOuvert(1, false);
                servosNerell.pinceArriereOuvert(3, true);

                mv.avanceMM(300);

                servosNerell.pinceArriereOuvert(2, false);
                servosNerell.pinceArriereOuvert(0, true);

                servosNerell.ascenseurArriereHaut(true);
                servosNerell.pivotArriereFerme(false);
                servosNerell.pinceArriereFerme(0, false);
                servosNerell.pinceArriereFerme(1, false);
                servosNerell.pinceArriereFerme(2, false);
                servosNerell.pinceArriereFerme(3, false);

                group.echangeReady();

                mv.gotoPoint(2420, 685, GotoOption.AVANT);

                if (deposeExtremite) {
                    servosNerell.pivotArriereOuvert(false);
                    mv.gotoOrientationDeg(-165);

                    servosNerell.ascenseurArriereTable(true);
                    servosNerell.pinceArriereOuvert(4, true);

                    group.deposeGrandChenalVert(GrandChenaux.Line.A, 4, ECouleurBouee.VERT);
                }
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            pincesArriereService.finalizeDeposeTableEchange();
            complete();
        }
    }
}
