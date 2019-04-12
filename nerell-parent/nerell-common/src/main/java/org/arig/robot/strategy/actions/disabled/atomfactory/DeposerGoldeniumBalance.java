package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Palet;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.PincesService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerGoldeniumBalance extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private PincesService pincesService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déposer le goldenium dans la balance";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return rs.getGoldeniumInPince() != null && rs.getPaletsInBalance().size() < IConstantesNerellConfig.nbPaletsBalanceMax;
    }

    @Override
    public void execute() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            // va au point le plus proche
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(1700, 600);
            } else {
                mv.pathTo(1300, 600);
            }

            rs.disableAvoidance();

            pincesService.deposeBalance(Palet.Couleur.GOLD, side);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

}
