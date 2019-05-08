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
import org.arig.robot.model.EStrategy;
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
public class DeposeAccelerateur extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private VentousesService ventouses;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Active l'accelerateur et dépose";
    }

    @Override
    public int order() {
        // 10 pour ouvrir l'accelerateur
        int points = (!rs.isAccelerateurOuvert() ? 10 + 20 + 24 : 0) +
                // 10 par palet
                (int) Math.min(IConstantesNerellConfig.nbPaletsAccelerateurMax - rs.getPaletsInAccelerateur().size(), rs.getRemainingTime() < 30 ? carousel.count(CouleurPalet.ANY) : carousel.count(CouleurPalet.ROUGE)) * 10;
        return points;

    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                (
                        !rs.isAccelerateurOuvert() ||
                                canDepose() ||
                                rs.strategyActive(EStrategy.PRISE_BLEU_ACCELERATEUR) && !rs.isAccelerateurPrit() && carousel.has(null)
                );
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

            int yAvantAvance = 1740;

            // va au point le plus proche
            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(1500 - 210 - IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            } else {
                mv.pathTo(1500 + 210 + IConstantesNerellConfig.dstAtomeCentre, yAvantAvance);
            }

            rs.disableAvoidance();

            ventouses.waitAvailable(side);

            if (rs.strategyActive(EStrategy.PRISE_BLEU_ACCELERATEUR)) {
                ventouses.preparePriseAccelerateur(side);
            } else {
                ventouses.prepareDeposeAccelerateur(side);
            }

            // oriente et avance à fond
            mv.gotoOrientationDeg(90);

            // 30 = epaisseur accelerateur
            mv.avanceMM(2000 - 30 - yAvantAvance - IConstantesNerellConfig.dstVentouseFacade);

            // prend ou pousse le bleu
            if (rs.strategyActive(EStrategy.PRISE_BLEU_ACCELERATEUR) && !rs.isAccelerateurPrit()) {
                if (ventouses.priseAccelerateur(side)) {
                    ventouses.stockageAsyncMaisResteEnHaut(side);
                    rs.setAccelerateurPrit(true);
                    ventouses.waitAvailable(side);

                } else if (!rs.isAccelerateurOuvert()) {
                    ventouses.pousseAccelerateur(side);
                    rs.setAccelerateurOuvert(true);
                }
            } else if (!rs.isAccelerateurOuvert() && !rs.isAccelerateurPrit()) {
                ventouses.pousseAccelerateur(side);
                rs.setAccelerateurOuvert(true);
            }

            // dépose
            while (canDepose()) {
                CouleurPalet couleur = carousel.has(CouleurPalet.ROUGE) ? CouleurPalet.ROUGE : CouleurPalet.ANY;

                ventouses.deposeAccelerateur(couleur, side);

                // cas ou a prit le bleu
                if (!rs.isAccelerateurOuvert()) {
                    rs.setAccelerateurOuvert(true);
                }
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
