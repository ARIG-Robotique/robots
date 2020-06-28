package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseBoueesSud extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise bouées sud";
    }

    @Override
    protected Point entryPoint() {
        double x = 225;
        double y = 1200;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        if (rs.getStrategy() == EStrategy.BASIC_SUD) {
            return 1000;
        }
        return 6 + (rs.isEcueilEquipePris() ? 0 : 10) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return rs.pincesAvantEmpty() &&
                (rs.getTeam() == ETeam.JAUNE && rs.grandChenaux().chenalVertEmpty() || rs.grandChenaux().chenalRougeEmpty()
        );
    }

    @Override
    public void execute() {
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
                    mv.gotoPointMM(220, 1110, true);
                    mv.gotoOrientationDeg(-66);
                }

                pincesAvantService.setEnabled(true, true, true, false);
                rs.enablePincesAvant();

                // attente d'ouverture des servos
                ThreadUtils.sleep(IConstantesNerellConfig.i2cReadTimeMs * 3);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);

                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(target, false, true, SensDeplacement.AVANT);
                rs.bouee(3).prise(true);
                rs.bouee(4).prise(true);

                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
                servos.ascenseurAvantRoulage(false);
                mv.pathTo(910, 1070);
                servos.ascenseurAvantBas(false);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(1093, 1146, true);
                rs.bouee(7).prise(true);

                servos.pinceAvantOuvert(3, false);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 4);
                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(1330, 933, true);
                rs.bouee(8).prise(true);

            } else {
                if (rs.getStrategy() != EStrategy.BASIC_SUD) {
                    mv.gotoPointMM(3000 - 220, 1110, true);
                    mv.gotoOrientationDeg(-180 + 66);
                }

                pincesAvantService.setEnabled(false, true, true, true);
                rs.enablePincesAvant();

                // attente d'ouverture des servos
                ThreadUtils.sleep(IConstantesNerellConfig.i2cReadTimeMs * 3);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 4);

                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(target, false, true, SensDeplacement.AVANT);
                rs.bouee(15).prise(true);
                rs.bouee(16).prise(true);

                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
                servos.ascenseurAvantRoulage(false);
                mv.pathTo(3000 - 910, 1070);
                servos.ascenseurAvantBas(false);

                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);
                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(3000 - 1093, 1146, true);
                rs.bouee(10).prise(true);

                servos.pinceAvantOuvert(0, false);
                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(3000 - 1330, 933, true);
                rs.bouee(9).prise(true);
            }

        } catch (AvoidingException | NoPathFoundException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = true;
            rs.disablePincesAvant();
        }
    }
}
