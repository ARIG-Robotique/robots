package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposeAccelerateurSansCarousel extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Active l'accelerateur et dépose";
    }

    @Override
    public int order() {
        if (!rs.isAccelerateurOuvert()) {
            return Integer.MAX_VALUE - 2;
        } else {
            // peu d'importe de couleur de palet
            ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

            return Math.min(IConstantesNerellConfig.nbPaletsAccelerateurMax - rs.getPaletsInAccelerateur().size(), ventouses.getCouleur(side) != null ? 1 : 0) * 10;
        }
    }

    @Override
    public boolean isValid() {
        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        return isTimeValid() &&
                (
                        ventouses.getCouleur(side) != null &&
                                rs.getPaletsInAccelerateur().size() < IConstantesNerellConfig.nbPaletsAccelerateurMax ||

                                !rs.isAccelerateurOuvert()
                );
    }

    @Override
    public void execute() {

        ESide side = rs.getTeam() == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;

        try {
            rs.enableAvoidance();

            int yAvantAvance = 1740;

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(1500 - 210 - IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            } else {
                mv.pathTo(1500 + 210 + IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            }

            rs.disableAvoidance();

            ventouses.waitAvailable(side);

            ventouses.prepareDeposeAccelerateur(side);

            // oriente et avance à fond
            mv.gotoOrientationDeg(90);

            // 30 = epaisseur accelerateur
            mv.avanceMM(2000 - 30 - yAvantAvance - IConstantesNerellConfig.dstVentouseFacade);

            // prend ou pousse le bleu
            if (!rs.isAccelerateurOuvert()) {
                ventouses.pousseAccelerateur(side);
                rs.setAccelerateurOuvert(true);
            }

            // dépose
            ventouses.deposeAccelerateur(CouleurPalet.ANY, side);

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
