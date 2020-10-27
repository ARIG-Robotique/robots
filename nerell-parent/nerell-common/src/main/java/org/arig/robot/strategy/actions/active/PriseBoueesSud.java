package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseBoueesSud extends AbstractNerellAction {

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private Bouee8 bouee8;

    @Autowired
    private Bouee9 bouee9;

    private boolean firstExecution = true;

    @Override
    public String name() {
        return "Prise bouées sud";
    }

    @Override
    public Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        if (rs.getStrategy() == EStrategy.BASIC_SUD && firstExecution) {
            return 1000;
        }
        return 6 + (rs.isEcueilEquipePris() ? 0 : 10) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return rs.pincesAvantEmpty() &&
                rs.getRemainingTime() > IConstantesNerellConfig.invalidPriseRemainingTime &&
                (rs.getTeam() == ETeam.JAUNE && rs.grandChenaux().chenalVertEmpty() || rs.grandChenaux().chenalRougeEmpty()
        );
    }

    @Override
    public void execute() {
        firstExecution = false;
        try {
            final Point entry = entryPoint();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            if (rs.getStrategy() != EStrategy.BASIC_SUD && tableUtils.distance(entry) > 100) {
                mv.pathTo(entry);
            } else {
                // Le path active l'évittement en auto, pas de path, pas d'evittement
                rs.enableAvoidance();
            }

            double targetx = 434;
            double targety = 1200 - 570;
            if (ETeam.JAUNE == rs.getTeam()) {
                targetx = 3000 - targetx;
            }
            final Point target = new Point(targetx, targety);

            if (rs.getTeam() == ETeam.BLEU) {
                if (rs.getStrategy() != EStrategy.BASIC_SUD) {
                    mv.gotoPoint(220, 1110);
                    mv.gotoOrientationDeg(-66);
                }

                pincesAvantService.setEnabled(true, true, true, false);
                rs.enablePincesAvant();

                // attente d'ouverture des servos
                ThreadUtils.sleep(400);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);

                mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.bouee(3).setPrise();
                rs.bouee(4).setPrise();

                rs.disablePincesAvant();

                // en cas d'erreur sur bouee 9 ou 8
                complete();

                if (bouee8.isValid()) {
                    bouee8.execute();
                }
                if (bouee9.isValid()) {
                    bouee9.execute();
                }

            } else {
                if (rs.getStrategy() != EStrategy.BASIC_SUD) {
                    mv.gotoPoint(3000 - 220, 1110);
                    mv.gotoOrientationDeg(-180 + 66);
                }

                pincesAvantService.setEnabled(false, true, true, true);
                rs.enablePincesAvant();

                // attente d'ouverture des servos
                ThreadUtils.sleep(IConstantesNerellConfig.i2cReadTimeMs * 3);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 4);

                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPoint(target, GotoOption.SANS_ORIENTATION, GotoOption.AVANT);
                rs.bouee(15).setPrise();
                rs.bouee(16).setPrise();

                rs.disablePincesAvant();

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
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            complete();
            rs.disablePincesAvant();
        }
    }
}
