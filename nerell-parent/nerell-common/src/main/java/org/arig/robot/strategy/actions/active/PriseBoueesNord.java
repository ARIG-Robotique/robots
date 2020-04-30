package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriseBoueesNord extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

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
            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);
            rs.enablePincesAvant();

            Point entry = entryPoint();
            double y = entry.getY();

            if (rs.getTeam() == ETeam.BLEU) {
                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 3);
                mv.gotoPointMM(entry, false, true, SensDeplacement.AVANT);
                rs.bouee1().prise(true);
                rs.bouee2().prise(true);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 0);
                mv.gotoPointMM(640, y, true, SensDeplacement.AVANT);
                rs.bouee5().prise(true);

                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 2);
                mv.gotoPointMM(940, 1662, true, SensDeplacement.AVANT);
                rs.bouee6().prise(true);

            } else {
                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 0);
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 2);
                mv.gotoPointMM(entry, false, true, SensDeplacement.AVANT);
                rs.bouee13().prise(true);
                rs.bouee14().prise(true);

                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT,3);
                mv.gotoPointMM(3000 - 640, y, true, SensDeplacement.AVANT);
                rs.bouee12().prise(true);

                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 1);
                mv.gotoPointMM(3000 - 940, 1662, true, SensDeplacement.AVANT);
                rs.bouee11().prise(true);
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
