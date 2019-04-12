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
import org.arig.robot.services.SerrageService;
import org.arig.robot.services.ServosService;
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
    private ServosService servos;

    @Autowired
    private PincesService pincesService;

    @Autowired
    private SerrageService serrageService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déposer le goldenium dsur la table";
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isValid() {
        return rs.getGoldeniumInPince() != null && rs.getPaletsInBalance().size() >= IConstantesNerellConfig.nbPaletsBalanceMax;
    }

    @Override
    public void execute() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            // va au point le plus proche
            // TODO
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2800, 600);
            } else {
                mv.pathTo(200, 600);
            }

            rs.disableAvoidance();

            mv.gotoOrientationDeg(rs.getTeam() == Team.VIOLET ? 0 : 180);
            mv.avanceMM(150); // TODO

            serrageService.disable();

            pincesService.deposeTable(Palet.Couleur.GOLD, side);

            mv.reculeMM(150);
            mv.gotoOrientationDeg(rs.getTeam() == Team.VIOLET ? 180 : 0);

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            serrageService.enable();
        }
    }

}
