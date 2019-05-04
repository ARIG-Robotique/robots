package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Position;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeAccelerateur extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private ConvertionRobotUnit conv;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Active l'accelerateur et dépose";
    }

    @Override
    public int order() {
        return 0; // TODO
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                (!rs.isAccelerateurOuvert() || canDepose());
    }

    private boolean canDepose() {
        return rs.getPaletsInAccelerateur().size() < IConstantesNerellConfig.nbPaletsAccelerateurMax &&
                (
                        carousel.has(CouleurPalet.ROUGE) ||
                                carousel.has(CouleurPalet.ANY) && rs.getRemainingTime() < 30
                );
    }

    @Override
    public void execute() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(1340, 1740);
            } else {
                mv.pathTo(1760, 1740);
            }

            rs.disableAvoidance();

            ventouses.waitAvailable(side);

            ventouses.prepareDeposeAccelerateur(side);

            // oriente et avance à fond
            mv.gotoOrientationDeg(90);
            mv.gotoPointMM(conv.pulseToMm(position.getPt().getX()), 2000 - IConstantesNerellConfig.dstVentouseFacade);

            // pousse le bleu
            if (!rs.isAccelerateurOuvert()) {
                ventouses.pousseAccelerateur(side);
                rs.setAccelerateurOuvert(true);
            }

            // dépose
            while (canDepose()) {
                CouleurPalet couleur = carousel.has(CouleurPalet.ROUGE) ? CouleurPalet.ROUGE : CouleurPalet.ANY;

                ventouses.deposeAccelerateur(couleur, side);
            }

            mv.reculeMM(50);

            completed = rs.getPaletsInAccelerateur().size() >= IConstantesNerellConfig.nbPaletsAccelerateurMax;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException | CarouselNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            ventouses.finishDeposeAccelerateurAsync(side);
        }
    }

}
