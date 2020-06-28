package org.arig.robot.strategy.actions.active;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractPincesAvantService.Side;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBouee extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private TableUtils tableUtils;

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Getter
    private boolean completed = false;

    private final int numeroBouee;
    private Bouee bouee;

    @PostConstruct
    public void init() {
        bouee = rs.bouee(numeroBouee);
    }

    @Override
    public String name() {
        return "Bouee " + numeroBouee;
    }

    @Override
    protected Point entryPoint() {
        return bouee.pt();
    }

    @Override
    public int order() {
        return 1 + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !bouee.prise();
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(entry, GotoOption.AVANT);

            if (bouee.couleur() == ECouleurBouee.ROUGE) {
                pincesAvantService.setEnabled(true, true, false, false);
            } else {
                pincesAvantService.setEnabled(false, false, true, true);
            }
            rs.enablePincesAvant();

            // attente d'ouverture des servos
            ThreadUtils.sleep(IConstantesNerellConfig.i2cReadTimeMs * 3);

            if (bouee.couleur() == ECouleurBouee.ROUGE) {
                pincesAvantService.setExpected(Side.LEFT, ECouleurBouee.ROUGE, 2);
            } else {
                pincesAvantService.setExpected(Side.RIGHT, ECouleurBouee.VERT, 4);
            }

            mv.setVitesse(IConstantesNerellConfig.vitesseLente, IConstantesNerellConfig.vitesseOrientation);
            mv.avanceMM(100); // TODO Faire le vrai déplacement
            bouee.prise(true);

            completed = true;
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            rs.disablePincesAvant();
        }
    }
}
