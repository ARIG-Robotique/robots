package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseBoueesNord extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private NerellStatus rs;

    @Getter
    private boolean completed = false;

    @Autowired
    private ServosService servosService;

    @Override
    public String name() {
        return "Prise bouées nord";
    }

    @Override
    protected Point entryPoint() {
        double x = 434;
        double y = 1770;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean isValid() {
        return rs.getStrategy() == EStrategy.BASIC;
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);

            Point entry = entryPoint();
            double y = entry.getY();

            if (rs.getTeam() == ETeam.BLEU) {
                pincesAvantService.setEnabled(false, true, true, true);
                rs.enablePincesAvant();

                // attente d'ouverture des servos
                ThreadUtils.sleep(IConstantesNerellConfig.i2cReadTimeMs * 2);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 4);
                mv.gotoPointMM(entry, false, true, SensDeplacement.AVANT);
                rs.bouee(1).prise(true);
                rs.bouee(2).prise(true);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                mv.gotoOrientationDeg(0);
                servosService.pinceAvantOuvert(0, false);
                mv.gotoPointMM(640, y, false, SensDeplacement.AVANT);
                rs.bouee(5).prise(true);

                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);
                mv.gotoPointMM(940, 1662, true, SensDeplacement.AVANT);
                rs.bouee(6).prise(true);

            } else {
                pincesAvantService.setEnabled(true, true, true, false);
                rs.enablePincesAvant();

                // attente d'ouverture des servos
                ThreadUtils.sleep(IConstantesNerellConfig.i2cReadTimeMs * 2);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);
                mv.gotoPointMM(entry, false, true, SensDeplacement.AVANT);
                rs.bouee(13).prise(true);
                rs.bouee(14).prise(true);

                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT,4);
                mv.gotoOrientationDeg(180);
                servosService.pinceAvantOuvert(3, false);
                mv.gotoPointMM(3000 - 640, y, false, SensDeplacement.AVANT);
                rs.bouee(12).prise(true);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
                mv.gotoPointMM(3000 - 940, 1662, true, SensDeplacement.AVANT);
                rs.bouee(11).prise(true);
            }

        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = true;
            rs.disablePincesAvant();
        }
    }
}
