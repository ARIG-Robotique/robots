package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
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
    private NerellStatus rs;

    @Autowired
    private ServosService servos;

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
        return 6 + (rs.isEcueilEquipePrit() ? 0 : 10);
    }

    @Override
    public boolean isValid() {
        return rs.pincesAvantEmpty() && (
                rs.getTeam() == ETeam.JAUNE && rs.grandChenaux().chenalVertEmpty() || rs.grandChenaux().chenalRougeEmpty()
        );
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            Point entry = entryPoint();

            double targetx = 434;
            double targety = 1200 - 570;
            if (ETeam.JAUNE == rs.getTeam()) {
                targetx = 3000 - targetx;
            }
            Point target = new Point(targetx, targety);

            rs.enableAvoidance();
            mv.pathTo(entry);
            rs.disableAvoidance();

            if (rs.getTeam() == ETeam.BLEU) {
                mv.gotoPointMM(220, 1110, true);
                mv.gotoOrientationDeg(-66);

                pincesAvantService.setEnabled(true, true, true, false);
                rs.enablePincesAvant();
                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);

                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(target, false, true, SensDeplacement.AVANT);

                rs.bouee(3).prise(true);
                rs.bouee(4).prise(true);

                servos.ascenseurAvantRoulage(false);

                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
                rs.enableAvoidance();
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
                mv.gotoPointMM(3000 - 220, 1110, true);
                mv.gotoOrientationDeg(-180 + 66);

                pincesAvantService.setEnabled(false, true, true, true);
                rs.enablePincesAvant();
                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 4);

                mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
                mv.gotoPointMM(target, false, true, SensDeplacement.AVANT);

                rs.bouee(15).prise(true);
                rs.bouee(16).prise(true);

                servos.ascenseurAvantRoulage(false);

                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
                rs.enableAvoidance();
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
