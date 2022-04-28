package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.CommonIOService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseDistributeurEquipe extends AbstractEurobotAction {

    private static final int DISTRIB_H = 102;

    private static final int ENTRY_X = 300;
    private static final int ENTRY_Y = 750;

    @Autowired
    private BrasService brasService;

    @Autowired
    private CommonIOService io;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_EQUIPE;
    }

    @Override
    public int order() {
        int points = 6; // 1 points par échantillons pris + 1 point de dépose
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.distributeurEquipePris()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        // TODO temps max de prise (même paramètre pour toutes les actions)
        return !rs.distributeurEquipePris() && isTimeValid() && remainingTimeValid() && rs.stockDisponible() >= 3;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(entry);

            // Calage sur X
            mv.setVitesse(robotConfig.vitesse(10), robotConfig.vitesseOrientation());
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
            rs.enableCalageBordure(TypeCalage.AVANT);
            rs.disableAvoidance();
            mv.avanceMM(ENTRY_X - robotConfig.distanceCalageAvant());
            if (!io.calageAvantDroit() || !io.calageAvantGauche()) {
                // FIXME on est pas en face
            }
            rs.enableAvoidance();
            mv.reculeMM(ENTRY_X - robotConfig.distanceCalageAvant() - DISTRIB_H - 10);
            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);

            rs.disableAvoidance();

            brasService.initPrise(BrasService.TypePrise.DISTRIBUTEUR);

            for (int i = 0; i < 3; i++) {
                mv.avanceMM(20);

                if (brasService.processPrise(BrasService.TypePrise.DISTRIBUTEUR)) {
                    if (i == 2) {
                        mv.reculeMM(30);
                    }
                    CouleurEchantillon couleur = i == 0 ? CouleurEchantillon.ROCHER_BLEU :
                            i == 1 ? CouleurEchantillon.ROCHER_VERT :
                                    CouleurEchantillon.ROCHER_ROUGE;
                    brasService.stockagePrise(BrasService.TypePrise.DISTRIBUTEUR, couleur);
                }
            }

            brasService.finalizePrise();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entry, GotoOption.ARRIERE);

            group.distributeurEquipePris();

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            refreshCompleted();
        }
    }
}
