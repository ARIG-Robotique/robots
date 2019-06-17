package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IVentousesService;
import org.arig.robot.services.SerrageService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerGoldeniumTable extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IVentousesService ventouses;

    @Autowired
    private SerrageService serrageService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déposer le goldenium sur la table";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                ventouses.getCouleur(rs.mainSide()) == CouleurPalet.GOLD &&
                rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax;
    }

    @Override
    public void execute() {
        ESide side = rs.mainSide();

        try {

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            // va au point le plus proche (zone bleu)
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2700, 950);
                mv.gotoOrientationDeg(0);
            } else {
                mv.pathTo(300, 950);
                mv.gotoOrientationDeg(180);
            }

            rs.disableAvoidance();

            mv.avanceMM(100);

            ventouses.deposeGoldeniumTable(side);

            mv.reculeMM(100);
            mv.gotoOrientationDeg(rs.getTeam() == Team.VIOLET ? 180 : 0);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            ventouses.finishDepose(side);
        }
    }

}
