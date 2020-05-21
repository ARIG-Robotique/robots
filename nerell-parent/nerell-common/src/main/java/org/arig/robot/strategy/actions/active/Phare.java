package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Phare extends AbstractAction {

    public static final double ENTRY_X = 225;
    public static final double ENTRY_Y = 1765;

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private ServosService servos;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    private SensDeplacement sensEntry = SensDeplacement.AUTO;

    @Override
    public String name() {
        return "Phare";
    }

    @Override
    protected Point entryPoint() {
        double x = ENTRY_X;
        double y = ENTRY_Y;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        int order = 13;
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.phare() && !rs.inPort();
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            mv.pathTo(entry, SensDeplacement.ARRIERE);
            mv.gotoOrientationDeg(rs.getTeam() == ETeam.BLEU ? 0 : 180);

            mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);
            rs.enableCalageBordure();
            mv.reculeMM(200);

            if (rs.getTeam() == ETeam.BLEU) {
                servos.brasGauchePhare(true);
            } else {
                servos.brasDroitPhare(true);
            }
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
            mv.avanceMM(200);

            rs.phare(true);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            completed = rs.phare();
            servos.brasDroitFerme(false);
            servos.brasGaucheFerme(false);
        }
    }
}
